import Container from '~/component/container';
import Reconciler from '~/util/reconciler';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import BuildQueue from '~/page/main-page/component/inspector/component/system-details/component/build-queue';
import SystemName from '~/page/main-page/component/inspector/component/system-name';
import Colony from '~/page/main-page/component/inspector/component/system-details/component/colony';
import Allocations from '~/page/main-page/component/inspector/component/system-details/component/allocations';
import cssUrl from '~/util/cssUrl';
import TransferColonists from '~/page/main-page/component/inspector/component/system-details/component/transfer-colonists';

export default class SystemDetails extends HTMLElement {
	static NAME = 're-system-details';

	#systemNameEl: SystemName;
	#habitabilityEl: Habitability;
	#colonyEl: Colony;
	#allocationsEl: Allocations;
	#buildQueueEl: BuildQueue;
	#transferColonistsEl: TransferColonists;

	#rangeWrapperEl: HTMLDivElement;
	#rangeEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>

			<${Container.NAME} outer-gap="12px">
				<${SystemName.NAME}></${SystemName.NAME}>
				<${Habitability.NAME}></${Habitability.NAME}>
				<${Colony.NAME}></${Colony.NAME}>
				<${Allocations.NAME}></${Allocations.NAME}>
				<${BuildQueue.NAME}></${BuildQueue.NAME}>
				<${TransferColonists.NAME}></${TransferColonists.NAME}>
				<div id="range-wrapper">Range <span id="range"></span> parsecs</div>
			</${Container.NAME}>`;

		this.#systemNameEl = this.shadowRoot.querySelector(SystemName.NAME);
		this.#habitabilityEl = this.shadowRoot.querySelector(Habitability.NAME);
		this.#colonyEl = this.shadowRoot.querySelector(Colony.NAME);
		this.#allocationsEl = this.shadowRoot.querySelector(Allocations.NAME);
		this.#buildQueueEl = this.shadowRoot.querySelector(BuildQueue.NAME);
		this.#transferColonistsEl = this.shadowRoot.querySelector(TransferColonists.NAME);

		this.#rangeWrapperEl = this.shadowRoot.querySelector('#range-wrapper');
		this.#rangeEl = this.shadowRoot.querySelector('#range');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#systemNameEl.render(data.systemName);
			this.#habitabilityEl.render(data.habitability);
			this.#colonyEl.render(data.colony);
			this.#allocationsEl.render(data.allocations);
			this.#buildQueueEl.render(data.buildQueue);
			this.#transferColonistsEl.render(data.transferColonists);

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#rangeWrapperEl, !data.range)) {
				Reconciler.reconcileProperty(this.#rangeEl, 'innerText', data.range);
			}
		}
	}
}

customElements.define(SystemDetails.NAME, SystemDetails);
