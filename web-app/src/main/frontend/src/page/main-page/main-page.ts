import StarMap from '~/page/main-page/component/star-map/star-map';
import Inspector from '~/page/main-page/component/inspector/inspector';
import ButtonBar from '~/page/main-page/component/button-bar';
import HypermediaUtil from '~/util/hypermedia-util';
import TurnStatusDialog from '~/page/main-page/component/turn-finish-dialog';
import Container from '~/component/container';
import FlowLayout from '~/component/flow-layout';
import Reconciler from '~/util/reconciler';
import cssUrl from '~/util/cssUrl';
import ConnectionIndicator from '~/component/connection-indicator';

export default class MainPage extends HTMLElement {
	static NAME = 're-main-page';

	#starMapEl: StarMap;
	#inspectorEl: Inspector;
	#buttonBarEl: ButtonBar;
	#roundEl: HTMLSpanElement;
	#connectionIndicatorEl: ConnectionIndicator;
	#turnStatusDialogEl: TurnStatusDialog;

	#fleetMovementsAction;
	#beginNewTurnAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#container {
					height: 100%;
				}

				#round-text-wrapper {
					position: fixed;
					
					left: 0px;
					right: 0px;
					top: 4px;

					z-index: 2000;
				}

				#round-text-center-wrapper {
					text-align: center;
				}

				#round-text {
					padding: 0px 4px;
					font-family: var(--theme-scifi-font);
					background-color: var(--theme-background-color);
				}
			</style>			

			<${Container.NAME} id="container" gap="M">
				<${FlowLayout.NAME} data-fill-vertically gap="M">
					<${StarMap.NAME} data-flow-size="1fr"></${StarMap.NAME}>
					<${Inspector.NAME}></${Inspector.NAME}>					
				</${FlowLayout.NAME}>

				<${ButtonBar.NAME}></${ButtonBar.NAME}>
			</${Container.NAME}>

			<div id="round-text-wrapper">
				<div id="round-text-center-wrapper">
					<span id="round-text">
						<${ConnectionIndicator.NAME}></${ConnectionIndicator.NAME}> round <span id="round"></span>
					</span>
				</div>
			</div>

			<${TurnStatusDialog.NAME} hidden></${TurnStatusDialog.NAME}>`;

		this.#starMapEl = this.shadowRoot.querySelector(StarMap.NAME);
		this.#inspectorEl = this.shadowRoot.querySelector(Inspector.NAME);
		this.#buttonBarEl = this.shadowRoot.querySelector(ButtonBar.NAME);
		this.#roundEl = this.shadowRoot.querySelector('#round');
		this.#connectionIndicatorEl = this.shadowRoot.querySelector(ConnectionIndicator.NAME);
		this.#turnStatusDialogEl = this.shadowRoot.querySelector(TurnStatusDialog.NAME);
	}

	async render(data) {
		this.#fleetMovementsAction = HypermediaUtil.getAction(data, 'fleet-movements');
		this.#beginNewTurnAction = HypermediaUtil.getAction(data, 'begin-new-turn');

		const starMapAnimation = this.#starMapEl.render(data.starMap);
		this.#inspectorEl.render(data.inspector);
		this.#buttonBarEl.render(data.buttonBar);
		Reconciler.reconcileProperty(this.#roundEl, 'innerText', data.round);
		this.#turnStatusDialogEl.render(data.turnStatus);

		Reconciler.reconcileAttribute(this.#connectionIndicatorEl, 'title', sessionStorage.getItem('sessionId'));

		return starMapAnimation;
	}

	updateTurnStatus(playerStatus) {
		this.#turnStatusDialogEl.updateTurnStatus(playerStatus);
	}

	fleetMovements() {
		return HypermediaUtil.submitAction(this.#fleetMovementsAction, {});
	}

	beginNewTurn() {
		return HypermediaUtil.submitAction(this.#beginNewTurnAction, {});
	}

	getStarMapViewport() {
		return this.#starMapEl.getStarMapViewport();
	}

	showConnected(connected: boolean) {
		Reconciler.reconcileProperty(this.#connectionIndicatorEl, 'connected', connected);
	}
}

customElements.define(MainPage.NAME, MainPage);
