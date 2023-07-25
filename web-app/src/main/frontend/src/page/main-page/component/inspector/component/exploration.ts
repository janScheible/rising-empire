import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import HypermediaUtil from '~/util/hypermedia-util';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import SystemName from '~/page/main-page/component/inspector/component/system-name';
import Reconciler from '~/util/reconciler';
import cssUrl from '~/util/cssUrl';

export default class Exploration extends HTMLElement {
	static NAME = 're-exploration';

	#systemNameEl: SystemName;
	#habitabilityEl: Habitability;

	#continueAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#explored-text {
					text-align: center;
					padding: 0px 16px;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${SystemName.NAME}></${SystemName.NAME}>

				<${Habitability.NAME}></${Habitability.NAME}>

				<div id="explored-text">Scout ships explored a new star system</div>

				<${ContainerButtons.NAME} fill-horizontally><button id="continue-button">Continue</button></${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#systemNameEl = this.shadowRoot.querySelector(SystemName.NAME);
		this.#habitabilityEl = this.shadowRoot.querySelector(Habitability.NAME);

		this.shadowRoot.querySelector('#continue-button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#continueAction, {});
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#continueAction = HypermediaUtil.getAction(data, 'continue');

			this.#systemNameEl.render(data.systemName);
			this.#habitabilityEl.render(data.habitability);
		}
	}
}

customElements.define(Exploration.NAME, Exploration);
