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

	#selectAction;

	#blocked: boolean;

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

				#star-image.blocked {
					filter: grayscale(100%);
				}

				#name {
					margin-left: calc(-50%);
					margin-top: 36px;

					font-family: var(--theme-scifi-font);
				}
			</style>
			<div id="star"></div>
			<img id="star-image"></img>
			<span id="name"></span>`;

		this.#starEl = this.shadowRoot.querySelector('#star');
		this.#starImageEl = this.shadowRoot.querySelector('#star-image');

		this.#nameEl = this.shadowRoot.querySelector('#name');

		[this.#starEl, this.#starImageEl].forEach((starEl) =>
			starEl.addEventListener('click', (e) => {
				if (this.#selectAction && !this.#blocked) {
					HypermediaUtil.submitAction(this.#selectAction, {});
				}
			})
		);
	}

	render(data) {
		this.#selectAction = HypermediaUtil.getAction(data, 'select');
		this.#blocked = data.blocked;

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

			Reconciler.reconcileClass(this.#starImageEl, 'blocked', data.blocked);

			Reconciler.reconcileProperty(this.#starImageEl, 'hidden', false);
			Reconciler.reconcileStyle(this.#starEl, 'display', 'none');
		} else {
			Reconciler.reconcileStyle(
				this.#starEl,
				'background',
				`radial-gradient(${!data.blocked ? data.type : 'gray'}, #000)`,
				(elValue, value) =>
					!(data.blocked ? elValue.includes('gray') : elValue.toLowerCase().includes(data.type.toLowerCase()))
			);

			Reconciler.reconcileStyle(this.#starEl, 'display', 'inline-block');
			Reconciler.reconcileProperty(this.#starImageEl, 'hidden', true);
		}

		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			Reconciler.reconcileProperty(this.#nameEl, 'innerText', data.name ? data.name : '');
			Reconciler.reconcileStyle(
				this.#nameEl,
				'color',
				data.playerColor ? `var(--${data.playerColor}-player-color)` : '#3F3F3F',
				(elValue, value) => elValue.toLowerCase() !== value.toLowerCase()
			);
		}
	}
}

customElements.define(Star.NAME, Star);
