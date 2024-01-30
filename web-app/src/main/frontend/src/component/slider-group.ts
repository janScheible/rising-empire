import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import SliderGroupCategory from '~/component/silder-group-category';
import Slider from '~/component/slider';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class SliderGroup extends HTMLElement {
	static NAME = 're-slider-group';

	#containerEl: GridLayout;
	#categoryTemplateEl: HTMLTemplateElement;
	#categoryEls;

	#selectAction;

	#categories;
	#locked: boolean;

	#idLockedMapping = {};

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				.locked {
					color: darkred;

					/* making the text bold would increase it's width... */
					-webkit-text-stroke-width: 0.5px;
					-webkit-text-stroke-color: darkred;
				}
			</style>
			<slot></slot>
			<${GridLayout.NAME} id="container" cols="min-content 3fr 1fr" gap="L">
				<template id="category-template">
					<${FlowLayout.NAME} class="name category" cross-axis-align="center"></${FlowLayout.NAME}>
					<${Slider.NAME} class="value category" data-flow-size="1fr" value="0"></${Slider.NAME}>
					<${FlowLayout.NAME} class="status category" axis-align="end" cross-axis-align="center">-</${FlowLayout.NAME}>
				</template>
			</${GridLayout.NAME}>`;

		this.#containerEl = this.shadowRoot.querySelector('#container');
		this.#categoryTemplateEl = this.shadowRoot.querySelector('#category-template');

		this.shadowRoot.querySelector('slot').addEventListener('slotchange', (e) => {
			Array.from(this.#containerEl.children).forEach((childEl) => childEl.remove());

			this.#categoryEls = {};

			for (const el of (e.target as HTMLSlotElement).assignedElements() as HTMLElement[]) {
				if (el instanceof SliderGroupCategory) {
					const name = el.innerHTML;
					const category = el.qualifier;

					this.#instantiateAndAppendTemplate(category);

					const nameEl: FlowLayout = this.#containerEl.querySelector(`[data-category="${category}"].name`);
					nameEl.innerText = name;
					nameEl.addEventListener('click', (e) => {
						const lockingCategory = (e.target as HTMLElement).dataset.category;
						const groupId = this.getAttribute('data-id');
						this.#idLockedMapping[groupId] = this.#lock(lockingCategory, true);
					});

					const valueEl: Slider = this.#containerEl.querySelector(`[data-category="${category}"].value`);
					valueEl.addEventListener('change', (e: CustomEvent) => {
						const category = (e.target as HTMLElement).dataset.category;

						const lockedCategoryNameEl: HTMLElement =
							this.#containerEl.querySelector('.name.category.locked');
						const lockedCategory = lockedCategoryNameEl?.dataset.category;

						if (category !== lockedCategory) {
							const values = {
								[category]: e.detail.value,
							};
							if (lockedCategory) {
								values.locked = lockedCategory;
							}
							HypermediaUtil.submitAction(this.#selectAction, values);
						}
					});

					const statusEl: FlowLayout = this.#containerEl.querySelector(
						`[data-category="${category}"].status`
					);

					this.#categoryEls[category] = { valueEl, statusEl };
				}
			}

			if (this.#categories) {
				this.#renderCategories(this.#categories);
			}
			this.#lock(this.#locked, false);
		});
	}

	#instantiateAndAppendTemplate(category) {
		const categoryFragmentEl: HTMLElement = this.#categoryTemplateEl.content.cloneNode(true) as HTMLElement;

		Array.from(categoryFragmentEl.querySelectorAll('.category')).forEach(
			(categoryEl: HTMLElement) => (categoryEl.dataset.category = category)
		);

		this.#containerEl.appendChild(categoryFragmentEl);
	}

	render(data) {
		this.#selectAction = HypermediaUtil.getAction(data, this.getAttribute('select-action'));

		Reconciler.reconcileAttribute(this, 'data-id', data.id);
		this.#renderCategories((this.#categories = data.categories));

		const groupId = data.id;
		if (data.locked && !Object.keys(this.#idLockedMapping).includes(groupId)) {
			this.#idLockedMapping[groupId] = data.locked;
		}
		this.#lock((this.#locked = this.#idLockedMapping[groupId]));
	}

	#renderCategories(categories) {
		for (const category of Object.getOwnPropertyNames(categories)) {
			if (this.#categoryEls?.[category]) {
				const valueEl = this.#categoryEls[category].valueEl;
				const statusEl = this.#categoryEls[category].statusEl;

				// could be that render(data) is called before slotchange of slot is fired
				if (valueEl && statusEl) {
					Reconciler.reconcileProperty(valueEl, 'value', categories[category].value);
					Reconciler.reconcileProperty(statusEl, 'innerText', categories[category].status);
				}
			}
		}
	}

	#lock(lockingCategory, toggle?) {
		let lockedCategory;

		Array.from(this.#containerEl.querySelectorAll('.name.category')).forEach((nameEl: HTMLElement) => {
			const locked =
				nameEl.dataset.category === lockingCategory && (!toggle || !nameEl.classList.contains('locked'));
			if (locked) {
				lockedCategory = lockingCategory;
			}

			Reconciler.reconcileClass(nameEl, 'locked', locked);
			Reconciler.reconcileProperty(this.#categoryEls[nameEl.dataset.category].valueEl, 'locked', locked);
		});

		return lockedCategory;
	}
}

customElements.define(SliderGroup.NAME, SliderGroup);
