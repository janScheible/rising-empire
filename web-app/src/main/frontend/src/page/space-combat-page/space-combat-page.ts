import Container from '~/component/container';
import GridLayout from '~/component/grid-layout';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import ModalDialog from '~/component/modal-dialog';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import ShipSpecs from '~/page/space-combat-page/component/ship-specs';
import FlowLayout from '~/component/flow-layout';
import LoggerFactory from '~/util/logger/logger-factory';
import Logger from '~/util/logger/logger';
import Outcome from '~/page/space-combat-page/component/outcome';
import cssUrl from '~/util/cssUrl';
import Theme from '~/theme/theme';

export default class SpaceCombatPage extends HTMLElement {
	static NAME = 're-space-combat-page';

	static get observedAttributes() {
		return ['hidden'];
	}

	static #logger: Logger = LoggerFactory.get(`${import.meta.url}`);

	#attackerEls: HTMLSpanElement[];
	#defenderEls: HTMLSpanElement[];

	#systemNameEl: HTMLSpanElement;
	#attackerShipSpecsEl: FlowLayout;
	#defenderShipSpecsEl: FlowLayout;
	#outcomeEl: Outcome;
	#fireExchangeEl: HTMLSpanElement;
	#buttonEl: HTMLButtonElement;

	/**
	 * Used to check if the current render invocation should continue with animating the fire exchanges.
	 */
	#combatAnimationToken: number = 0;
	#data;

	#continueAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>
			<${ModalDialog.NAME}>
				<${Container.NAME} border>
					<${ContainerTtile.NAME}>
						<span class="attacker"></span> attack <span class="defender"></span> at <span id="system-name"></span>
					</${ContainerTtile.NAME}>

					<${GridLayout.NAME} cols="2" col-gap="XL">
						<${FlowLayout.NAME} id="attacker-ship-specs" direction="column">
							<div><span class="attacker"></span> fleet</div>
						</${FlowLayout.NAME}>

						<${FlowLayout.NAME} id="defender-ship-specs" direction="column">
							<div><span class="defender"></span> fleet</div>
						</${FlowLayout.NAME}>
					</${GridLayout.NAME}>

					<${GridLayout.NAME} cols="1" row-align="center">
						<${Outcome.NAME} style="grid-area: 1 / 1;"></${Outcome.NAME}>
						<${FlowLayout.NAME} style="grid-area: 1 / 1;" cross-axis-align="center">
							<span id="fire-exchange"></span>
						</${FlowLayout.NAME}>
						</div>
					</${GridLayout.NAME}>

					<${ContainerButtons.NAME}><button>Continue</button></${ContainerButtons.NAME}>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#attackerEls = Array.from(this.shadowRoot.querySelectorAll('.attacker'));
		this.#defenderEls = Array.from(this.shadowRoot.querySelectorAll('.defender'));

		this.#systemNameEl = this.shadowRoot.querySelector('#system-name');
		this.#attackerShipSpecsEl = this.shadowRoot.querySelector('#attacker-ship-specs');
		this.#defenderShipSpecsEl = this.shadowRoot.querySelector('#defender-ship-specs');
		this.#outcomeEl = this.shadowRoot.querySelector(Outcome.NAME);
		this.#fireExchangeEl = this.shadowRoot.querySelector('#fire-exchange');
		this.#buttonEl = this.shadowRoot.querySelector('button');

		this.#buttonEl.addEventListener('click', (e) => {
			if (this.#buttonEl.innerText.includes('Skip')) {
				Reconciler.reconcileProperty(this.#buttonEl, 'disabled', true);
				Reconciler.reconcileProperty(this.#buttonEl, 'innerText', 'Continue');

				this.#combatAnimationToken += 1;

				this.#renderShipSpecs(this.#data, this.#data.fireExchangeCount - 1, true);

				this.#renderOutcome(this.#data);
			} else {
				HypermediaUtil.submitAction(this.#continueAction, {});
			}
		});
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if (name === 'hidden' && (newValue !== null || newValue !== false)) {
			this.#combatAnimationToken += 1;
		}
	}

