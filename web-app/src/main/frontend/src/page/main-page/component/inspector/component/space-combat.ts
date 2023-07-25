import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import SystemName from '~/page/main-page/component/inspector/component/system-name';
import FlowLayout from '~/component/flow-layout';
import cssUrl from '~/util/cssUrl';

export default class SpaceCombat extends HTMLElement {
	static NAME = 're-space-combat';

	#wrapperEl: FlowLayout;
	#systemNameEl: SystemName;
	#habitabilityEl: Habitability;

	#attackerEl: HTMLSpanElement;
	#defenderEl: HTMLSpanElement;

	#continueAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#text {
					text-align: center;
					padding: 0px 16px;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${FlowLayout.NAME} id="wrapper" direction="column" gap="XL">
					<${SystemName.NAME}></${SystemName.NAME}>
					<${Habitability.NAME}></${Habitability.NAME}>
				</${FlowLayout.NAME}>

				<div id="text">
					<span id="attacker" class="bold"></span> attack <span id="defender" class="bold"></span> in a space combat								
				</div>

				<${ContainerButtons.NAME} fill-horizontally><button id="continue-button">Continue</button></${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#wrapperEl = this.shadowRoot.querySelector('#wrapper');
		this.#systemNameEl = this.shadowRoot.querySelector(SystemName.NAME);
		this.#habitabilityEl = this.shadowRoot.querySelector(Habitability.NAME);

		this.#attackerEl = this.shadowRoot.querySelector('#attacker');
		this.#defenderEl = this.shadowRoot.querySelector('#defender');

		this.shadowRoot.querySelector('#continue-button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#continueAction, {});
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#continueAction = HypermediaUtil.getAction(data, 'continue');

			if (Reconciler.reconcileProperty(this.#systemNameEl, 'hidden', !data.systemName)) {
				this.#wrapperEl.updateMargins();
			}
			if (!this.#systemNameEl.hidden) {
				this.#systemNameEl.render(data.systemName);
			}

			this.#habitabilityEl.render(data.habitability);

			Reconciler.reconcileProperty(this.#attackerEl, 'innerText', data.attackerRace);
			Reconciler.reconcileStyle(this.#attackerEl, 'color', `var(--${data.attackerColor}-player-color)`);

			Reconciler.reconcileProperty(this.#defenderEl, 'innerText', data.defenderRace);
			Reconciler.reconcileStyle(this.#defenderEl, 'color', `var(--${data.defenderColor}-player-color)`);
		}
	}
}

customElements.define(SpaceCombat.NAME, SpaceCombat);
