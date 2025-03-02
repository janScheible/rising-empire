import Container from '~/component/container';
import StarBackground from '~/component/star-background';
import Theme from '~/theme/theme';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Transports extends HTMLElement {
	static NAME = 're-transports';

	static #IMAGE_WIDTH = 234;
	static #IMAGE_HEIGHT = 72;

	#transportsCountEl: HTMLSpanElement;
	#raceEl: HTMLSpanElement;
	#etaTextEl: HTMLDivElement;
	#etaEl: HTMLDivElement;

	#transportSvgEl: SVGElement;
	#transportImgEl: HTMLImageElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
				<style>
					@import ${cssUrl('~/element.css', import.meta.url)};

					#eta-text {
						text-align: center;
					}

					#wrapper {
						width: ${Transports.#IMAGE_WIDTH}px;
						height: ${Transports.#IMAGE_HEIGHT}px;

						 position: relative;
					}

					#transport-svg, #transport-img {
						position: absolute;

						left: 0px;
						top: 0px;
						bottom: 0px;
						right: 0px;
					}

					#transport-svg {
						fill: currentColor;
					}	

					#transport-img {
						padding-top: 5px;
  						padding-left: 14px;
					}
				</style>
				<${Container.NAME} outer-gap="12px">
					<div><span id="transports-count">10</span> <span id="race" class="bold">Lumerisks</span> Colonist Transports</div>
					<div id="wrapper">
						<canvas is="${StarBackground.NAME}" width="${Transports.#IMAGE_WIDTH}" height="${Transports.#IMAGE_HEIGHT}"></canvas>						
						<svg id="transport-svg" viewBox="0 0 ${Transports.#IMAGE_WIDTH} ${Transports.#IMAGE_HEIGHT}">
							<ellipse cx="175" cy="35" rx="30" ry="16" shape-rendering="geometricPrecision"/>
							<rect x="30" y="25" width="120" height="20" shape-rendering="geometricPrecision"/>
							<rect x="90" y="22" width="50" height="26" shape-rendering="geometricPrecision"/>
							<rect x="50" y="19" width="30" height="32" shape-rendering="geometricPrecision"/>
							<rect x="25" y="23" width="10" height="24" shape-rendering="geometricPrecision"/>
						</svg>
						<img hidden id="transport-img">
					</div>
					<div id="eta-text">
						ETA <span id="eta">4</span> rounds
					</div>
				</${Container.NAME}>`;

		this.#transportsCountEl = this.shadowRoot.querySelector('#transports-count');
		this.#raceEl = this.shadowRoot.querySelector('#race');
		this.#etaTextEl = this.shadowRoot.querySelector('#eta-text');
		this.#etaEl = this.shadowRoot.querySelector('#eta');

		this.#transportSvgEl = this.shadowRoot.querySelector('#transport-svg');
		this.#transportImgEl = this.shadowRoot.querySelector('#transport-img');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			const imageDataUrl = Theme.getDataUrl('inspector-transport');

			Reconciler.reconcileProperty(this.#transportsCountEl, 'innerText', data.transports);

			Reconciler.reconcileProperty(this.#raceEl, 'innerText', Theme.getRace(data.race));
			Reconciler.reconcileStyle(this.#raceEl, 'color', `var(--${data.playerColor}-player-color)`);

			Reconciler.reconcileProperty(this.#transportSvgEl, 'hidden', !!imageDataUrl);
			Reconciler.reconcileProperty(this.#transportImgEl, 'hidden', !imageDataUrl);
			if (!imageDataUrl) {
				Reconciler.reconcileStyle(this.#transportSvgEl, 'color', `var(--${data.playerColor}-player-color)`);
			} else {
				Reconciler.reconcileAttribute(this.#transportImgEl, 'src', imageDataUrl);
			}

			if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#etaTextEl, !data.eta)) {
				Reconciler.reconcileProperty(this.#etaEl, 'innerText', data.eta);
			}
		}
	}
}

customElements.define(Transports.NAME, Transports);
