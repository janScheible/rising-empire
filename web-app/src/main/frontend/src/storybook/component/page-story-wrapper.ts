import cssUrl from '~/util/cssUrl';
import Renderable from '~/util/renderable';

/**
 * Provides facilites for page stories. This includes an open button and support for a hypermedia close action.
 */
export default class PageStoryWrapper extends HTMLElement {
	static NAME = 're-page-story-wrapper';

	#closeActionName;

	/**
	 * 'Cache' render data to re-apply it when the open button is clicked.
	 */
	#pageData;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>
			<button id="open-button">Open</button>
			<slot></slot>`;

		this.shadowRoot
			.querySelector('#open-button')
			.addEventListener('click', (e) => this.#hideWrappedChildren(false));

		this.addEventListener('hypermedia-submit', (e: CustomEvent) => {
			if (this.#closeActionName && e.detail.action.name === this.#closeActionName) {
				this.#hideWrappedChildren(true);
			}
		});

		this.shadowRoot.addEventListener('modal-background-click', (e) => {
			this.#hideWrappedChildren(true);
		});
	}

	#hideWrappedChildren(hide) {
		this.shadowRoot
			.querySelector('slot')
			.assignedNodes()
			.forEach((slottedEl: HTMLElement & Renderable) => {
				slottedEl.hidden = hide;

				if (!hide && typeof slottedEl['render'] === 'function') {
					slottedEl.render(this.#pageData);
				}
			});
	}

	connectedCallback() {
		this.#closeActionName = this.getAttribute('close-action');
	}

	render(data) {
		this.#pageData = data;
		this.#hideWrappedChildren(false);
	}

	close() {
		this.#hideWrappedChildren(true);
	}
}

customElements.define(PageStoryWrapper.NAME, PageStoryWrapper);
