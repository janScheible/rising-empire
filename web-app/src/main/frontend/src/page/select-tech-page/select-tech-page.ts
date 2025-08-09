import ModalDialog from '~/component/modal-dialog';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import Container from '~/component/container';
import ContainerTtile from '~/component/container-title';
import ContainerButtons from '~/component/container-buttons';
import FlowLayout from '~/component/flow-layout';
import ListBox from '~/component/list-box';
import cssUrl from '~/util/cssUrl';

export default class SelectTechPage extends HTMLElement {
	static NAME = 're-select-tech-page';

	#selectButtonEl: HTMLButtonElement;

	#researchedNameEl: HTMLDivElement;
	#researchedDescriptionEl: HTMLDivElement;

	#categoryEl: HTMLSpanElement;

	#technologiesEl: HTMLElement & ListBox;

	#selectTechActions;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				/* A native component should do that out-of-the box. */
				${ListBox.NAME} {
					background-color: white;
				}

				#technologies {
					max-width: 450px;
					min-height: 200px;
				}

				.technology-expense-wrapper {
					text-align: right;
				}
			</style>
			<${ModalDialog.NAME}>
				<${Container.NAME} border>
					<${ContainerTtile.NAME}>Select next technology</${ContainerTtile.NAME}>

					<${FlowLayout.NAME} direction="column">
						<div><span id="researched-name" class="bold"></span> is now available.</div>
						<div id="researched-description"></div>
					</${FlowLayout.NAME}>

					<${FlowLayout.NAME} direction="column">
						<div>Select next <span id="category"></span> technology our reseachers should focus on.</div>
						<${ListBox.NAME} id="technologies">
							<template id="technology-template">
								<div>
									<${FlowLayout.NAME}>
										<div class="bold technology-name"></div>
										<div data-flow-size="1fr" class="bold technology-expense-wrapper"><span class="technology-expense"></span> RP</div>
									</${FlowLayout.NAME}>
									<div class="technology-description"></div>
								</div>
							</template>							
						</${ListBox.NAME}>
					</${FlowLayout.NAME}>

					<${ContainerButtons.NAME}><button id="select-button">Select</button></${ContainerButtons.NAME}>
				</${Container.NAME}>				
			</${ModalDialog.NAME}>`;

		this.#selectButtonEl = this.shadowRoot.querySelector('#select-button');
		this.#selectButtonEl.addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#selectTechActions[this.#technologiesEl.selectedIndex], {});
		});

		this.#researchedNameEl = this.shadowRoot.querySelector('#researched-name');
		this.#researchedDescriptionEl = this.shadowRoot.querySelector('#researched-description');

		this.#categoryEl = this.shadowRoot.querySelector('#category');

		this.#technologiesEl = this.shadowRoot.querySelector('#technologies');
		this.#technologiesEl.addEventListener('dblclick', (e) => {
			if (this.#technologiesEl.selectedIndex >= 0) {
				HypermediaUtil.submitAction(this.#selectTechActions[this.#technologiesEl.selectedIndex], {});
			}
		});
		this.#technologiesEl.addEventListener('currentindexchange', (e) => {
			this.#selectButtonEl.disabled = this.#technologiesEl.selectedIndex < 0;
		});
	}

	render(data) {
		this.#selectTechActions = data.techs.map((tech) => HypermediaUtil.getAction(tech, 'select'));

		Reconciler.reconcileProperty(this.#researchedNameEl, 'innerText', data.researchedTech.name);
		Reconciler.reconcileProperty(this.#researchedDescriptionEl, 'innerText', data.researchedTech.description);

		Reconciler.reconcileProperty(this.#categoryEl, 'innerText', data.category);

		Reconciler.reconcileChildren(
			this.#technologiesEl,
			this.#technologiesEl.querySelectorAll(':scope > div'),
			data.techs,
			'#technology-template',
			{
				renderCallbackFn: (technologyEl: HTMLDivElement, technology) => {
					Reconciler.reconcileProperty(
						technologyEl.querySelector('.technology-name'),
						'innerText',
						technology.name
					);
					Reconciler.reconcileProperty(
						technologyEl.querySelector('.technology-expense'),
						'innerText',
						technology.expense
					);
					Reconciler.reconcileProperty(
						technologyEl.querySelector('.technology-description'),
						'innerText',
						technology.description
					);
				},
				idAttributName: 'data-tech-id',
				idValueFn: (data) => data.id.split('@')[0], // strip tech level to have unique ids
			}
		);

		this.#technologiesEl.selectedIndex = 0;
		this.#selectButtonEl.disabled = false;
	}
}

customElements.define(SelectTechPage.NAME, SelectTechPage);
