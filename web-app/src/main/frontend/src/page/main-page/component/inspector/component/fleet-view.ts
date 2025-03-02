import Container from '~/component/container';
import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import Ships from '~/component/ships';
import Theme from '~/theme/theme';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class FleetView extends HTMLElement {
	static NAME = 're-fleet-view';

	#raceEl: HTMLSpanElement;

	#shipsEls;

	#etaTextEl: HTMLSpanElement;
	#etaEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#eta-text {
					text-align: center;
				}

				#ship-wrapper.present {
					background-color: black !important;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${FlowLayout.NAME} direction="column">
					<div>
						<span><span id="race" class="bold"></span> fleet</span><br>
					</div>

					<${GridLayout.NAME} cols="2">
						${FleetView.#shipSlots(
							(index) => `
							<${GridLayout.NAME} data-flow-size="1fr" cols="1" border>
								<${FlowLayout.NAME} id="ship-wrapper" data-no-padding axis-align="center">
									<${Ships.NAME} id="ships-${index}"></${Ships.NAME}>
								</${FlowLayout.NAME}>
								<${FlowLayout.NAME} axis-align="center">
									<div id="ship-name-${index}">&nbsp;</div>
								</${FlowLayout.NAME}>
							</${GridLayout.NAME}>`
						)}
					</${GridLayout.NAME}>
				</${FlowLayout.NAME}>

				<div id="eta-text">
					ETA <span id="eta"></span> rounds
				</div>
			</${Container.NAME}>`;

		this.#raceEl = this.shadowRoot.querySelector('#race');

		this.#shipsEls = Array(6)
			.fill('')
			.map(
				(_, i) =>
					({
						shipsEl: this.shadowRoot.querySelector('#ships-' + i),
						shipNameEl: this.shadowRoot.querySelector('#ship-name-' + i),
					} as { shipsEl: Ships; shipNameEl: HTMLDivElement })
			);

		this.#etaTextEl = this.shadowRoot.querySelector('#eta-text');
		this.#etaEl = this.shadowRoot.querySelector('#eta');
	}

	static #shipSlots(template) {
		return Array(6)
			.fill(template)
			.map((template, index) => template(index))
			.join('');
	}

	connectedCallback() {
		// for storybook animation can't be enabled because it would cause screenshot test comparison errors
		if (!this.hasAttribute('no-animated-background')) {
			this.#shipsEls.forEach(({ shipsEl }) => shipsEl.setAttribute('animated-background', ''));
		}
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			Reconciler.reconcileProperty(this.#raceEl, 'innerText', Theme.getRace(data.race));
			Reconciler.reconcileStyle(this.#raceEl, 'color', `var(--${data.playerColor}-player-color)`);

			for (let i = 0; i < 6; i++) {
				const ships = data.ships[i];
				const { shipsEl, shipNameEl } = this.#shipsEls[i];

				Reconciler.reconcileClass(shipsEl.parentElement, 'present', ships);
				Reconciler.reconcileStyle(shipsEl, 'visibility', ships ? 'visible' : 'hidden');
				Reconciler.reconcileProperty(
					shipNameEl,
					ships ? 'innerText' : 'innerHTML',
					ships ? ships.name : '&nbsp;'
				);
				if (ships) {
					shipsEl.render({ playerColor: data.playerColor, count: ships.count, size: ships.size });
				}
			}

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#etaTextEl, !data.eta)) {
				Reconciler.reconcileProperty(this.#etaEl, 'innerText', data.eta);
			}
		}
	}
}

customElements.define(FleetView.NAME, FleetView);
