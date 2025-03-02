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

	#fleetSvgEl: SVGElement;
	#transportSvgEl: SVGElement;

	#imageEl: HTMLImageElement;

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

				svg {
					fill: currentColor;
				}				
				
				svg, #image {
					padding-bottom: ${(Fleet.HEIGHT / 2) * 1.4}px;

					transform: scaleX(-1);
				}
				
				#fleet-svg.oriented-right, #transport-svg.oriented-right, #image.oriented-right {
					transform: scaleX(1);
				}
			</style>
			<svg id="fleet-svg" viewBox="0 0 200 80">
				<rect x="28" y="24" width="172" height="32" shape-rendering="geometricPrecision"/>
				<rect x="0" y="0" width="60" height="80" shape-rendering="geometricPrecision"/>
			</svg>
			<svg id="transport-svg" viewBox="0 0 200 80">
				<rect x="0" y="25" width="160" height="30" shape-rendering="geometricPrecision"/>
				<ellipse cx="126.4" cy="40" rx="60" ry="40" shape-rendering="geometricPrecision"/>
				<rect x="0" y="0" width="50" height="80" shape-rendering="geometricPrecision"/>
			</svg>
			<img id="image"></img>`;

		this.#fleetSvgEl = this.shadowRoot.querySelector('#fleet-svg');
		this.#transportSvgEl = this.shadowRoot.querySelector('#transport-svg');

		this.#imageEl = this.shadowRoot.querySelector('#image');

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

		const qualifier = !data.colonistTransporters ? `fleet-${data.playerColor}` : `transport-${data.playerColor}`;
		const imageDataUrl = Theme.getDataUrl(qualifier);

		const orientedRight = data.orbiting || data.horizontalDirection === 'right';

		Reconciler.reconcileStyle(this, 'color', `var(--${data.playerColor}-player-color)`);

		if (!imageDataUrl) {
			Reconciler.reconcileProperty(this.#imageEl, 'hidden', true);

			Reconciler.reconcileStyle(this.#fleetSvgEl, 'display', !data.colonistTransporters ? 'initial' : 'none');
			Reconciler.reconcileStyle(this.#transportSvgEl, 'display', data.colonistTransporters ? 'initial' : 'none');

			Reconciler.reconcileClass(this.#fleetSvgEl, 'oriented-right', orientedRight);
			Reconciler.reconcileClass(this.#transportSvgEl, 'oriented-right', orientedRight);
		} else {
			Reconciler.reconcileStyle(this.#fleetSvgEl, 'display', 'none');
			Reconciler.reconcileStyle(this.#transportSvgEl, 'display', 'none');

			Reconciler.reconcileProperty(this.#imageEl, 'hidden', false);

			Reconciler.reconcileAttribute(this.#imageEl, 'src', imageDataUrl);

			Reconciler.reconcileClass(this.#imageEl, 'oriented-right', orientedRight);
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
