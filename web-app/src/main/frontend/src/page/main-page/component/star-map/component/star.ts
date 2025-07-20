import Reconciler from '~/util/reconciler';
import HypermediaUtil from '~/util/hypermedia-util';
import cssUrl from '~/util/cssUrl';
import Theme from '~/theme/theme';

export default class Star extends HTMLElement {
	static NAME = 're-star';

	static WIDTH = 32;
	static HEIGHT = 32;

	#starEl: HTMLDivElement;
	#starImageEl: HTMLImageElement;
	#nameEl: HTMLSpanElement;
	#siegeProgressEl: HTMLSpanElement;

	#selectAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
				
				:host {
					position: absolute;

					left: var(--star-left);
					top: var(--star-top);

					display: flex;
					flex-direction: column;
				}

				#star {
					display: inline-block;
					
					width: var(--star-width);
					height: var(--star-height);
					margin: var(--star-margin);

					border-radius: 50%;
					
					filter: blur(4px) saturate(130%) brightness(130%);
				}

				#star-image {
					width: var(--star-width);
					height: var(--star-height);
					margin: var(--star-margin);
				}

				#name-wrapper {
					position: relative;
  					left: calc(-50% + 16px);
					top: 5px;

					text-align: center;

					white-space: nowrap;
					font-family: var(--theme-scifi-font);
				}
			</style>
			<div id="star"></div>
			<img id="star-image"></img>
			<span id="name-wrapper">
				<span id="name"></span>
				<span id="siege-progress"> <span style="background-image: conic-gradient(var(--star-siege-player-color) var(--star-siege-progress), var(--star-player-color) var(--star-siege-progress)); border-radius: 50%;">   </span></span>
			</span>`;

		this.#starEl = this.shadowRoot.querySelector('#star');
		this.#starImageEl = this.shadowRoot.querySelector('#star-image');

		this.#nameEl = this.shadowRoot.querySelector('#name');
		this.#siegeProgressEl = this.shadowRoot.querySelector('#siege-progress');

		[this.#starEl, this.#starImageEl].forEach((starEl) =>
			starEl.addEventListener('click', (e) => {
				if (this.#selectAction) {
					HypermediaUtil.submitAction(this.#selectAction, {});
				}
			})
		);
	}

	render(data) {
		this.#selectAction = HypermediaUtil.getAction(data, 'select');

		Reconciler.reconcileCssVariable(this, 'star-left', data.x - Star.WIDTH / 2 + 'px');
		Reconciler.reconcileCssVariable(this, 'star-top', data.y - Star.HEIGHT / 2 + 'px');

		const starQualifier = `star-${data.type.toLowerCase()}${data.small ? '-small' : ''}`;
		const starImageDataUrl = Theme.getDataUrl(starQualifier);

		const effectiveStarEl = starImageDataUrl ? this.#starImageEl : this.#starEl;
		Reconciler.reconcileCssVariable(effectiveStarEl, 'star-width', (data.small ? '20' : Star.WIDTH) + 'px');
		Reconciler.reconcileCssVariable(effectiveStarEl, 'star-height', (data.small ? '20' : Star.WIDTH) + 'px');
		Reconciler.reconcileCssVariable(effectiveStarEl, 'star-margin', (data.small ? '6' : '0') + 'px');

		if (starImageDataUrl) {
			Reconciler.reconcileAttribute(this.#starImageEl, 'src', starImageDataUrl);

			Reconciler.reconcileProperty(this.#starImageEl, 'hidden', false);
			Reconciler.reconcileStyle(this.#starEl, 'display', 'none');
		} else {
			Reconciler.reconcileStyle(
				this.#starEl,
				'background',
				`radial-gradient(${data.type}, #000)`,
				(elValue, value) => !elValue.toLowerCase().includes(data.type.toLowerCase())
			);

			Reconciler.reconcileStyle(this.#starEl, 'display', 'inline-block');
			Reconciler.reconcileProperty(this.#starImageEl, 'hidden', true);
		}

		Reconciler.reconcileProperty(this.#nameEl, 'innerText', Theme.getSystemName(data.name) ?? '');
		Reconciler.reconcileStyle(
			this.#nameEl,
			'color',
			data.playerColor ? `var(--${data.playerColor}-player-color)` : '#3F3F3F',
			(elValue, value) => elValue.toLowerCase() !== value.toLowerCase()
		);

		if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#siegeProgressEl, data.siegeProgress === undefined)) {
			Reconciler.reconcileCssVariable(this.#siegeProgressEl, 'star-siege-progress', data.siegeProgress + '%');

			Reconciler.reconcileCssVariable(
				this.#siegeProgressEl,
				'star-player-color',
				`var(--${data.playerColor}-player-color)`
			);

			Reconciler.reconcileCssVariable(
				this.#siegeProgressEl,
				'star-siege-player-color',
				`var(--${data.siegePlayerColor}-player-color)`
			);
		}
	}
}

customElements.define(Star.NAME, Star);
