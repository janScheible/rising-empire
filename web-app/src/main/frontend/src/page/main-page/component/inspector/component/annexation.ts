import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import SystemName from '~/page/main-page/component/inspector/component/system-name';
import cssUrl from '~/util/cssUrl';

export default class Annexation extends HTMLElement {
	static NAME = 're-annexation';

	#systemNameEl: SystemName;
	#habitabilityEl: Habitability;

	#annexAction;
	#cancelAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#annex-colony-text {
					text-align: center;
					padding: 0px 16px;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${SystemName.NAME}></${SystemName.NAME}>

				<${Habitability.NAME}></${Habitability.NAME}>

				<div id="annex-colony-text">Annex the colony?</div>

				<${ContainerButtons.NAME} fill-horizontally>
					<button id="cancel-button">No</button>
					<button id="annex-button">Yes</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#systemNameEl = this.shadowRoot.querySelector(SystemName.NAME);
		this.#habitabilityEl = this.shadowRoot.querySelector(Habitability.NAME);

		this.shadowRoot.querySelector('#cancel-button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#cancelAction, {});
		});

		this.shadowRoot.querySelector('#annex-button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#annexAction, {});
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#annexAction = HypermediaUtil.getAction(data, 'annex');
			this.#cancelAction = HypermediaUtil.getAction(data, 'cancel');

			this.#systemNameEl.render(data.systemName);
			this.#habitabilityEl.render(data.habitability);
		}
	}
}

customElements.define(Annexation.NAME, Annexation);
