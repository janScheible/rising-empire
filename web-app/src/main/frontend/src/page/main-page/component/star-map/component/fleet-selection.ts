import Reconciler from '~/util/reconciler';
import FleetLocationUtil from '~/page/main-page/component/star-map/component/fleet-location-util';
import cssUrl from '~/util/cssUrl';

export default class FleetSelection extends HTMLElement {
	static NAME = 're-fleet-selection';

	static #WIDTH = 28;
	static #HEIGHT = 16;

	static #SIZE = { width: FleetSelection.#WIDTH, height: FleetSelection.#HEIGHT };

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					position: absolute;

					pointer-events: none;

					width: ${FleetSelection.#WIDTH}px;
					height: ${FleetSelection.#HEIGHT}px;

					left: var(--fleet-selection-left);
					top: var(--fleet-selection-top);

					border: 2px solid pink;
					border-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='28' height='16'><rect x='0' y='0' width='6' height='2' fill='green'/><rect x='0' y='0' width='2' height='6' fill='green'/><rect x='22' y='0' width='6' height='2' fill='green'/><rect x='26' y='0' width='2' height='6' fill='green'/><rect x='0' y='14' width='6' height='2' fill='green'/><rect x='0' y='10' width='2' height='6' fill='green'/><rect x='22' y='14' width='6' height='2' fill='green'/><rect x='26' y='10' width='2' height='6' fill='green'/></svg>") 1 stretch;
				}
			</style>`;
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			const location = FleetLocationUtil.withOffsetCentered(
				data.x,
				data.y,
				FleetSelection.#SIZE,
				data.orbiting,
				data.justLeaving
			);

			Reconciler.reconcileCssVariable(this, 'fleet-selection-left', location.x + 'px');
			Reconciler.reconcileCssVariable(this, 'fleet-selection-top', location.y + 'px');
		}
	}
}

customElements.define(FleetSelection.NAME, FleetSelection);
