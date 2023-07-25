import Container from '~/component/container';
import Reconciler from '~/util/reconciler';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import cssUrl from '~/util/cssUrl';

export default class Unexplored extends HTMLElement {
	static NAME = 're-unexplored';

	static #STAR_TYPE_DESCRIPTION_MAPPING = {
		YELLOW: 'Yellow stars offer the best chance of discovering terran and sub-terran planets.',
		RED: 'Red stars are old, dull stars that commonly have poor planets.',
		GREEN: 'Green stars are moderately bright and have a wide range of planetary types.',
		BLUE: 'Blue stars are relatively young stars with mineral rich lifeless planets.',
		WHITE: 'White stars burn incredibly hot and generally have hostile planets.',
		PURPLE: 'Neutron stars are rare and offer the greatest chance of finding rich planets.',
	};

	#habitabilityEl: Habitability;
	#descriptionEl: HTMLDivElement;
	#rangeEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#description {
					text-align: center;
					padding: 0px 16px;
				}
			</style>
			<${Container.NAME} outer-gap="12px">
				<${Habitability.NAME}></${Habitability.NAME}>
				<div id="description">Unexplored</div>
				<div>Range <span id="range"></span> parsecs</div>
			</${Container.NAME}>`;

		this.#habitabilityEl = this.shadowRoot.querySelector(Habitability.NAME);
		this.#descriptionEl = this.shadowRoot.querySelector('#description');
		this.#rangeEl = this.shadowRoot.querySelector('#range');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#habitabilityEl.render(undefined);
			Reconciler.reconcileProperty(
				this.#descriptionEl,
				'innerText',
				Unexplored.#STAR_TYPE_DESCRIPTION_MAPPING[data.starType]
			);

			Reconciler.reconcileProperty(this.#rangeEl, 'innerText', data.range);
		}
	}
}

customElements.define(Unexplored.NAME, Unexplored);
