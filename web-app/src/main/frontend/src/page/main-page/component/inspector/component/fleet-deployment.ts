import Reconciler from '~/util/reconciler';
import HypermediaUtil from '~/util/hypermedia-util';
import GridLayout from '~/component/grid-layout';
import Ships from '~/component/ships';
import Stepper from '~/component/stepper';
import FlowLayout from '~/component/flow-layout';
import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import cssUrl from '~/util/cssUrl';

export default class FleetDeployment extends HTMLElement {
	static NAME = 're-fleet-deployment';

	#etaTextEl: HTMLSpanElement;
	#etaEl: HTMLSpanElement;
	#outOfRangeByTextEl: HTMLSpanElement;
	#outOfRangeByEl: HTMLSpanElement;

	#cancelButtonEl: HTMLButtonElement;
	#deployButtonEl: HTMLButtonElement;

	#shipsEls;

	#deployAction;
	#cancelAction;
	#assignShipsAction;

	#ships;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#eta-text, #out-of-range-by-text {
					text-align: center;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${FlowLayout.NAME} direction="column">
					${FleetDeployment.#shipSlots(
						(index) => `
						<${GridLayout.NAME} cols="min-content 1fr" rows="1fr min-content" border>
							<div data-row-span="2" data-no-padding>
								<${Ships.NAME} id="ships-${index}"></${Ships.NAME}>
							</div>
							
							<${FlowLayout.NAME} axis-align="center" cross-axis-align="center">
								<div id="ship-name-${index}"></div>
							</${FlowLayout.NAME}>

							<div><${Stepper.NAME} id="stepper-${index}"></${Stepper.NAME}></div>
						</${GridLayout.NAME}>`
					)}
				</${FlowLayout.NAME}>

				<div id="eta-text">
					ETA <span id="eta"></span> turns
				</div>
				<div id="out-of-range-by-text">
					Destination is out of range, <span id="out-of-range-by"></span> parsecs from closest colony
				</div>

				<${ContainerButtons.NAME} fill-horizontally>
					<button id="cancel-button">Cancel</button>
					<button id="deploy-button" disabled>Deploy</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		(this.#shipsEls = Array(6)
			.fill('')
			.map(
				(_, i) =>
					({
						shipsEl: this.shadowRoot.querySelector('#ships-' + i),
						shipNameEl: this.shadowRoot.querySelector('#ship-name-' + i),
						stepperEl: this.shadowRoot.querySelector('#stepper-' + i),
					} as {
						shipsEl: Ships;
						shipNameEl: HTMLDivElement;
						stepperEl: Stepper;
					})
			)).forEach(({ stepperEl }, index) => {
			stepperEl.addEventListener('change', (e: CustomEvent) => {
				HypermediaUtil.submitAction(this.#assignShipsAction, {
					[this.#ships[index].id]: e.detail.value,
				});
			});
		});

		this.#etaTextEl = this.shadowRoot.querySelector('#eta-text');
		this.#etaEl = this.shadowRoot.querySelector('#eta');
		this.#outOfRangeByTextEl = this.shadowRoot.querySelector('#out-of-range-by-text');
		this.#outOfRangeByEl = this.shadowRoot.querySelector('#out-of-range-by');

		this.#cancelButtonEl = this.shadowRoot.querySelector('#cancel-button');
		this.#cancelButtonEl.addEventListener('click', (e) => {
			if (this.#cancelAction) {
				HypermediaUtil.submitAction(this.#cancelAction, {});
			}
		});

		this.#deployButtonEl = this.shadowRoot.querySelector('#deploy-button');
		this.shadowRoot.querySelector('#deploy-button').addEventListener('click', (e) => {
			if (this.#deployAction) {
				HypermediaUtil.submitAction(this.#deployAction, {});
			}
		});
	}

	static #shipSlots(template) {
		return Array(6)
			.fill(template)
			.map((template, index) => template(index))
			.join('');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#ships = data.ships;

			this.#deployAction = HypermediaUtil.getAction(data, 'deploy');
			this.#cancelAction = HypermediaUtil.getAction(data, 'cancel');
			this.#assignShipsAction = HypermediaUtil.getAction(data, 'assign-ships');

			for (let i = 0; i < 6; i++) {
				const ships = data.ships[i];
				const { shipsEl, shipNameEl, stepperEl } = this.#shipsEls[i];

				Reconciler.reconcileStyle(shipsEl, 'visibility', ships ? 'visible' : 'hidden');
				Reconciler.reconcileProperty(shipNameEl, 'innerText', ships ? ships.name : '');
				Reconciler.reconcileStyle(stepperEl, 'visibility', ships ? 'visible' : 'hidden');
				if (ships) {
					shipsEl.render({ playerColor: data.playerColor, count: ships.count, size: ships.size });

					Reconciler.reconcileProperty(stepperEl, 'max', ships.maxCount);
					Reconciler.reconcileProperty(stepperEl, 'value', ships.count);
				}
			}

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#etaTextEl, !data.eta)) {
				Reconciler.reconcileProperty(this.#etaEl, 'innerText', data.eta);
			}

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#outOfRangeByTextEl, !data.outOfRangeBy)) {
				Reconciler.reconcileProperty(this.#outOfRangeByEl, 'innerText', data.outOfRangeBy);
			}

			const actionPossible = this.#deployAction || this.#cancelAction;
			Reconciler.reconcileProperty(this.#deployButtonEl, 'hidden', !actionPossible);
			Reconciler.reconcileProperty(this.#cancelButtonEl, 'hidden', !actionPossible);

			Reconciler.reconcileProperty(this.#deployButtonEl, 'disabled', !this.#deployAction);
		}
	}
}

customElements.define(FleetDeployment.NAME, FleetDeployment);
