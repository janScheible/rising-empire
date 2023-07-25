import cssUrl from '~/util/cssUrl';

/**
 * Container button element.
 *
 * @element re-container-buttons
 *
 * @attr {empty attribute} fill-horizontally - To span the whole container width.
 */
export default class ContainerButtons extends HTMLElement {
	static NAME = 're-container-buttons';

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;
					flex-direction: row;
				}

				#spacer {
					display: var(--spacer-display);
					flex: 1;
				}

				#buttons {
					display: grid;
					grid-gap: 4px;
					grid-template-columns: var(--buttons-grid-template-columns);
					flex-grow: var(--buttons-flex-grow);
				}

				::slotted(button) {
					min-width: var(--button-min-width);
				}
			</style>
			<div id="spacer">&nbsp;</div>
			<div id="buttons"><slot></slot></div>`;

		this.shadowRoot.querySelector('slot').addEventListener('slotchange', (e) => {
			const assignedElements = (e.target as HTMLSlotElement).assignedElements() as HTMLElement[];

			this.style.setProperty(
				'--buttons-grid-template-columns',
				new Array(assignedElements.length).fill('1fr').join(' ')
			);
		});
	}

	connectedCallback() {
		const fillHorizontally = this.hasAttribute('fill-horizontally');
		this.style.setProperty('--spacer-display', fillHorizontally ? 'none' : 'block');
		this.style.setProperty('--buttons-flex-grow', fillHorizontally ? '1' : '0');

		this.style.setProperty('--button-min-width', fillHorizontally ? '0px' : '80px');
	}
}

customElements.define(ContainerButtons.NAME, ContainerButtons);
