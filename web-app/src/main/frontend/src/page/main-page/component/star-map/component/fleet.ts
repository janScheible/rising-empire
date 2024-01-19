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

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				:host {
					position: absolute;

					width: ${Fleet.WIDTH}px;
					height: ${Fleet.HEIGHT}px;
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
			if (this.#selectAction) {
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

		const fleetQualifier = `fleet-${data.playerColor}`;
		const fleetImageDataUrl = Theme.getDataUrl(fleetQualifier);

		const orientedRight = data.orbiting || data.horizontalDirection === 'right';

		if (!fleetImageDataUrl) {
			Reconciler.reconcileCssVariable(this, 'fleet-before-content', `''`);
			Reconciler.reconcileProperty(this.#fleetImageEl, 'hidden', true);

			Reconciler.reconcileCssVariable(this, 'fleet-color', `var(--${data.playerColor}-player-color)`);

			Reconciler.reconcileClass(this, 'oriented-right', orientedRight);
		} else {
			Reconciler.reconcileCssVariable(this, 'fleet-before-content', 'none');
			Reconciler.reconcileProperty(this.#fleetImageEl, 'hidden', false);

			Reconciler.reconcileAttribute(this.#fleetImageEl, 'src', fleetImageDataUrl);

			Reconciler.reconcileClass(this.#fleetImageEl, 'oriented-right', orientedRight);
		}

		const previousLocation =
			!isNaN(data.previousX) && !isNaN(data.previousY)
				? FleetLocationUtil.withOffsetCentered(
						data.previousX,
						data.previousY,
						Fleet.#SIZE,
						false,
						data.previousJustLeaving
				  )
				: undefined;

		const location = FleetLocationUtil.withOffsetCentered(
			data.x,
			data.y,
			Fleet.#SIZE,
			data.orbiting,
			data.justLeaving
		);

		const currentX = parseInt(this.style.left.replace('px', ''));
		const currentY = parseInt(this.style.top.replace('px', ''));
		if (!previousLocation || (currentX === location.x && currentY === location.y)) {
			Reconciler.reconcileStyle(this, 'left', location.x + 'px');
			Reconciler.reconcileStyle(this, 'top', location.y + 'px');
		} else {
			const distance = Math.sqrt(
				Math.pow((previousLocation ?? location).x - location.x, 2) +
					Math.pow((previousLocation ?? location).y - location.y, 2)
			);
			const animationDuration = Math.max((Fleet.#BASE_ANIMATION_DURATION * distance) / data.speed, 0);

			const fleetAnimation = this.animate(
				[
					{ left: previousLocation.x + 'px', top: previousLocation.y + 'px' },
					{ left: location.x + 'px', top: location.y + 'px' },
				],
				{ duration: animationDuration * 1000, fill: 'forwards' }
			);
			fleetAnimation.addEventListener('finish', () => {
				fleetAnimation.commitStyles();
				fleetAnimation.cancel();
			});

			Fleet.#logger.debug(
				`fleet movement (${previousLocation.x},${previousLocation.y}) (previousJustLeaving: ${data.previousJustLeaving}) -> (${location.x},${location.y}) in ${animationDuration}sec (orbiting: ${data.orbiting}, justLeaving: ${data.justLeaving})`
			);
		}
	}
}

customElements.define(Fleet.NAME, Fleet);
