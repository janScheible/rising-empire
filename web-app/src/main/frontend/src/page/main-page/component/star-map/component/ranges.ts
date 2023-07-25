import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Ranges extends HTMLElement {
	static NAME = 're-ranges';

	#fleetRangesMaskEl: SVGMaskElement;
	#extendedFleetRangesMaskEl: SVGMaskElement;
	#scannerRangesClipPathEl: SVGClipPathElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
				
				:host {
					position: absolute;

					width: var(--ranges-width);
					height: var(--ranges-height);

					pointer-events: none;
				}

				svg {
					width: 100%;
					height: 100%;
				}

				.fleet-range {
					stroke: white;
					stroke-width: 4;
				}
					
				.fleet-range.inner {
					stroke: none;
					fill: black;
				}

				#fleet-ranges {
					fill: var(--ranges-scanner-color);
					filter: opacity(60%);
				}

				#extended-fleet-ranges {
					fill: var(--ranges-scanner-color);
					filter: opacity(30%);
				}

				#scanner-ranges {
					fill: var(--ranges-scanner-color);
					filter: opacity(30%);
				}
			</style>
			<svg xmlns="http://www.w3.org/2000/svg">
				<g>
					<mask id="fleet-ranges-mask"/>
					<rect id="fleet-ranges" width="100%" height="100%" mask="url(#fleet-ranges-mask)"/>
				</g>
				<g>
					<mask id="extended-fleet-ranges-mask"/>
					<rect id="extended-fleet-ranges" width="100%" height="100%" mask="url(#extended-fleet-ranges-mask)"/>
				</g>				
				<g>
					<defs><clipPath id="scanner-ranges-clip-path"></clipPath></defs>
					<rect id="scanner-ranges" width="100%" height="100%" clip-path="url(#scanner-ranges-clip-path)"/>
				</g>
			</svg>`;

		this.#fleetRangesMaskEl = this.shadowRoot.querySelector('#fleet-ranges-mask');
		this.#extendedFleetRangesMaskEl = this.shadowRoot.querySelector('#extended-fleet-ranges-mask');
		this.#scannerRangesClipPathEl = this.shadowRoot.querySelector('#scanner-ranges-clip-path');
	}

	render(data) {
		Reconciler.reconcileCssVariable(this, 'ranges-width', data.starMapWidth + 'px');
		Reconciler.reconcileCssVariable(this, 'ranges-height', data.starMapHeight + 'px');

		Reconciler.reconcileCssVariable(this, 'ranges-scanner-color', `var(--${data.playerColor}-player-color)`);

		const renderRangeCircle = (circleEl: SVGCircleElement, fleetRange) => {
			Reconciler.reconcileClass(circleEl, 'fleet-range', true);
			Reconciler.reconcileAttribute(circleEl, 'cx', fleetRange.centerX);
			Reconciler.reconcileAttribute(circleEl, 'cy', fleetRange.centerY);
			Reconciler.reconcileAttribute(circleEl, 'r', fleetRange.radius);
		};

		const reconcileFleetRanges = (maskEl, fleetRanges) => {
			Reconciler.reconcileChildren(
				maskEl,
				maskEl.querySelectorAll('circle:not(.inner)'),
				fleetRanges,
				'svg:circle',
				{
					renderCallbackFn: renderRangeCircle,
					afterCreateCallbackFn: (circleEl: SVGCircleElement, fleetRange) => {
						const innerCircleEl = circleEl.cloneNode() as SVGCircleElement;
						renderRangeCircle(innerCircleEl, fleetRange);
						innerCircleEl.dataset.fleetRangeId = fleetRange.id;
						Reconciler.reconcileClass(innerCircleEl, 'inner', true);
						maskEl.appendChild(innerCircleEl);
					},
					beforeDeleteCallbackFn: (circleEl: SVGCircleElement) => {
						let innerCircleEl = maskEl.querySelector(
							`circle.inner[data-fleet-range-id="${circleEl.dataset.fleetRangeId}"]`
						);
						circleEl.parentElement.removeChild(innerCircleEl);
					},
					idAttributName: 'data-fleet-range-id',
					insertionMode: 'PREPEND',
				}
			);
		};

		reconcileFleetRanges(this.#fleetRangesMaskEl, data.fleetRanges);
		reconcileFleetRanges(
			this.#extendedFleetRangesMaskEl,
			data.fleetRanges.map((fleetRange) => ({
				id: fleetRange.id,
				centerX: fleetRange.centerX,
				centerY: fleetRange.centerY,
				radius: fleetRange.extendedRadius,
			}))
		);

		Reconciler.reconcileChildren(
			this.#scannerRangesClipPathEl,
			this.#scannerRangesClipPathEl.querySelectorAll('circle'),
			data.colonyScannerRanges.concat(data.fleetScannerRanges),
			'svg:circle',
			{
				renderCallbackFn: (circleEl: SVGCircleElement, scannerRange) => {
					Reconciler.reconcileAttribute(circleEl, 'cx', scannerRange.centerX);
					Reconciler.reconcileAttribute(circleEl, 'cy', scannerRange.centerY);
					Reconciler.reconcileAttribute(circleEl, 'r', scannerRange.radius);
				},
				idAttributName: 'data-scanner-range-id',
			}
		);
	}
}

customElements.define(Ranges.NAME, Ranges);
