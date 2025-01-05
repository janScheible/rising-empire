import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import Theme from '~/theme/theme';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Colony extends HTMLElement {
	static NAME = 're-colony';

	#wrapperEl: FlowLayout;

	#colonyEl: FlowLayout;
	#colonyRaceEl: HTMLSpanElement;
	#outdatedColonyEl: HTMLSpanElement;

	#figuresEl: GridLayout;
	#productionEl: FlowLayout;

	#population: HTMLDivElement;
	#basesEl: HTMLDivElement;
	#netProductionEl: HTMLSpanElement;
	#grossProductionEl: HTMLSpanElement;

	#siegeProgressEl: HTMLDivElement;
	#roundsUntilAnnexableEl: HTMLDivElement;
	#ownColonySiegedEl: HTMLDivElement;
	#siegeRaceEl: HTMLDivElement;
	#foreigenColonySiegedEl: HTMLDivElement;

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

				#siege-progress {
					font-weight: bold;
					text-align: center;
				}
			</style>
			<${FlowLayout.NAME} id="wrapper" direction="column" gap="XL">
				<${FlowLayout.NAME} id="colony" axis-align="center">
					<span id="outdated-colony">Last reported as a</span><span id="colony-race"></span>&nbsp;colony
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

				<div id="siege-progress" data-cross-axis-align="center">
					Annexation possible <span id="rounds-until-annexable"></span><!--
					--><span id="own-colony-sieged"> by the <span id="siege-race"></span>!</span><!--
					--><span id="foreigen-colony-sieged">.</span>
				</div>
			</${FlowLayout.NAME}>`;

		this.#wrapperEl = this.shadowRoot.querySelector('#wrapper');

		this.#colonyEl = this.shadowRoot.querySelector('#colony');
		this.#colonyRaceEl = this.shadowRoot.querySelector('#colony-race');
		this.#outdatedColonyEl = this.shadowRoot.querySelector('#outdated-colony');

		this.#figuresEl = this.shadowRoot.querySelector('#figures');
		this.#productionEl = this.shadowRoot.querySelector('#production');

		this.#population = this.shadowRoot.querySelector('#population');
		this.#basesEl = this.shadowRoot.querySelector('#bases');
		this.#netProductionEl = this.shadowRoot.querySelector('#net-production');
		this.#grossProductionEl = this.shadowRoot.querySelector('#gross-production');

		this.#siegeProgressEl = this.shadowRoot.querySelector('#siege-progress');
		this.#roundsUntilAnnexableEl = this.shadowRoot.querySelector('#rounds-until-annexable');
		this.#ownColonySiegedEl = this.shadowRoot.querySelector('#own-colony-sieged');
		this.#siegeRaceEl = this.shadowRoot.querySelector('#siege-race');
		this.#foreigenColonySiegedEl = this.shadowRoot.querySelector('#foreigen-colony-sieged');
	}

	render(data) {
		const visibilities = [
			this.#colonyEl.hidden,
			this.#figuresEl.hidden,
			this.#siegeProgressEl.hidden,
			this.#ownColonySiegedEl.hidden,
		];

		if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#colonyEl, data && !data.race)) {
			Reconciler.reconcileClass(this.#colonyRaceEl, 'bold', data && data.race);
			Reconciler.reconcileProperty(
				this.#colonyRaceEl,
				'innerText',
				data && data.race ? Theme.getRace(data.race) : 'No'
			);
			Reconciler.reconcileStyle(
				this.#colonyRaceEl,
				'color',
				data && data.playerColor ? `var(--${data.playerColor}-player-color)` : ''
			);
			Reconciler.reconcileStyle(
				this.#outdatedColonyEl,
				'display',
				data && data.outdated === true ? 'unset' : 'none'
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

		if (
			!Reconciler.isHiddenAfterPropertyReconciliation(
				this.#siegeProgressEl,
				!(data && data.roundsUntilAnnexable >= 0)
			)
		) {
			Reconciler.reconcileProperty(
				this.#roundsUntilAnnexableEl,
				'innerText',
				data.roundsUntilAnnexable > 0 ? `in ${data.roundsUntilAnnexable} rounds` : 'now'
			);

			const foreigenSiegePresent = data.siegePlayerColor && data.siegeRace;
			Reconciler.reconcileProperty(this.#foreigenColonySiegedEl, 'hidden', foreigenSiegePresent);
			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#ownColonySiegedEl, !foreigenSiegePresent)) {
				Reconciler.reconcileProperty(this.#siegeRaceEl, 'innerText', Theme.getRace(data.siegeRace));
				Reconciler.reconcileStyle(this.#siegeRaceEl, 'color', `var(--${data.siegePlayerColor}-player-color)`);
			}
		}

		if (
			![
				this.#colonyEl.hidden,
				this.#figuresEl.hidden,
				this.#siegeProgressEl.hidden,
				this.#ownColonySiegedEl.hidden,
			].every((val, i) => val === visibilities[i])
		) {
			this.#wrapperEl.updateMargins();
		}
	}
}

customElements.define(Colony.NAME, Colony);
