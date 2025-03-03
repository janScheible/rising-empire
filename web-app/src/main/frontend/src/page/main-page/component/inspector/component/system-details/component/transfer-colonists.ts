import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import FlowLayout from '~/component/flow-layout';
import Slider from '~/component/slider';
import Action from '~/util/action';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class TransferColonists extends HTMLElement {
	static NAME = 're-transfer-colonists';

	#selectDestinationStarEl: HTMLDivElement;
	#chooseNumberEl: HTMLDivElement;

	#colonistsSliderEl: Slider;
	#colonistsEl: HTMLSpanElement;
	#etaEl: HTMLSpanElement;
	#acceptButton: HTMLButtonElement;

	#chooseNumberTextEl: HTMLSpanElement;
	#thresholdWarningTextEl: HTMLSpanElement;
	#thresholdEl: HTMLSpanElement;

	#cancelAction: Action;
	#transferAction: Action;

	#data;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#eta-container {
					text-align: center;
				}

				#header {
					font-size: 150%;
					text-align: center;
				}

				#threshold-warning-text {
					font-weight: bold;
				}
			</style>
			<${Container.NAME} outer-gap="0px">
				<div id="header">Transfer</div>
				<div id="select-destination-star" hidden>Select a destination star system under your control to send colonists to.</div>
				<${FlowLayout.NAME} id="choose-number" hidden direction="column">
					<span id="choose-number-text">Choose number of colonists to transfer.</span>
					<span id="threshold-warning-text">Warning - Target system can only support <span id="threshold"></span> million more!</span>
					<${Slider.NAME} id="colonists-slider"></${Slider.NAME}>
					<div>Moving <span id="colonists"></span> million colonists.</div>
					<${Container.NAME} direction="column" gap="M">
						<div id="eta-container">ETA <span id="eta"></span> rounds</div>
					</${Container.NAME}>
				</${FlowLayout.NAME}>
				
				<${ContainerButtons.NAME} fill-horizontally>
					<button id="cancel">Cancel</button>
					<button id="accept">Accept</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#selectDestinationStarEl = this.shadowRoot.querySelector('#select-destination-star');
		this.#chooseNumberEl = this.shadowRoot.querySelector('#choose-number');

		this.#colonistsSliderEl = this.shadowRoot.querySelector('#colonists-slider');
		this.#colonistsEl = this.shadowRoot.querySelector('#colonists');
		this.#etaEl = this.shadowRoot.querySelector('#eta');

		this.#chooseNumberTextEl = this.shadowRoot.querySelector('#choose-number-text');
		this.#thresholdWarningTextEl = this.shadowRoot.querySelector('#threshold-warning-text');
		this.#thresholdEl = this.shadowRoot.querySelector('#threshold');

		this.shadowRoot
			.querySelector('#cancel')
			.addEventListener('click', (e) => HypermediaUtil.submitAction(this.#cancelAction));

		this.#acceptButton = this.shadowRoot.querySelector('#accept');
		this.#acceptButton.addEventListener('click', (e) => HypermediaUtil.submitAction(this.#transferAction));

		this.#colonistsSliderEl.addEventListener('change', (event: CustomEvent) => {
			this.#data.colonists = (event.detail.value / 100.0) * this.#data.maxColonists;
			HypermediaUtil.getField(this.#data, 'transfer', 'colonists').value = Math.round(this.#data.colonists);

			this.render(this.#data);
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#data = data;

			this.#cancelAction = HypermediaUtil.getAction(data, 'cancel');
			this.#transferAction = HypermediaUtil.getAction(data, 'transfer');

			Reconciler.reconcileProperty(this.#selectDestinationStarEl, 'hidden', data.eta);

			const showWarning = data.colonists > data.warningThreshold;
			Reconciler.reconcileProperty(this.#chooseNumberTextEl, 'hidden', showWarning);
			Reconciler.reconcileProperty(this.#thresholdWarningTextEl, 'hidden', !showWarning);
			Reconciler.reconcileProperty(this.#thresholdEl, 'innerText', data.warningThreshold);

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#chooseNumberEl, !(data.eta > 0))) {
				Reconciler.reconcileProperty(this.#colonistsEl, 'innerText', Math.round(data.colonists).toString());
				Reconciler.reconcileProperty(this.#etaEl, 'innerText', data.eta);
				Reconciler.reconcileProperty(
					this.#colonistsSliderEl,
					'value',
					Math.round((data.colonists / data.maxColonists) * 100)
				);
			}
			Reconciler.reconcileProperty(this.#acceptButton, 'disabled', !this.#transferAction);
		}
	}
}

customElements.define(TransferColonists.NAME, TransferColonists);
