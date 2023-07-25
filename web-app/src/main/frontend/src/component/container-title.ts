import cssUrl from '~/util/cssUrl';

export default class ContainerTtile extends HTMLElement {
	static NAME = 're-container-title';

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					padding: 8px;
					
					background-color: var(--theme-container-title-background-color);
					color: var(--theme-container-title-color);
					font-weight: bold
				}
			</style>
			<slot></slot>`;

		this.shadowRoot.querySelector('slot').addEventListener('slotchange', (e) => {});
	}

	connectedCallback() {}
}

customElements.define(ContainerTtile.NAME, ContainerTtile);
