import Reconciler from '~/util/reconciler';
import FleetLocationUtil from '~/page/main-page/component/star-map/component/fleet-location-util';
import cssUrl from '~/util/cssUrl';

export default class Itinerary extends HTMLElement {
	static NAME = 're-itinerary';

	#lineEl: SVGLineElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
				
				:host {
					position: absolute;

					left: var(--itinerary-left);
					top: var(--itinerary-top);
					width: var(--itinerary-width);
					height: var(--itinerary-height);

					pointer-events: none;
				}

				svg {
					width: 100%;
					height: 100%;
				}

				line {
					stroke-width: 3;
					stroke-dasharray: 5, 5;
					stroke-dashoffset: 25;

					animation: line-dash-offset-animation 1s linear 0s infinite;
				}

				@keyframes line-dash-offset-animation {
					to {
						stroke-dashoffset: 5;
					}
				}
			</style>
			<svg xmlns="http://www.w3.org/2000/svg">
				<line/>
			</svg>`;

		this.#lineEl = this.shadowRoot.querySelector('line');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			const { x: fleetX, y: fleetY } = FleetLocationUtil.withOffset(
				data.fleetX,
				data.fleetY,
				data.orbiting,
				data.justLeaving
			);

			const starX = data.starX;
			const starY = data.starY;

			const width = Math.abs(fleetX - starX);
			const height = Math.abs(fleetY - starY);

			Reconciler.reconcileAttribute(this.#lineEl, fleetX > starX ? 'x1' : 'x2', width);
			Reconciler.reconcileAttribute(this.#lineEl, fleetX > starX ? 'x2' : 'x1', '0');
			Reconciler.reconcileAttribute(this.#lineEl, fleetY > starY ? 'y1' : 'y2', height);
			Reconciler.reconcileAttribute(this.#lineEl, fleetY > starY ? 'y2' : 'y1', '0');

			Reconciler.reconcileAttribute(
				this.#lineEl,
				'stroke',
				data.relocation ? 'purple' : data.inRange ? 'green' : 'red'
			);

			Reconciler.reconcileCssVariable(this, 'itinerary-left', Math.min(fleetX, starX) + 'px');
			Reconciler.reconcileCssVariable(this, 'itinerary-top', Math.min(fleetY, starY) + 'px');
			Reconciler.reconcileCssVariable(this, 'itinerary-width', width + 'px');
			Reconciler.reconcileCssVariable(this, 'itinerary-height', height + 'px');
		}
	}
}

customElements.define(Itinerary.NAME, Itinerary);
