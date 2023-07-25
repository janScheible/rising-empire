import cssUrl from '~/util/cssUrl';

/**
 * Layout that places the children in a CSS grid.
 *
 * @element re-grid-layout2
 *
 * @attr {Integer|grid-template-columns} cols - Either number of columns (each column treated as '1fr') or a grid-template-columns CSS property value.
 * @attr {Integer|grid-template-rows} rows - Either number of rows (each row treated as '1fr') or a grid-template-rows CSS property value.
 * @attr {'S'|'M' (default)|'L'|'XL'} gap - Size of grid (row and column) gap between the children (ignored when a border is enabled).
 * @attr {'S'|'M' (default)|'L'|'XL'} col-gap - Size of grid column gap between the children (ignored when a border is enabled).
 * @attr {'S'|'M' (default)|'L'|'XL'} row-gap - Size of grid row gap between the children (ignored when a border is enabled).
 * @attr {empty means all sides, {'left'|'right'|'top'|'bottom'}} border - If display but borders on all sides, otherwise only the specified sides.
 * @attr {'start'|'center'|'end'} row-align - Alignment of the children along the row axis.
 * @attr {'top'|'middle'|'bottom'} col-align - Alignment of the children along the column axis.
 *
 * @childattr {Integer} data-col-span - Number of columns a child is spanning.
 * @childattr {Integer} data-row-span - Number of rows a child is spanning.
 * @childattr {'left'|'center'|'right'} data-row-align - Alignment of a child along the row axis.
 * @childattr {'top'|'middle'|'bottom'} data-col-align - Alignment of a child along the column axis.
 * @childattr {empty attribute} data-no-padding - No padding for child.
 */
export default class GridLayout extends HTMLElement {
	static NAME = 're-grid-layout';

	static #GAP_MAPPING = {
		S: '2px',
		M: '4px',
		L: '8px',
		XL: '16px',
	};

	static #ROW_ALIGN_MAPPING = {
		left: 'flex-start',
		center: 'center',
		right: 'flex-end',
	};

	static #COL_ALIGN_MAPPING = {
		top: 'flex-start',
		middle: 'center',
		bottom: 'flex-end',
	};

	static #BORDER_WIDTH = '1px';

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: grid;
					grid-template-columns: var(--grid-layout-template-columns);
					grid-template-rows: var(--grid-layout-template-rows);
					gap: var(--row-gap) var(--column-gap);

					justify-items: var(--justify-items);
					align-items: var(--align-items);

					border-top: var(--border-top);
					border-right: var(--border-right);
					border-bottom: var(--border-bottom);
					border-left: var(--border-left);

					background-color: var(--background-color);
				}
			</style>
			<slot></slot>`;

		this.shadowRoot.querySelector('slot').addEventListener('slotchange', (e) => {
			for (const el of (e.target as HTMLSlotElement).assignedElements() as HTMLElement[]) {
				const colSpan = el.dataset.colSpan;
				if (colSpan) {
					el.style.gridColumn = 'span ' + colSpan;
				}

				const rowSpan = el.dataset.rowSpan;
				if (rowSpan) {
					el.style.gridRow = 'span ' + rowSpan;
				}

				if (!el.hasAttribute('data-no-padding') && this.hasBorder()) {
					el.style.padding = '2px';
				}

				if (this.hasBorder()) {
					el.style.backgroundColor = 'var(--theme-background-color)';
				}

				const justifySelf = GridLayout.#ROW_ALIGN_MAPPING[el.dataset.rowAlign];
				if (justifySelf) {
					el.style.justifySelf = justifySelf;
				}

				const alignSelf = GridLayout.#COL_ALIGN_MAPPING[el.dataset.colAlign];
				if (alignSelf) {
					el.style.alignSelf = alignSelf;
				}
			}
		});
	}

	connectedCallback() {
		for (const axis of [
			{ attributeName: 'cols', variableName: '--grid-layout-template-columns' },
			{ attributeName: 'rows', variableName: '--grid-layout-template-rows' },
		]) {
			const axisAttributeValue = this.getAttribute(axis.attributeName);
			if (axisAttributeValue) {
				if (parseInt(axisAttributeValue).toString() === axisAttributeValue) {
					const count = parseInt(this.getAttribute(axis.attributeName));
					const gridTemplateColumns = new Array(count).fill('1fr').join(' ');
					this.style.setProperty(axis.variableName, gridTemplateColumns);
				} else {
					this.style.setProperty(axis.variableName, axisAttributeValue);
				}
			}
		}

		const borderWidthFn = (side) =>
			this.getAttribute('border') === '' || this.getAttribute('border')?.includes(side) ? '1px' : '0px';
		['top', 'right', 'bottom', 'left'].forEach((side) =>
			this.style.setProperty('--border-' + side, borderWidthFn(side) + ' solid var(--theme-border-color)')
		);

		this.style.setProperty('--background-color', this.hasBorder() ? ' var(--theme-border-color)' : 'auto');

		const rowGap =
			GridLayout.#GAP_MAPPING[this.getAttribute('row-gap')] ??
			GridLayout.#GAP_MAPPING[this.getAttribute('gap')] ??
			'4px';
		this.style.setProperty('--row-gap', this.hasAttribute('border') ? GridLayout.#BORDER_WIDTH : rowGap);
		const colGap =
			GridLayout.#GAP_MAPPING[this.getAttribute('col-gap')] ??
			GridLayout.#GAP_MAPPING[this.getAttribute('gap')] ??
			'4px';
		this.style.setProperty('--column-gap', this.hasAttribute('border') ? GridLayout.#BORDER_WIDTH : colGap);

		const justifyItems = GridLayout.#ROW_ALIGN_MAPPING[this.getAttribute('row-align')];
		this.style.setProperty('--justify-items', justifyItems ?? 'legacy');

		const alignItems = GridLayout.#COL_ALIGN_MAPPING[this.getAttribute('col-align')];
		this.style.setProperty('--align-items', alignItems ?? 'normal');
	}

	hasBorder() {
		return this.hasAttribute('border');
	}
}

customElements.define(GridLayout.NAME, GridLayout);
