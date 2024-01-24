import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import Ships from '~/component/ships';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class BuildQueue extends HTMLElement {
	static NAME = 're-build-queue';

	#shipsEl: Ships;
	#nameEl: HTMLDivElement;
	#transferButtonEl: HTMLButtonElement;

	#nextShipTypeAction;
	#transferColonistsAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
				}
			</style>
			<${FlowLayout.NAME} id="ship-build-queue" direction="column">
				<div class="bold">Ship build queue</div>
				<${GridLayout.NAME} cols="min-content 3fr" gap="S" border>
					<${Ships.NAME} data-no-padding></${Ships.NAME}>
					<div data-row-span="2">
						<${GridLayout.NAME} cols="1">
							<button id="next-ship-type">Ships</button>
							<button disabled>Relocate</button>
							<button id="transfer">Transfer</button>
						</${GridLayout.NAME}>
					</div>
					<${FlowLayout.NAME} id="name" axis-align="center"></${FlowLayout.NAME}>
				</${GridLayout.NAME}>
			</${FlowLayout.NAME}>`;

		this.#shipsEl = this.shadowRoot.querySelector(Ships.NAME);
		this.#nameEl = this.shadowRoot.querySelector('#name');

		this.shadowRoot.querySelector('#next-ship-type').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#nextShipTypeAction);
		});

		this.shadowRoot.querySelector('#transfer').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#transferColonistsAction);
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#nextShipTypeAction = HypermediaUtil.getAction(data, 'next-ship-type');
			this.#transferColonistsAction = HypermediaUtil.getAction(data, 'transfer-colonists');

			this.#shipsEl.render(data);
			Reconciler.reconcileProperty(this.#nameEl, 'innerText', data.name);
		}
	}
}

customElements.define(BuildQueue.NAME, BuildQueue);