	async render(data) {
		this.#data = data;
		this.#continueAction = HypermediaUtil.getAction(data, 'continue');

		Reconciler.reconcileProperty(this.#systemNameEl, 'innerText', data.systemName);

		this.#attackerEls.forEach((attackerEl) =>
			Reconciler.reconcileProperty(attackerEl, 'innerText', Theme.getRace(data.attacker))
		);
		this.#defenderEls.forEach((_defenderEl) =>
			Reconciler.reconcileProperty(_defenderEl, 'innerText', Theme.getRace(data.defender))
		);

		Reconciler.reconcileStyle(this.#outcomeEl, 'visibility', 'hidden');

		Reconciler.reconcileProperty(this.#buttonEl, 'innerText', 'Skip...');

		// make sure that initially displayed
		await Promise.all(this.#renderShipSpecs(data, 0, true));

		// make sure that the render function returns immediately to disable the load indicator, nevertheless do the animation (that can be cancled with the skip button)
		setTimeout(async () => {
			const currentCombatAnimationToken = (this.#combatAnimationToken += 1);
			for (let i = -1; i < data.fireExchangeCount; i++) {
				Reconciler.reconcileProperty(this.#fireExchangeEl, 'hidden', false);

				SpaceCombatPage.#logger.debug(
					`fire exchange ${i} with local token ${currentCombatAnimationToken} (global token ${
						this.#combatAnimationToken
					})`
				);

				Reconciler.reconcileProperty(
					this.#fireExchangeEl,
					'innerText',
					`Fire exchange ${Math.max(1, i + 1)} of ${data.fireExchangeCount}...`
				);

				await Promise.all(this.#renderShipSpecs(data, i, false));

				if (currentCombatAnimationToken !== this.#combatAnimationToken) {
					break;
				}
			}

			this.#renderOutcome(data);
		}, 0);
	}

	#renderShipSpecs(data, i, skipAnimation) {
		const shipSpecsAnimations = [];
		for (const shipSpecsDesc of [
			{ el: this.#attackerShipSpecsEl, data: data.attackerShipSpecs },
			{ el: this.#defenderShipSpecsEl, data: data.defenderShipSpecs },
		]) {
			const shipSpecs = shipSpecsDesc.data.map((originalShipSpec) => {
				const shipSpec = structuredClone(originalShipSpec);

				if (i >= 0) {
					let fireExchange;
					let isCurrentExchange;
					for (let j = i; j >= 0; j--) {
						if (shipSpec.fireExchanges[j]) {
							isCurrentExchange = j === i;
							fireExchange = shipSpec.fireExchanges[j];
							break;
						}
					}

					if ('damage' in shipSpec) {
						shipSpec.damage = fireExchange?.damage > 0 ? fireExchange.damage : 0;
					}

					if (fireExchange) {
						shipSpec.ships.count = fireExchange.count;

						if (fireExchange.lostHitPoints && isCurrentExchange && !skipAnimation) {
							shipSpec.ships.lostHitPoints = fireExchange.lostHitPoints;
						} else {
							delete shipSpec.ships.lostHitPoints;
						}
					} else {
						shipSpec.ships.count = shipSpec.ships.previousCount;
					}
				}

				return shipSpec;
			});

			shipSpecsAnimations.push(
				Reconciler.reconcileChildren(
					shipSpecsDesc.el,
					shipSpecsDesc.el.querySelectorAll(ShipSpecs.NAME),
					shipSpecs,
					ShipSpecs.NAME,
					{ idAttributName: 'data-ship-spec' }
				)
			);
		}

		return shipSpecsAnimations;
	}

	#renderOutcome(data) {
		Reconciler.reconcileProperty(this.#fireExchangeEl, 'hidden', true);

		Reconciler.reconcileProperty(this.#buttonEl, 'innerText', 'Continue');
		Reconciler.reconcileProperty(this.#buttonEl, 'disabled', false);

		Reconciler.reconcileStyle(this.#outcomeEl, 'visibility', 'visible');
		this.#outcomeEl.render(data.combatOutcome);
	}
}

customElements.define(SpaceCombatPage.NAME, SpaceCombatPage);
