import cssUrl from '~/util/cssUrl';

/**
 * Modal content that is shown on-top of everything.
 *
 * @element re-modal-dialog
 */
export default class ModalDialog extends HTMLElement {
	static NAME = 're-modal-dialog';

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					position: fixed;

					z-index: 10000;

					display: grid;
					place-items: center;

					top: 0;
					left: 0;
					width: 100vw;
					height: 100vh;

					background-color: rgba(0.0, 0.0, 0.0, 0.0);
				}
			</style>
			<slot></slot>`;

		this.addEventListener('click', (e) => {
			if (e.target === this) {
				this.dispatchEvent(new CustomEvent('modal-background-click', { composed: true, bubbles: true }));
			}
		});
	}

	connectedCallback() {}
}

customElements.define(ModalDialog.NAME, ModalDialog);
