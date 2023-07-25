import cssUrl from '~/util/cssUrl';

/**
 * Layout that places children next to each other in a CSS flex layout (either horizontally (default) or vertically).
 *
 * @element re-flow-layout
 *
 * @attr {'row' (default)|'column'} direction - The flow direction.
 * @attr {'S'|'M' (default)|'L'|'XL'} gap - Size of gap between the children.
 * @observedattr {'start'|'center'|'end'} axis-align - Alignment of the children along the main axis.
 * @observedattr {'start'|'center'|'end'} cross-axis-align - Alignment of the children along the cross axis.
 *
 * @childattr {fraction|pixel width} data-flow-size - Either a fraction or absolute pixel value.
 * @childattr {'start'|'center'|'end'} data-cross-axis-align - Alignment of a child along the cross axis.
 */
export default class FlowLayout extends HTMLElement {
	static NAME = 're-flow-layout';

	static #GAP_MARGIN_MAPPING = {
		S: '2px',
		M: '4px',
		L: '8px',
		XL: '16px',
	};

	static #AXIS_ALIGN_MAPPING = {
		start: 'flex-start',
		center: 'center',
		end: 'flex-end',
	};

	static #CROSS_AXIS_ALIGN_MAPPING = {
		start: 'flex-start',
		center: 'center',
		end: 'flex-end',
	};

	static get observedAttributes() {
		return ['axis-align', 'cross-axis-align'];
	}

	#slotEl: HTMLSlotElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;
					flex-direction: var(--flex-direction);
					justify-content: var(--justify-content);
					align-items: var(--align-items);
				}
			</style>
			<slot></slot>`;

		this.#slotEl = this.shadowRoot.querySelector('slot');
		this.#slotEl.addEventListener('slotchange', (e) => {
			this.#updateMargins(e.target as HTMLSlotElement);
		});
	}

	connectedCallback() {
		this.style.setProperty('--flex-direction', this.getAttribute('direction') === 'column' ? 'column' : 'row');

		this.#setAxisAlign();
		this.#setCrossAxisAlign();
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if (oldValue !== newValue) {
			if (name === 'axis-align') {
				this.#setAxisAlign();
			} else if (name === 'cross-axis-align') {
				this.#setCrossAxisAlign();
			}
		}
	}

	#setAxisAlign() {
		this.style.setProperty(
			'--justify-content',
			FlowLayout.#AXIS_ALIGN_MAPPING[this.getAttribute('axis-align')] ?? 'normal'
		);
	}

	#setCrossAxisAlign() {
		this.style.setProperty(
			'--align-items',
			FlowLayout.#CROSS_AXIS_ALIGN_MAPPING[this.getAttribute('cross-axis-align')] ?? 'normal'
		);
	}

	/**
	 * Margins are only automatically updated when a element in the default slot is added or removed, hiding an
	 * child is for example not detected and requires a call of this method.
	 */
	updateMargins() {
		this.#updateMargins(this.#slotEl);
	}

	#updateMargins(slotEl: HTMLSlotElement) {
		const isVertical = this.getAttribute('direction') === 'column';
		const margin = FlowLayout.#GAP_MARGIN_MAPPING[this.getAttribute('gap')] ?? '4px';

		let isFirst = true;

		for (const el of slotEl.assignedElements() as HTMLElement[]) {
			const flowSize = el.dataset.flowSize;
			if (flowSize) {
				if (flowSize.endsWith('px')) {
					el.style.flexBasis = flowSize;
					el.style.flexShrink = '0';
				} else if (flowSize.endsWith('fr')) {
					el.style.flexGrow = parseInt(flowSize).toString();
				}
			}

			const alignSelf = FlowLayout.#CROSS_AXIS_ALIGN_MAPPING[el.dataset.crossAxisAlign];
			if (alignSelf) {
				el.style.alignSelf = alignSelf;
			}

			if (!isFirst && !el.hidden) {
				if (isVertical) {
					el.style.marginTop = margin;
				} else {
					el.style.marginLeft = margin;
				}
			} else {
				if (isVertical) {
					el.style.marginTop = '0px';
				} else {
					el.style.marginLeft = '0px';
				}
			}

			if (!el.hidden) {
				isFirst = false;
			}
		}
	}
}

customElements.define(FlowLayout.NAME, FlowLayout);
