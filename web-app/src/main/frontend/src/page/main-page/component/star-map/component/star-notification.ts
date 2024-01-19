import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import Container from '~/component/container';
import cssUrl from '~/util/cssUrl';

export default class StarNotification extends HTMLElement {
	static NAME = 're-star-notifications';

	#boxEl: HTMLDivElement;
	#textEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
				
				:host {
					width: 100%;
					height: 100%;

					position: absolute;
				}

				#box {
					position: relative;

					max-width: 250px;
				}
			</style>
			<${Container.NAME} id="box" notification gap="M">
				<span id="text"></span><br>
			</${Container.NAME}>`;

		this.#boxEl = this.shadowRoot.querySelector('#box');
		this.#boxEl.addEventListener('click', (e) => {
			this.dispatchEvent(
				new CustomEvent('notification-confirm', {
					detail: { starId: this.getAttribute('id').replace('notification-', '') },
				})
			);
		});

		this.#textEl = this.shadowRoot.querySelector('#text');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			Reconciler.reconcileAttribute(this, 'id', `notification-${data.starId}`);
			Reconciler.reconcileStyle(this.#boxEl, 'left', data.x + 'px');
			Reconciler.reconcileStyle(this.#boxEl, 'top', data.y + 'px');

			Reconciler.reconcileProperty(this.#textEl, 'innerText', data.text);
		}
	}
}

customElements.define(StarNotification.NAME, StarNotification);
