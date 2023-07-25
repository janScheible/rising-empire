import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Colony extends HTMLElement {
	static NAME = 're-colony';

	#wrapperEl: FlowLayout;

	#colonyEl: FlowLayout;
	#colonyRaceEl: HTMLSpanElement;

	#figuresEl: GridLayout;
	#productionEl: FlowLayout;

	#population: HTMLDivElement;
	#basesEl: HTMLDivElement;
	#netProductionEl: HTMLSpanElement;
	#grossProductionEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#net-production {
					color: yellow;
				}

				#gross-production {
					color: forestgreen;
				}
			</style>
			<${FlowLayout.NAME} id="wrapper" direction="column" gap="XL">
				<${FlowLayout.NAME} id="colony" axis-align="center">
					<span id="colony-race"></span>&nbsp;colony
				</${FlowLayout.NAME}>

				<${GridLayout.NAME} id="figures" cols="2" border>
					<${FlowLayout.NAME}>
						<div data-flow-size="1fr">Pop</div>
						<div id="population"></div>
					</${FlowLayout.NAME}>

					<${FlowLayout.NAME}>
						<div data-flow-size="1fr">Bases</div>
						<div id="bases"></div>
					</${FlowLayout.NAME}>

					<${FlowLayout.NAME} id="production" data-col-span="2">
						<div data-flow-size="1fr">Production</div>
						<span id="net-production" class="bold"></span>&nbsp;<span id="gross-production" class="bold">()</span>
					</${FlowLayout.NAME}>
				</${GridLayout.NAME}>
			</${FlowLayout.NAME}>`;

		this.#wrapperEl = this.shadowRoot.querySelector('#wrapper');

		this.#colonyEl = this.shadowRoot.querySelector('#colony');
		this.#colonyRaceEl = this.shadowRoot.querySelector('#colony-race');

		this.#figuresEl = this.shadowRoot.querySelector('#figures');
		this.#productionEl = this.shadowRoot.querySelector('#production');

		this.#population = this.shadowRoot.querySelector('#population');
		this.#basesEl = this.shadowRoot.querySelector('#bases');
		this.#netProductionEl = this.shadowRoot.querySelector('#net-production');
		this.#grossProductionEl = this.shadowRoot.querySelector('#gross-production');
	}

	render(data) {
		if (Reconciler.reconcileProperty(this.#colonyEl, 'hidden', data && !data.race)) {
			this.#wrapperEl.updateMargins();
		}
		if (!this.#colonyEl.hidden) {
			Reconciler.reconcileClass(this.#colonyRaceEl, 'bold', data && data.race);
			Reconciler.reconcileProperty(this.#colonyRaceEl, 'innerText', data && data.race ? data.race : 'No');
			Reconciler.reconcileStyle(
				this.#colonyRaceEl,
				'color',
				data && data.playerColor ? `var(--${data.playerColor}-player-color)` : ''
			);
		}

		if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#figuresEl, !data)) {
			Reconciler.reconcileProperty(this.#population, 'innerText', data.population);
			Reconciler.reconcileProperty(
				this.#basesEl,
				'innerText',
				data.bases,
				(oldValue, newValue) => oldValue !== newValue
			);

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#productionEl, !data.production)) {
				Reconciler.reconcileProperty(this.#netProductionEl, 'innerText', data.production.net);
				Reconciler.reconcileProperty(this.#grossProductionEl, 'innerText', '(' + data.production.gross + ')');
			}
		}
	}
}

customElements.define(Colony.NAME, Colony);
