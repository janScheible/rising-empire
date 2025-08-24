import Container from '~/component/container';
import ContainerTtile from '~/component/container-title';
import ModalDialog from '~/component/modal-dialog';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class VictoryDefeatPage extends HTMLElement {
	static NAME = 're-victory-defeat-page';

	#titleEl: HTMLSpanElement;
	#victoryEl: HTMLSpanElement;
	#defeatEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>
			<${ModalDialog.NAME}>
				<${Container.NAME} border>
					<${ContainerTtile.NAME} id="title">Victory/Defeat</${ContainerTtile.NAME}>
					<div>
						<div id="victory">Your empire truly rose above all others. You now rule over the entire known universe.</div>
						<div id="defeat">All your colonies are lost. Your empire did not stand the test of time and will be forgotten soon...</div>
					</div>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#titleEl = this.shadowRoot.querySelector('#title');
		this.#victoryEl = this.shadowRoot.querySelector('#victory');
		this.#defeatEl = this.shadowRoot.querySelector('#defeat');
	}

	render(data) {
		Reconciler.reconcileProperty(this.#titleEl, 'innerText', data.victory ? 'Victory' : 'Defeat');

		Reconciler.reconcileProperty(this.#victoryEl, 'hidden', !data.victory);
		Reconciler.reconcileProperty(this.#defeatEl, 'hidden', data.victory);
	}
}

customElements.define(VictoryDefeatPage.NAME, VictoryDefeatPage);
