import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class StarSelection extends HTMLElement {
	static NAME = 're-star-selection';

	static #WIDTH = 40;
	static #HEIGHT = 40;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					position: absolute;

					pointer-events: none;

					width: ${StarSelection.#WIDTH}px;
					height: ${StarSelection.#HEIGHT}px;

					left: var(--star-selection-left);
					top: var(--star-selection-top);

					border: 2px solid pink;
					border-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='40' height='40'><rect x='0' y='0' width='8' height='2' fill='green'/><rect x='0' y='0' width='2' height='8' fill='green'/><rect x='32' y='0' width='8' height='2' fill='green'/><rect x='38' y='0' width='2' height='8' fill='green'/><rect x='0' y='38' width='8' height='2' fill='green'/><rect x='0' y='32' width='2' height='8' fill='green'/><rect x='32' y='38' width='8' height='2' fill='green'/><rect x='38' y='32' width='2' height='8' fill='green'/></svg>") 1 stretch;
				}
			</style>`;
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			Reconciler.reconcileCssVariable(this, 'star-selection-left', data.x - StarSelection.#WIDTH / 2 + 'px');
			Reconciler.reconcileCssVariable(this, 'star-selection-top', data.y - StarSelection.#HEIGHT / 2 + 'px');
		}
	}
}

customElements.define(StarSelection.NAME, StarSelection);
