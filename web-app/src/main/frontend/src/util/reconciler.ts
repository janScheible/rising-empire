import LoggerFactory from '~/util/logger/logger-factory';
import Logger from '~/util/logger/logger';
import ReconciliationOptions from '~/util/reconciliation-options';
import Renderable from '~/util/renderable';

export default class Reconciler {
	static #logger: Logger = LoggerFactory.get(`${import.meta.url}`);

	static toggleDebugLog() {
		const debug = Reconciler.#logger.getLevel() === 'DEBUG';
		Reconciler.#logger.setLevel(debug ? 'NONE' : 'DEBUG');
		return !debug;
	}

	static #getElementDescription(el) {
		const customElement = customElements.get(el.tagName.toLowerCase());
		return `"${el.id ? el.id : el.tagName.toLowerCase()}"${
			!customElement ? ' in ' + el.getRootNode().host?.tagName?.toLowerCase() : ''
		}`;
	}

	static reconcileAttribute(el, attr, value, predicateFn?) {
		if (
			(predicateFn && predicateFn(el.getAttribute(attr), value)) ||
			(!predicateFn && el.getAttribute(attr) != value)
		) {
			if (Reconciler.#logger.isDebugEnabled) {
				Reconciler.#logger.debug(
					`reconciled attribute "${attr}" of ${Reconciler.#getElementDescription(el)} to "${value}"`
				);
			}

			el.setAttribute(attr, value);
			return true;
		}

		return false;
	}

	static reconcileProperty(el, prop, value, predicateFn?) {
		if ((predicateFn && predicateFn(el[prop], value)) || (!predicateFn && el[prop] != value)) {
			if (Reconciler.#logger.isDebugEnabled) {
				Reconciler.#logger.debug(
					`reconciled property "${prop}" of ${Reconciler.#getElementDescription(el)} to "${value}"`
				);
			}

			el[prop] = value;
			return true;
		}

		return false;
	}

	static isHiddenAfterPropertyReconciliation(el, value, predicateFn?) {
		Reconciler.reconcileProperty(el, 'hidden', value, predicateFn);
		return el['hidden'];
	}

	static reconcileCssVariable(el, variable, value, predicateFn?) {
		const dashedVariable = '--' + variable;
		if (
			(predicateFn && predicateFn(el[dashedVariable], value)) ||
			(!predicateFn && el.style.getPropertyValue(dashedVariable) != value)
		) {
			if (Reconciler.#logger.isDebugEnabled) {
				Reconciler.#logger.debug(
					`reconciled CSS property "${dashedVariable}" of ${Reconciler.#getElementDescription(
						el
					)} to "${value}"`
				);
			}

			el.style.setProperty(dashedVariable, value);
			return true;
		}

		return false;
	}

	static reconcileStyle(el, prop, value, predicateFn?) {
		if ((predicateFn && predicateFn(el.style[prop], value)) || (!predicateFn && el.style[prop] != value)) {
			if (Reconciler.#logger.isDebugEnabled) {
				Reconciler.#logger.debug(
					`reconciled style "${prop}" of ${Reconciler.#getElementDescription(el)} to "${value}"`
				);
			}

			el.style[prop] = value;
			return true;
		}

		return false;
	}

	static reconcileClass(el, className, present) {
		let reconciled = false;

		if (present && !el.classList.contains(className)) {
			el.classList.add(className);
			reconciled = true;
		} else if (!present && el.classList.contains(className)) {
			el.classList.remove(className);
			reconciled = true;
		}

		if (reconciled) {
			if (Reconciler.#logger.isDebugEnabled) {
				Reconciler.#logger.debug(
					`reconciled class "${className}" of ${Reconciler.#getElementDescription(el)} to ${
						present ? 'present' : 'absent'
					}`
				);
			}
		}
	}

	/**
	 * @param rootEl
	 * @param childEls
	 * @param dataArray
	 * @param  tagName (either a regular tag, svg tag (name starting with 'svg:') or a template reference (starting with '#', must be a child of rootEl))
	 * @param options
	 */
	static async reconcileChildren(
		rootEl: HTMLElement | ShadowRoot | SVGElement,
		childEls: NodeListOf<any> | Node[] | HTMLCollection,
		dataArray: Array<any>,
		tagName: string,
		options?: ReconciliationOptions
	) {
		const createElementFn = () => {
			if (tagName.startsWith('svg:')) {
				return document.createElementNS('http://www.w3.org/2000/svg', tagName.substring(4));
			} else if (tagName.startsWith('#')) {
				const template: HTMLTemplateElement = rootEl.querySelector(tagName);
				return (template.content.cloneNode(true) as HTMLElement).firstElementChild;
			} else {
				return document.createElement(tagName);
			}
		};
		const afterCreateFn = (el, data) => (options?.afterCreateCallbackFn?.(el, data), el);
		const beforeDeleteFn = (el) => (options?.beforeDeleteCallbackFn?.(el), el);
		const idAttributName = options?.idAttributName ?? 'id';

		const elMapping = (Array.isArray(childEls) ? childEls : [...childEls])
			.filter(
				(childEl) =>
					(childEl instanceof HTMLElement && !(childEl instanceof HTMLTemplateElement)) ||
					childEl instanceof SVGElement
			)
			.reduce((map, obj) => ((map[obj.getAttribute(idAttributName)] = obj), map), {});
		const existingIds = new Set(Object.keys(elMapping));

		const newIds = new Set<string>();
		const animations = [];
		for (const data of dataArray) {
			const id = options?.idValueFn?.(data) ?? data.id;
			newIds.add(id);
			const el: HTMLElement & Renderable = elMapping[id] ?? afterCreateFn(createElementFn(), data);
			if (!el.parentElement) {
				el.setAttribute(idAttributName, id);

				if (options?.insertionMode === 'PREPEND') {
					rootEl.prepend(el);
				} else {
					rootEl.appendChild(el);
				}
			}
			delete elMapping[id];

			if (options?.renderCallbackFn) {
				options.renderCallbackFn(el, data);
			} else if (el.render) {
				animations.push(el.render(data));
			} else {
				throw new Error('Element neither has a render method nor is renderCallbackFn defined!');
			}
		}

		if (Reconciler.#logger.isDebugEnabled()) {
			const addedIds = new Set();
			const updatedIds = new Set();
			const removedIds = new Set();

			for (const existingId of existingIds) {
				if (newIds.has(existingId)) {
					updatedIds.add(existingId);
				} else {
					removedIds.add(existingId);
				}
			}
			for (const newId of newIds) {
				if (!existingIds.has(newId)) {
					addedIds.add(newId);
				}
			}

			if (Reconciler.#logger.isDebugEnabled) {
				Reconciler.#logger.debug(
					`reconciled children of ${Reconciler.#getElementDescription(rootEl)} ` +
						`by adding [${[...addedIds].join(',')}], updating [${[...updatedIds].join(',')}] and ` +
						`removing [${[...removedIds].join(',')}]`
				);
			}
		}

		Object.keys(elMapping).forEach((id) => rootEl.removeChild(beforeDeleteFn(elMapping[id])));

		return Promise.all(animations);
	}
}
