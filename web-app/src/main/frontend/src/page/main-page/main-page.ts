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
import MainPageState from '~/page/main-page/main-page-state';

export default class MainPage extends HTMLElement {
	static NAME = 're-main-page';

	#starMapEl: StarMap;
	#inspectorEl: Inspector;
	#buttonBarEl: ButtonBar;
	#roundEl: HTMLSpanElement;
	#connectionIndicatorEl: ConnectionIndicator;
	#turnStatusDialogEl: TurnStatusDialog;

	#selfAction;

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
					top: 3px;

					z-index: 2000;

					pointer-events: none;
				}

				#round-text-center-wrapper {
					text-align: center;
				}

				#round-text {
					padding: 0px 4px;

					font-family: var(--theme-scifi-font);
					background-color: var(--theme-background-color);

					pointer-events: auto;
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
		this.#starMapEl.addEventListener('notifications-done', (e: CustomEvent) => {
			this.#state.onNotificationsDone(this.#selfAction, e.detail.starId);
		});

		this.#inspectorEl = this.shadowRoot.querySelector(Inspector.NAME);
		this.#buttonBarEl = this.shadowRoot.querySelector(ButtonBar.NAME);
		this.#roundEl = this.shadowRoot.querySelector('#round');
		this.#connectionIndicatorEl = this.shadowRoot.querySelector(ConnectionIndicator.NAME);
		this.#turnStatusDialogEl = this.shadowRoot.querySelector(TurnStatusDialog.NAME);
	}

	#state = new MainPageState();

	async render(data) {
		this.#state.next(data);

		data.starMap.miniMap = this.#state.miniMap;
		data.starMap.fleetMovements = this.#state.fleetMovements;

		this.#selfAction = HypermediaUtil.getAction(data, '_self');

		const starMap = Object.assign({}, data.starMap, {
			// also display destroyed fleets (they will vanish as soon as the spaceCombats will be removed from the state)
			fleets: Object.values(data.starMap.fleets).flat().concat(this.#state.destroyedFleets),
			starNotifications: this.#state.isNotificationState() ? this.#state.starNotifications : [],
		});
		await this.#starMapEl.render(starMap);

		if (this.#state.isTurnState() && this.#state.didStateChange() && data.starMap.starSelection) {
			this.#starMapEl.centerStar(data.starMap.starSelection.x, data.starMap.starSelection.y);
		}

		this.#inspectorEl.render(data.inspector);
		this.#buttonBarEl.render(data.buttonBar);
		Reconciler.reconcileProperty(this.#roundEl, 'innerText', data.round);
		this.#turnStatusDialogEl.render(data.turnStatus);

		Reconciler.reconcileAttribute(this.#connectionIndicatorEl, 'title', sessionStorage.getItem('sessionId'));

		if (this.#state.isFleetMovementsState()) {
			// after animating the fleet movements we need to trigger the next step automatically
			await this.render(data);
		}
	}

	updateTurnStatus(playerStatus) {
		this.#turnStatusDialogEl.updateTurnStatus(playerStatus);
	}

	roundFinished() {
		HypermediaUtil.submitAction(this.#selfAction, { partial: false });
	}

	showConnected(connected: boolean) {
		Reconciler.reconcileProperty(this.#connectionIndicatorEl, 'connected', connected);
	}
}

customElements.define(MainPage.NAME, MainPage);
