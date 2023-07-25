import cssUrl from '~/util/cssUrl';

export default class ConnectionIndicator extends HTMLElement {
	static NAME = 're-connection-indicator';

	#connected = false;
	#indicatorEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				span {
					color: red;
				}

				.connected {
					color: green;
				}
			</style>
			<span>⬤</span>`;

		this.#indicatorEl = this.shadowRoot.querySelector('span');
	}

	set connected(connected) {
		if (connected) {
			this.#indicatorEl.classList.add('connected');
		} else {
			this.#indicatorEl.classList.remove('connected');
		}

		this.#connected = connected;
	}

	get connected() {
		return this.#connected;
	}
}

customElements.define(ConnectionIndicator.NAME, ConnectionIndicator);
