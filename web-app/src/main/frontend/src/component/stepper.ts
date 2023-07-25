import cssUrl from '~/util/cssUrl';

export default class Stepper extends HTMLElement {
	static NAME = 're-stepper';

	#value: number;
	#max: number;
	#disabled: boolean;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;
				}

				button {
					flex: 1;

					padding: 0px;
					margin: 0px;
				}
			</style>
			<button id="min">◀◀</button><button id="decrement">◀</button><button id="increment">▶</button><button id="max">▶▶</button>`;

		const updatingEventDispatcher = (valueUpdater) => {
			const previousValue = this.#value;

			if (previousValue !== (this.#value = valueUpdater())) {
				this.dispatchEvent(new CustomEvent('change', { detail: { value: this.#value } }));
			}
		};

		this.shadowRoot.querySelector('#min').addEventListener('click', (e) => {
			updatingEventDispatcher(() => 0);
		});

		this.shadowRoot.querySelector('#decrement').addEventListener('click', (e) => {
			updatingEventDispatcher(() => Math.max(0, this.#value - 1));
		});

		this.shadowRoot.querySelector('#increment').addEventListener('click', (e) => {
			updatingEventDispatcher(() => Math.min(this.#max, this.#value + 1));
		});

		this.shadowRoot.querySelector('#max').addEventListener('click', (e) => {
			updatingEventDispatcher(() => this.#max);
		});

		this.#value = 0;
		this.#max = 0;
		this.#disabled = false;
	}

	set value(value) {
		if (Number.isInteger(value)) {
			this.#value = Math.min(Math.max(0, value), this.#max);
		}
	}

	set max(max) {
		if (Number.isInteger(max)) {
			this.#max = Math.max(max, this.#value);
		}
	}

	set disabled(disabled) {
		if (typeof disabled === 'boolean') {
			this.#disabled = disabled;
			this.shadowRoot.querySelectorAll('button').forEach((buttonEl) => (buttonEl.disabled = this.#disabled));
		}
	}

	get value() {
		return this.#value;
	}

	get max() {
		return this.#max;
	}

	get disabled() {
		return this.#disabled;
	}
}

customElements.define(Stepper.NAME, Stepper);
