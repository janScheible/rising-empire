import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import Container from '~/component/container';
import cssUrl from '~/util/cssUrl';

export default class StarNotification extends HTMLElement {
	static NAME = 're-star-notifications';

	#boxEl: HTMLDivElement;
	#textEl: HTMLSpanElement;

	#confirmAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
				
				:host {
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
		this.#textEl = this.shadowRoot.querySelector('#text');

		this.addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#confirmAction, {});
		});
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			Reconciler.reconcileStyle(this, 'width', data.starMapWidth + 'px');
			Reconciler.reconcileStyle(this, 'height', data.starMapHeight + 'px');

			Reconciler.reconcileStyle(this.#boxEl, 'left', data.x + 'px');
			Reconciler.reconcileStyle(this.#boxEl, 'top', data.y + 'px');

			this.#confirmAction = HypermediaUtil.getAction(data, 'confirm');
			Reconciler.reconcileProperty(this.#textEl, 'innerText', data.text);
		}
	}
}

customElements.define(StarNotification.NAME, StarNotification);

class Test extends HTMLElement {
	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>`;
	}
}
