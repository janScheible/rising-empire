import cssUrl from '~/util/cssUrl';

/**
 * Minimal list box implementation with arbitrary list items (e.g. multiline items).
 *
 * @element re-list-box
 *
 * @attr selectedIndex - The selected item index (-1 if non is selected).
 */
export default class ListBox extends HTMLElement {
	static NAME = 're-list-box';

	#slot: HTMLSlotElement;

	#selectedIndex = -1;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;
					
					overflow: hidden;
					
					background-color: Field;
					color: FieldText;

					border: 1px solid #8F8F9D;
				}

				#container {
					flex: 1;

					display: block;
				}

				::slotted(*) {
					padding: 0.25em;
				}

				::slotted([selected]) {
					background: highlight;
					color: highlighttext;
				}
			</style>
			<div id="container">
				<slot></slot>
			</div>`;

		this.#slot = this.shadowRoot.querySelector('slot');

		this.addEventListener('click', (e) => {
			let prevPathEl = undefined;
			for (const pathEl of e.composedPath()) {
				if (pathEl === this.#slot) {
					break;
				}
				prevPathEl = pathEl;
			}

			const listItemEl: HTMLElement = prevPathEl;
			const listItemEls = this.#getListItemEls();

			listItemEls.forEach((slottedEl) =>
				slottedEl !== listItemEl
					? slottedEl.removeAttribute('selected')
					: slottedEl.setAttribute('selected', '')
			);

			const prevSelectedIndex = this.#selectedIndex;
			if (e.target === this) {
				this.#selectedIndex = -1;
			} else {
				this.#selectedIndex = listItemEls.indexOf(listItemEl);
			}

			if (prevSelectedIndex !== this.#selectedIndex) {
				this.dispatchEvent(new CustomEvent('currentindexchange', { detail: { index: this.#selectedIndex } }));
			}
		});
	}

	#getListItemEls() {
		return this.#slot.assignedElements().filter((slottedEl) => !(slottedEl instanceof HTMLTemplateElement));
	}

	get selectedIndex() {
		return this.#selectedIndex;
	}

	set selectedIndex(selectedIndex) {
		this.#selectedIndex = selectedIndex;

		const listItemEls = this.#getListItemEls();
		for (let i = 0; i < listItemEls.length; i++) {
			i === selectedIndex
				? listItemEls[i].setAttribute('selected', '')
				: listItemEls[i].removeAttribute('selected');
		}
	}
}

customElements.define(ListBox.NAME, ListBox);
