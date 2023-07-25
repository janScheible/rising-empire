import Reconciler from '~/util/reconciler';
import HypermediaUtil from '~/util/hypermedia-util';
import FleetLocationUtil from '~/page/main-page/component/star-map/component/fleet-location-util';
import LoggerFactory from '~/util/logger/logger-factory';
import Logger from '~/util/logger/logger';
import Theme from '~/theme/theme';

export default class Fleet extends HTMLElement {
	static NAME = 're-fleet';

	static WIDTH = 20;
	static HEIGHT = 8;

	static #BASE_ANIMATION_DURATION = 0.5;

	static #SIZE = { width: Fleet.WIDTH, height: Fleet.HEIGHT };

	static #logger: Logger = LoggerFactory.get(`${import.meta.url}`);

	#fleetImageEl: HTMLImageElement;

	#selectAction;

	#blocked: boolean;

	#previousX: number;
	#previousY: number;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				:host {
					position: absolute;

					width: ${Fleet.WIDTH}px;
					height: ${Fleet.HEIGHT}px;
					
					left: var(--fleet-left);
					top: var(--fleet-top);

					transition: left var(--fleet-animation-duration), top var(--fleet-animation-duration);
					transition-timing-function: linear;
				}
				
				:host::before {
					content: var(--fleet-before-content);
					border-style: solid;
					border-width: ${(Fleet.HEIGHT / 2) * 1.4}px ${Fleet.WIDTH * 1.2}px ${(Fleet.HEIGHT / 2) * 1.4}px 0px;
					border-color: transparent var(--fleet-color) transparent transparent;
					display: inline-block;
					vertical-align: top;
					margin-left: -${Fleet.WIDTH * 1.2 - Fleet.WIDTH}px;
					margin-top: -${((Fleet.HEIGHT / 2) * 1.4 - Fleet.HEIGHT / 2) * 0.5}px;
				}

				:host(.oriented-right)::before {
					border-width: ${(Fleet.HEIGHT / 2) * 1.4}px ${Fleet.WIDTH * 1.2}px ${(Fleet.HEIGHT / 2) * 1.4}px ${Fleet.WIDTH * 1.2}px;
					border-color: transparent transparent transparent var(--fleet-color);
					margin-left: 0px;
				}

				#fleet-image {
					padding-bottom: ${(Fleet.HEIGHT / 2) * 1.4}px;

					transform: scaleX(-1);
				}
				
				#fleet-image.oriented-right {
					transform: scaleX(1);
				}
			</style>
			<img id="fleet-image"></img>`;

		this.#fleetImageEl = this.shadowRoot.querySelector('#fleet-image');

		this.addEventListener('click', (e) => {
			if (this.#selectAction && !this.#blocked) {
				HypermediaUtil.submitAction(this.#selectAction, {});
			}
		});
	}

	getZIndex() {
		const zIndex = parseInt(this.style.zIndex);
		return isNaN(zIndex) ? parseInt(window.getComputedStyle(this).zIndex) : zIndex;
	}

	render(data) {
		this.#selectAction = HypermediaUtil.getAction(data, 'select');
		this.#blocked = data.blocked;

		this.#previousX = parseInt(this.style.getPropertyValue('--fleet-left').replace('px', ''));
		this.#previousY = parseInt(this.style.getPropertyValue('--fleet-top').replace('px', ''));

		const fleetQualifier = `fleet-${data.playerColor}`;
		const fleetImageDataUrl = Theme.getDataUrl(fleetQualifier);

		const orientedRight = data.orbiting || data.horizontalDirection === 'right';

		if (!fleetImageDataUrl) {
			Reconciler.reconcileCssVariable(this, 'fleet-before-content', `''`);
			Reconciler.reconcileProperty(this.#fleetImageEl, 'hidden', true);

			Reconciler.reconcileCssVariable(
				this,
				'fleet-color',
				!data.blocked ? `var(--${data.playerColor}-player-color)` : 'gray'
			);

			Reconciler.reconcileClass(this, 'oriented-right', orientedRight);
		} else {
			Reconciler.reconcileCssVariable(this, 'fleet-before-content', 'none');
			Reconciler.reconcileProperty(this.#fleetImageEl, 'hidden', false);

			Reconciler.reconcileAttribute(this.#fleetImageEl, 'src', fleetImageDataUrl);

			Reconciler.reconcileClass(this.#fleetImageEl, 'oriented-right', orientedRight);
		}

		const location = FleetLocationUtil.withOffsetCentered(
			data.x,
			data.y,
			Fleet.#SIZE,
			data.orbiting,
			data.justLeaving
		);

		if (
			!isNaN(this.#previousX) &&
			!isNaN(this.#previousY) &&
			this.#previousX !== location.x &&
			this.#previousY !== location.y
		) {
			const distance = Math.sqrt(
				Math.pow(this.#previousX - location.x, 2) + Math.pow(this.#previousY - location.y, 2)
			);
			const animationDuration = ((Fleet.#BASE_ANIMATION_DURATION * distance) / data.speed).toFixed(2);

			if (Fleet.#logger.isDebugEnabled()) {
				Fleet.#logger.debug(
					`fleet movement (${this.#previousX},${this.#previousY}) -> (${location.x},${
						location.y
					}) in ${animationDuration}sec (orbiting: ${data.orbiting}, justLeaving: ${data.justLeaving})`
				);
			}
			Reconciler.reconcileCssVariable(this, 'fleet-animation-duration', animationDuration + 's');
		}

		Reconciler.reconcileCssVariable(this, 'fleet-left', location.x + 'px');
		Reconciler.reconcileCssVariable(this, 'fleet-top', location.y + 'px');
	}
}

customElements.define(Fleet.NAME, Fleet);
