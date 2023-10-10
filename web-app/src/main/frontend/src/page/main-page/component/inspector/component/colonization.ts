import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import SystemName from '~/page/main-page/component/inspector/component/system-name';
import cssUrl from '~/util/cssUrl';

export default class Colonization extends HTMLElement {
	static NAME = 're-colonization';

	#systemNameEl: SystemName;
	#habitabilityEl: Habitability;

	#cancelButtonEl: HTMLButtonElement;
	#colonizeButtonEl: HTMLButtonElement;

	#colonizeAction;
	#cancelAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#build-colony-text {
					text-align: center;
					padding: 0px 16px;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${SystemName.NAME}></${SystemName.NAME}>

				<${Habitability.NAME}></${Habitability.NAME}>

				<div id="build-colony-text">Build a new colony?</div>

				<${ContainerButtons.NAME} fill-horizontally>
					<button id="cancel-button">No</button>
					<button id="colonize-button">Yes</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#systemNameEl = this.shadowRoot.querySelector(SystemName.NAME);
		this.#habitabilityEl = this.shadowRoot.querySelector(Habitability.NAME);

		this.#cancelButtonEl = this.shadowRoot.querySelector('#cancel-button');
		this.#colonizeButtonEl = this.shadowRoot.querySelector('#colonize-button');

		this.shadowRoot.querySelector('#cancel-button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#cancelAction, {});
		});

		this.shadowRoot.querySelector('#colonize-button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#colonizeAction, {});
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#colonizeAction = HypermediaUtil.getAction(data, 'colonize');
			this.#cancelAction = HypermediaUtil.getAction(data, 'cancel');

			if (data.colonizeCommand === true) {
				this.#cancelButtonEl.disabled = false;
				this.#colonizeButtonEl.disabled = true;
			} else if (data.colonizeCommand === false) {
				this.#cancelButtonEl.disabled = true;
				this.#colonizeButtonEl.disabled = false;
			} else {
				this.#cancelButtonEl.disabled = false;
				this.#colonizeButtonEl.disabled = false;
			}

			this.#systemNameEl.render(data.systemName);
			this.#habitabilityEl.render(data.habitability);
		}
	}
}

customElements.define(Colonization.NAME, Colonization);
