import FlowLayout from '~/component/flow-layout';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class SystemName extends HTMLElement {
	static NAME = 're-system-name';

	#systemNameEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#system-name {
					font-size: 200%;
					font-family: var(--theme-scifi-font);
				}
			</style>
			<${FlowLayout.NAME} axis-align="center"><span id="system-name"></span></${FlowLayout.NAME}>`;

		this.#systemNameEl = this.shadowRoot.querySelector('#system-name');
	}

	render(data) {
		Reconciler.reconcileProperty(this.#systemNameEl, 'innerText', data.name);
	}
}

customElements.define(SystemName.NAME, SystemName);
