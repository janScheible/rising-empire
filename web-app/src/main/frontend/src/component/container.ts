import ContainerTtile from '~/component/container-title';
import cssUrl from '~/util/cssUrl';

/**
 * Top-level wrapper that places children vertically next to each other with margins applied.
 *
 * @element re-container
 *
 * @attr {'S'|'M'|'L'|'XL' (default)|pixel width} gap - Size of gap between the children and outer gap.
 * @attr {pixel width} outer-gap - Size of outer gap (overrides gap).
 * @attr {empty attribute} border - Display a border.
 * @attr {empty attribute} notification - Style a container as a notification (always includes border).
 *
 * @childattr {empty attribute} data-fill-vertically - Let the child fill the remaining vertical space.
 */
export default class Container extends HTMLElement {
	static NAME = 're-container';

	static #GAP_MARGIN_MAPPING = {
		S: '2px',
		M: '4px',
		L: '8px',
		XL: '16px',
	};

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;
					flex-direction: column;

					background-color: var(--background-color);
					color: black;

					border: var(--border-size) solid var(--theme-border-color)
				}
			</style>
			<slot></slot>`;

		this.shadowRoot.querySelector('slot').addEventListener('slotchange', (e) => {
			let isFirst = true;
			const margin =
				this.hasAttribute('gap') && this.getAttribute('gap').endsWith('px')
					? this.getAttribute('gap')
					: Container.#GAP_MARGIN_MAPPING[this.getAttribute('gap')] ?? '16px';
			const outerMargin = this.hasAttribute('outer-gap') ? this.getAttribute('outer-gap') : margin;

			for (const el of (e.target as HTMLSlotElement).assignedElements() as HTMLElement[]) {
				if (!(el instanceof ContainerTtile)) {
					if (isFirst) {
						el.style.margin = `${outerMargin} ${outerMargin} ${margin} ${outerMargin}`;
					} else {
						el.style.margin = `0px ${outerMargin} ${margin} ${outerMargin}`;
					}

					isFirst = false;
				}

				const fillVertically = el.hasAttribute('data-fill-vertically');
				if (fillVertically) {
					el.style.flex = '1';
					el.style.minHeight = '0px';
				}
			}
		});
	}

	connectedCallback() {
		let borderSize = '0px';

		if (this.hasAttribute('border')) {
			borderSize = '1px';
		}

		if (this.hasAttribute('notification')) {
			borderSize = '1px';
			this.style.setProperty('--background-color', '#ffffff');
		} else {
			this.style.setProperty('--background-color', 'var(--theme-background-color)');
		}

		this.style.setProperty('--border-size', borderSize);
	}
}

customElements.define(Container.NAME, Container);
