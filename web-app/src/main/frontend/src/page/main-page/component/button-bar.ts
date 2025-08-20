import ModalDialog from '~/component/modal-dialog';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class ButtonBar extends HTMLElement {
	static NAME = 're-button-bar';

	#turnEl: HTMLButtonElement;
	#techEl: HTMLButtonElement;

	#finishTurnAction;
	#showTechPageAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;

					position: relative;
				}

				button {
					flex: 1;

					height: 28px;
				}

				${ModalDialog.NAME} {
					position: absolute;
				}
			</style>
			<button id="game" disabled>Game</button>
			<button id="design" disabled>Design</button>
			<button id="fleet" disabled>Fleet</button>
			<button id="map" disabled>Map</button>
			<button id="races" disabled>Races</button>
			<button id="planets" disabled>Planets</button>
			<button id="tech" disabled>Tech</button>
			<button id="turn" disabled>Next Turn</button>
			<${ModalDialog.NAME} hidden></${ModalDialog.NAME}>`;

		this.#turnEl = this.shadowRoot.querySelector('#turn');
		this.#turnEl.addEventListener('click', () => {
			HypermediaUtil.submitAction(this.#finishTurnAction, { partial: false });
		});

		this.#techEl = this.shadowRoot.querySelector('#tech');
		this.#techEl.addEventListener('click', () => HypermediaUtil.submitAction(this.#showTechPageAction, {}));
	}

	render(data) {
		this.#finishTurnAction = HypermediaUtil.getAction(data, 'finish-turn');
		Reconciler.reconcileProperty(this.#turnEl, 'disabled', !this.#finishTurnAction);

		// not yet really implemented... page just shows dummy data
		// this.#showTechPageAction = HypermediaUtil.getAction(data, 'show-tech-page');
		//Reconciler.reconcileProperty(this.#techEl, 'disabled', !this.#showTechPageAction);
	}
}

customElements.define(ButtonBar.NAME, ButtonBar);
