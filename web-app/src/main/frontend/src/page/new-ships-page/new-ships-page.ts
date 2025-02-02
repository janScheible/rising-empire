import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import ModalDialog from '~/component/modal-dialog';
import Ships from '~/component/ships';
import Action from '~/util/action';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class NewShipsPage extends HTMLElement {
	static NAME = 're-new-ships-page';

	#roundEl: HTMLSpanElement;
	#continueAction: Action;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
				<style>
					@import ${cssUrl('~/element.css', import.meta.url)};

					.ship-name {
						text-align: center;
					}
				</style>
				<${ModalDialog.NAME}>
					<${Container.NAME} border>
						<${ContainerTtile.NAME}>New ships in round <span id="round"></span></${ContainerTtile.NAME}>

						<${GridLayout.NAME} cols="1fr 1fr 1fr" rows="1fr 1fr" border>
							${NewShipsPage.#shipSlots(
								(index) => `
								
								<${FlowLayout.NAME} direction="column" id="new-ship-${index}">
									<${Ships.NAME}></${Ships.NAME}>
									<div class="ship-name">Scout</div>
								</${FlowLayout.NAME}>`
							)}
						</${GridLayout.NAME}>

						<${ContainerButtons.NAME}><button id="close-button">Close</button></${ContainerButtons.NAME}>
					</${Container.NAME}>
				</${ModalDialog.NAME}>`;

		this.#roundEl = this.shadowRoot.querySelector('#round');

		this.shadowRoot.querySelector('button').addEventListener('click', (e) => {
			HypermediaUtil.submitAction(this.#continueAction, {});
		});
	}

	static #shipSlots(template) {
		return Array(6)
			.fill(template)
			.map((template, index) => template(index))
			.join('');
	}

	render(data) {
		this.#continueAction = HypermediaUtil.getAction(data, 'continue');

		Reconciler.reconcileProperty(this.#roundEl, 'innerText', data.round);

		for (let i = 0; i < 6; i++) {
			const newShip = data.newShips[i];

			const newShipEl = this.shadowRoot.querySelector('#new-ship-' + i);
			Reconciler.reconcileStyle(newShipEl, 'visibility', newShip ? 'visible' : 'hidden');

			if (newShip) {
				const shipsEl = newShipEl.querySelector(Ships.NAME) as Ships;
				shipsEl.render(Object.assign({ playerColor: data.playerColor }, newShip));

				const nameEl = newShipEl.querySelector('.ship-name');
				Reconciler.reconcileProperty(nameEl, 'innerText', newShip.name);
			}
		}
	}
}

customElements.define(NewShipsPage.NAME, NewShipsPage);
