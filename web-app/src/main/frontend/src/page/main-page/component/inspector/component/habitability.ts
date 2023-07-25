import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import StarBackground from '~/component/star-background';
import Theme from '~/theme/theme';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Habitability extends HTMLElement {
	static NAME = 're-habitability';

	static #WIDTH = 234;
	static #HEIGHT = 72;

	static #TYPE_COLOR_MAPPING = {
		NOT_HABITABLE: 'transparent',
		RADIATED: 'purple',
		TOXIC: 'purple',
		INFERNO: 'red',
		DEAD: 'gray',
		TUNDRA: 'brown',
		BARREN: 'brown',
		MINIMAL: 'brown',
		DESERT: 'yellow',
		STEPPE: 'yellow',
		ARID: 'yellow',
		OCEAN: 'blue',
		JUNGLE: 'green',
		TERRAN: 'blue',
	};

	#planetEl: SVGElement;
	#planetImgEl: HTMLImageElement;

	#typeEl: HTMLDivElement;
	#specialEl: HTMLDivElement;
	#maxPopulationEl: HTMLSpanElement;

	#unexploredEl: FlowLayout;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-block;
					position: relative;

					width: ${Habitability.#WIDTH}px;
					height:  ${Habitability.#HEIGHT}px;

					/* same as for canvas: https://stackoverflow.com/questions/9878090/eliminate-ghost-margin-below-html5-canvas-element */
					vertical-align: bottom;
				}

				#planet {
					position: absolute;
				}

				#planet-img {
					position: absolute;

					height: ${Habitability.#HEIGHT}px;
					width: ${Habitability.#HEIGHT * 1.1}px;
				}

				${StarBackground.NAME} {
					position: absolute;
				}

				#stats {
					position: absolute;
					top: 4px;
					bottom: 2px;
					right: 6px;

					color: white;
				}

				#unexplored {
					position: absolute;
					left: 0px;
					right: 0px;
					top: 0px;
					bottom: 0px;

					color: white;

					font-size: 200%;
					font-family: var(--theme-scifi-font);
				}

				#type {
					color: yellow;
				}

				#special {
					color: dodgerblue;
				}

				#max-population-wrapper {
					color: forestgreen;
				}

				.explored.hidden {
					display: none;
				}
			</style>
			<svg id="planet" class="explored" xmlns="http://www.w3.org/2000/svg"
					width="${Habitability.#WIDTH}" height="${Habitability.#HEIGHT}">
				<circle cx="${Habitability.#HEIGHT / 2}" cy="${Habitability.#HEIGHT / 2}"
						r="${(Habitability.#HEIGHT / 2) * 0.8}" stroke="black" fill="var(--planet-color)" />
			</svg>
			<img id="planet-img" class="explored"></img>
			<${GridLayout.NAME} id="stats">
				<div id="type" class="explored" data-row-align="right"></div>
				<div class="explored" data-row-align="right"><span>&nbsp;</span><span id="special"></span></div>
				<div id="max-population-wrapper" class="explored" data-row-align="right">Pop <span id="max-population"></span> MAX</div>
			</${GridLayout.NAME}>
			<${FlowLayout.NAME} id="unexplored" axis-align="center" cross-axis-align="center">Unexplored</${FlowLayout.NAME}>
			<canvas is="${StarBackground.NAME}" width="${Habitability.#WIDTH}"
					height="${Habitability.#HEIGHT}"></canvas>`;

		this.#planetEl = this.shadowRoot.querySelector('#planet') as SVGElement;
		this.#planetImgEl = this.shadowRoot.querySelector('#planet-img');

		this.#typeEl = this.shadowRoot.querySelector('#type');
		this.#typeEl = this.shadowRoot.querySelector('#type');
		this.#specialEl = this.shadowRoot.querySelector('#special');
		this.#maxPopulationEl = this.shadowRoot.querySelector('#max-population');

		this.#unexploredEl = this.shadowRoot.querySelector('#unexplored');
	}

	render(data) {
		const explored = !!data;

		this.shadowRoot
			.querySelectorAll('.explored')
			.forEach((el) => Reconciler.reconcileClass(el, 'hidden', !explored));
		Reconciler.reconcileProperty(this.#unexploredEl, 'hidden', explored);

		if (explored) {
			const planetQualifier = `planet-${data.type.toLowerCase()}`;
			const planetImageDataUrl = Theme.getDataUrl(planetQualifier);

			if (!planetImageDataUrl) {
				Reconciler.reconcileCssVariable(this, 'planet-color', Habitability.#TYPE_COLOR_MAPPING[data.type]);

				Reconciler.reconcileClass(this.#planetEl, 'hidden', false);
				Reconciler.reconcileClass(this.#planetImgEl, 'hidden', true);
			} else {
				Reconciler.reconcileAttribute(this.#planetImgEl, 'src', planetImageDataUrl);

				Reconciler.reconcileClass(this.#planetEl, 'hidden', true);
				Reconciler.reconcileClass(this.#planetImgEl, 'hidden', false);
			}

			Reconciler.reconcileProperty(this.#typeEl, 'innerText', data.type);

			Reconciler.reconcileProperty(this.#specialEl, 'innerText', data.special !== 'NONE' ? data.special : '');
			Reconciler.reconcileProperty(this.#maxPopulationEl, 'innerText', data.maxPopulation);
		}
	}
}

customElements.define(Habitability.NAME, Habitability);
