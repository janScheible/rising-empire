import cssUrl from '~/util/cssUrl';
import StarBackground from '~/component/star-background';

export default class RisingEmppireLogo extends HTMLElement {
	static NAME = 're-logo';

	#resizeObserver: ResizeObserver;
	#backgroundEl: StarBackground;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: grid;
					grid-template-columns: 100%;
  					grid-template-rows: 1fr;

					align-items: center;
					justify-items: center;
				}

				#title {
					font-size: 96px;

					-webkit-text-stroke-width: 2px;
    				-webkit-text-stroke-color: #dbb238;
					
					font-family: var(--theme-scifi-font);
				}

				.stacked {
					grid-area: 1 / 1 / 1 / 1;
				}
			</style>
			<canvas id="background" class="stacked" is="${StarBackground.NAME}" height="160"></canvas>
			<div id="title" class="stacked" part="text">Rising Empire</div>`;

		this.#backgroundEl = this.shadowRoot.querySelector('#background');
	}

	connectedCallback() {
		if (!this.#resizeObserver) {
			this.#resizeObserver = new ResizeObserver((entries) => {
				// this avoids 'ResizeObserver loop completed with undelivered notifications.'... but not sure if really a good idea ;-(
				setTimeout(() =>
					this.#backgroundEl.setAttribute('width', entries[0].borderBoxSize[0].inlineSize.toString())
				);
			});
			this.#resizeObserver.observe(this);
		}
	}

	disconnectedCallback() {
		if (this.#resizeObserver) {
			this.#resizeObserver.unobserve(this);
		}
	}
}

customElements.define(RisingEmppireLogo.NAME, RisingEmppireLogo);
