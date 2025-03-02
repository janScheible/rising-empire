import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import Action from '~/util/action';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class RelocateShips extends HTMLElement {
	static NAME = 're-relocate-ships';

	#delaySectionEl: Container;
	#delayEl: HTMLDivElement;

	#relocateAction: Action;
	#cancelAction: Action;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#delay-container {
					text-align: center;
				}

				#header {
					font-size: 150%;
					text-align: center;
				}
			</style>
			<${Container.NAME} outer-gap="0px">
				<div id="header">Relocate</div>
				<div>Select another star system under your control to redirect newly built ships to.</div>

				<${Container.NAME} id="delay-section" direction="column" gap="M">
					<div id="delay-container">Delay <span id="delay"></span> rounds</div>
				</${Container.NAME}>

				<${ContainerButtons.NAME} fill-horizontally>
					<button id="cancel">Cancel</button>
					<button id="accept">Accept</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#delaySectionEl = this.shadowRoot.querySelector('#delay-section');
		this.#delayEl = this.shadowRoot.querySelector('#delay');

		this.shadowRoot
			.querySelector('#accept')
			.addEventListener('click', (e) => HypermediaUtil.submitAction(this.#relocateAction));

		this.shadowRoot
			.querySelector('#cancel')
			.addEventListener('click', (e) => HypermediaUtil.submitAction(this.#cancelAction));
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#relocateAction = HypermediaUtil.getAction(data, 'relocate');
			this.#cancelAction = HypermediaUtil.getAction(data, 'cancel');

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#delaySectionEl, !data.delay)) {
				Reconciler.reconcileProperty(this.#delayEl, 'innerText', data.delay);
			}
		}
	}
}

customElements.define(RelocateShips.NAME, RelocateShips);
