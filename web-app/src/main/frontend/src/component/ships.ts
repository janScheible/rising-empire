import StarBackground from '~/component/star-background';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';
import sleep from '~/util/sleep';
import Theme from '~/theme/theme';

export default class Ships extends HTMLElement {
	static NAME = 're-ships';

	static #WIDTH = 99;
	static #HEIGHT = 75;

	static #SHIP_WIDTH = 90;
	static #SHIP_HEIGHT = 72;

	static #SHIP_SCALE_AND_OFFSET = {
		SMALL: { scaleX: 0.25, scaleY: 0.12, displacementY: 0.45 },
		MEDIUM: { scaleX: 0.5, scaleY: 0.22, displacementY: 0.4 },
		LARGE: { scaleX: 0.6, scaleY: 0.27, displacementY: 0.36 },
		HUGE: { scaleX: 0.7, scaleY: 0.32, displacementY: 0.3 },
	};

	#shipEl: HTMLDivElement;
	#shipImgEl: HTMLImageElement;

	#backgroundEl: StarBackground;
	#countEl: HTMLSpanElement;
	#lostHitPointsWrapper: HTMLSpanElement;
	#lostHitPoints: HTMLDivElement;

	#countElText: string;

	#data = undefined;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-block;

					position: relative;

					/* same as for canvas: https://stackoverflow.com/questions/9878090/eliminate-ghost-margin-below-html5-canvas-element */
					vertical-align: top;
				}

				#wrapper {
					width:  ${Ships.#WIDTH}px;
					height: ${Ships.#HEIGHT}px;
				}

				#ship {
					position: absolute;
					left: 0px;
					top: 0px;
				}

				#ship-img {
					position: absolute;
					left: 0px;
					top: 0px;
				}

				#ship::before {
					content: '';
					border-style: solid;
					border-width: var(--ship-height) var(--ship-width) var(--ship-height) var(--ship-width);
					border-color: transparent transparent transparent var(--player-color);
					display: inline-block;
					vertical-align: top;
					margin-left: var(--ship-margin-left);
					margin-top: var(--ship-margin-top);
				}

				#count {
					position: absolute;
					right: 4px;
					bottom: 4px;

					color: yellow;
				}

				@keyframes fadeInAndOut {
					0% { opacity: 0; }
					50% { opacity: 1; }
					100% { opacity: 0; }
				}

				#lost-hit-points-wrapper {
					position: absolute;

					left: 0px;
					top: 0px;
					right: 0px;
					bottom: 0px;

					color: red;
					font-size: 170%;
					background-color: rgba(0,0,0,0.5);

					display: flex;
					justify-content: center;
					align-items: center;

					animation: fadeInAndOut 1s;
				}

				#lost-hit-points-wrapper.hidden {
					display: none;
					animation: none;
				}
			</style>
			<div id="wrapper">
				<canvas id="background" is="${StarBackground.NAME}" width="${Ships.#WIDTH}" height="${Ships.#HEIGHT}"></canvas>
				<div id="ship"></div>
				<img id="ship-img"></img>
				<span id="lost-hit-points-wrapper" class="hidden">
					<div id="lost-hit-points">-2</div>
				</span>
				<span id="count">0</span>
			</div>`;

		this.#shipEl = this.shadowRoot.querySelector('#ship');
		this.#shipImgEl = this.shadowRoot.querySelector('#ship-img');

		this.#backgroundEl = this.shadowRoot.querySelector('#background');
		this.#countEl = this.shadowRoot.querySelector('#count');
		this.#lostHitPointsWrapper = this.shadowRoot.querySelector('#lost-hit-points-wrapper');
		this.#lostHitPoints = this.shadowRoot.querySelector('#lost-hit-points');

		this.#lostHitPointsWrapper.addEventListener('animationend', () => {
			this.#lostHitPointsWrapper.classList.add('hidden');
			Reconciler.reconcileProperty(this.#countEl, 'innerText', this.#countElText);
		});
	}

	connectedCallback() {
		if (this.hasAttribute('animated-background')) {
			this.#backgroundEl.setAttribute('animated', '');
		}
	}

	async render(data) {
		this.#data = data;

		const countElText = data.previousCount ? data.count + '/' + data.previousCount : data.count;
		if (data.lostHitPoints) {
			this.#countElText = countElText;
		} else {
			Reconciler.reconcileProperty(this.#countEl, 'innerText', countElText);
		}

		const shipQualifier = `ship-${data.playerColor}-${data.size.toLowerCase()}`;
		const shipImageDataUrl = Theme.getDataUrl(shipQualifier);

		if (!shipImageDataUrl) {
			Reconciler.reconcileProperty(this.#shipEl, 'hidden', false);
			Reconciler.reconcileProperty(this.#shipImgEl, 'hidden', true);

			Reconciler.reconcileCssVariable(this, 'player-color', `var(--${data.playerColor}-player-color)`);

			const scaleAndOffset = Ships.#SHIP_SCALE_AND_OFFSET[data.size];
			let scaleX = scaleAndOffset.scaleX;
			let scaleY = scaleAndOffset.scaleY;
			let displacementY = scaleAndOffset.displacementY;

			Reconciler.reconcileCssVariable(this, 'ship-width', `${Ships.#SHIP_WIDTH * scaleX}px`);
			Reconciler.reconcileCssVariable(this, 'ship-height', `${Ships.#SHIP_HEIGHT * scaleY}px`);
			Reconciler.reconcileCssVariable(this, 'ship-margin-left', `${Ships.#SHIP_WIDTH * 0.5 * (1 - scaleX)}px`);
			Reconciler.reconcileCssVariable(
				this,
				'ship-margin-top',
				`${Ships.#SHIP_HEIGHT * displacementY * (1 - scaleY)}px`
			);
		} else {
			Reconciler.reconcileProperty(this.#shipImgEl, 'hidden', false);
			Reconciler.reconcileProperty(this.#shipEl, 'hidden', true);

			Reconciler.reconcileAttribute(this.#shipImgEl, 'src', shipImageDataUrl);
		}

		Reconciler.reconcileClass(this.#lostHitPointsWrapper, 'hidden', !data.lostHitPoints);
		if (data.lostHitPoints) {
			Reconciler.reconcileProperty(this.#lostHitPoints, 'innerText', '-' + data.lostHitPoints);

			return sleep(1050);
		}
	}

	/** Shortcut to just update the count on client-side. */
	updateCount(count) {
		if (this.#data) {
			this.#data.count = count;
			this.render(this.#data);
		}
	}
}

customElements.define(Ships.NAME, Ships);
