import cssUrl from '~/util/cssUrl';

export default class Slider extends HTMLElement {
	static NAME = 're-slider';

	#rangeEl: HTMLDivElement;
	#indicatorEl: HTMLDivElement;
	#decreaseButtonEl: HTMLButtonElement;
	#increaseButtonEl: HTMLButtonElement;

	#locked: boolean = false;
	#disabled: boolean = false;
	#value: number;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-flex;
				}

				#range {
					flex: 1;
					display: inline-block;
					min-width: 0px;

					background-color: black;

					border-top: 2px solid dimgray;
					border-bottom: 2px solid dimgray;
				}

				#indicator {
					height: 100%;
					width: 0%;

					pointer-events: none;

					background-color: #3D8A8B;
				}

				#indicator.locked {
					background-color: darkred;
				}

				#range.disabled {
					background-color: gray;
				}

				button {
					padding: 0px 2px 0px 2px;
					margin: 0px;
					line-height: 0px;
					color: darkred;
					font-size: 80%;
					border-color: dimgray;
				}

				button.disabled {
					color: gray;
				}
			</style>
			<button id="decrease">◀</button><div id="range"><div id="indicator">&nbsp;</div></div><button id="increase">▶</button>`;

		const updatingEventDispatcher = (valueUpdater) => {
			const previousValue = this.value;

			if (previousValue !== (this.value = valueUpdater())) {
				this.#update();
				this.dispatchEvent(new CustomEvent('change', { detail: { value: this.value } }));
			}
		};

		this.#rangeEl = this.shadowRoot.querySelector('#range');
		this.#rangeEl.addEventListener('click', (e) => {
			if (!this.#locked && !this.#disabled) {
				updatingEventDispatcher(() => Math.floor((e.offsetX / (e.target as HTMLElement).offsetWidth) * 100));
			}
		});
		this.#rangeEl.addEventListener('wheel', (e) => {
			if (!this.#locked && !this.#disabled) {
				e.preventDefault();
				updatingEventDispatcher(() => this.value + Math.sign(e.deltaY) * -10);
			}
		});

		this.#indicatorEl = this.shadowRoot.querySelector('#indicator');

		this.#decreaseButtonEl = this.shadowRoot.querySelector('#decrease');
		this.#decreaseButtonEl.addEventListener('click', (e) => {
			if (!this.#locked) {
				updatingEventDispatcher(() => this.value - 1);
			}
		});
		this.#increaseButtonEl = this.shadowRoot.querySelector('#increase');
		this.#increaseButtonEl.addEventListener('click', (e) => {
			if (!this.#locked) {
				updatingEventDispatcher(() => this.value + 1);
			}
		});

		this.value = 0;
	}

	connectedCallback() {
		const disabled = this.getAttribute('disabled') === '';
		this.#disabled = disabled;

		if (this.#disabled) {
			this.#rangeEl.classList.add('disabled');

			this.#decreaseButtonEl.classList.add('disabled');
			this.#increaseButtonEl.classList.add('disabled');
		}

		this.#decreaseButtonEl.disabled = this.#disabled;
		this.#increaseButtonEl.disabled = this.#disabled;
	}

	#update() {
		this.#indicatorEl.style.width = this.value + '%';
	}

	set value(value) {
		if (Number.isInteger(value)) {
			this.#value = Math.min(Math.max(0, value), 100);
			this.#update();
		}
	}

	get value() {
		return this.#value;
	}

	set locked(locked) {
		this.#locked = locked;

		if (locked) {
			this.#indicatorEl.classList.add('locked');
		} else {
			this.#indicatorEl.classList.remove('locked');
		}
	}

	get locked() {
		return this.#locked;
	}
}

customElements.define(Slider.NAME, Slider);
