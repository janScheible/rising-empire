import MainPage from '~/page/main-page/main-page';
import NewGamePage from '~/page/new-game-page/new-game-page';
import TechPage from '~/page/tech-page/tech-page';
import SelectTechPage from '~/page/select-tech-page/select-tech-page';
import SpaceCombatPage from '~/page/space-combat-page/space-combat-page';
import Reconciler from '~/util/reconciler';
import cssUrl from '~/util/cssUrl';
import ThemeManager from '~/component/theme-manager';
import Container from '~/component/container';
import ModalDialog from '~/component/modal-dialog';
import ContainerTtile from '~/component/container-title';

export default class Frontend extends HTMLElement {
	static NAME = 're-frontend';

	#themeManagerContainerEl: Container;
	#themeManagerEl: ThemeManager;

	#playerErrorContainerEl: ModalDialog;
	#playerErrorTitleEl: ContainerTtile;
	#playerErrorTextEl: HTMLDivElement;

	#mainPageEl: MainPage;
	#newGamePageEl: NewGamePage;
	#techPageEl: TechPage;
	#selectTechPageEl: SelectTechPage;
	#spaceCombatPageEl: SpaceCombatPage;
	#loadIndicatorEl: HTMLDivElement;

	#pageMapping;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-block;
					position: relative;

					width: 100vw;
					height: 100vh;
				}

				#load-indicator {
					position: absolute;

					left: 0px;
					top: 0px;
					right: 0px;
					bottom: 0px;

					z-index: 10000;
				}

				#load-indicator > .animation {
					background-color: blue;
					background-image: linear-gradient(to right, var(--theme-background-color) 35%, rgba(0, 0, 0, 0.0) 50%, var(--theme-background-color) 65%);
					height: 4px;
					width: 100%;
					animation: move-highlight 1s linear 0s infinite alternate;
				}
				  
				@keyframes move-highlight {
					from { margin-left: -40%; } to { margin-left: 40%; }
				}

				#theme-manager-container {
					height: 100%;
				}

				#player-error-container {
					height: 100%;
				}

				#player-error-text {
					max-width: 300px;
				}
			</style>

			<${Container.NAME} id="theme-manager-container">
				<${ModalDialog.NAME}>
					<${ThemeManager.NAME}></${ThemeManager.NAME}>
				</${ModalDialog.NAME}>
			</${Container.NAME}>

			<${Container.NAME} id="player-error-container" hidden>
				<${ModalDialog.NAME}>
					<${Container.NAME} border>
						<${ContainerTtile.NAME} id="player-error-title"></${ContainerTtile.NAME}>
						<div id="player-error-text"></div>
					</${Container.NAME}>
				</${ModalDialog.NAME}>
			</${Container.NAME}>

			<${MainPage.NAME} hidden></${MainPage.NAME}>
			<${NewGamePage.NAME} hidden class="page"></${NewGamePage.NAME}>
			<${TechPage.NAME} hidden class="page"></${TechPage.NAME}>
			<${SelectTechPage.NAME} hidden class="page"></${SelectTechPage.NAME}>
			<${SpaceCombatPage.NAME} hidden class="page"></${SpaceCombatPage.NAME}>
			<div id="load-indicator"><div class="animation"></div></div>`;

		this.#themeManagerContainerEl = this.shadowRoot.querySelector('#theme-manager-container');
		this.#themeManagerEl = this.shadowRoot.querySelector(ThemeManager.NAME);

		this.#playerErrorContainerEl = this.shadowRoot.querySelector('#player-error-container');
		this.#playerErrorTitleEl = this.shadowRoot.querySelector('#player-error-title');
		this.#playerErrorTextEl = this.shadowRoot.querySelector('#player-error-text');

		this.#mainPageEl = this.shadowRoot.querySelector(MainPage.NAME);
		this.#newGamePageEl = this.shadowRoot.querySelector(NewGamePage.NAME);
		this.#techPageEl = this.shadowRoot.querySelector(TechPage.NAME);
		this.#selectTechPageEl = this.shadowRoot.querySelector(SelectTechPage.NAME);
		this.#spaceCombatPageEl = this.shadowRoot.querySelector(SpaceCombatPage.NAME);
		this.#loadIndicatorEl = this.shadowRoot.querySelector('#load-indicator');

		this.#pageMapping = {
			MainPageDto: this.#mainPageEl,
			NewGamePageDto: this.#newGamePageEl,
			TechPageDto: this.#techPageEl,
			SelectTechPageDto: this.#selectTechPageEl,
			SpaceCombatPageDto: this.#spaceCombatPageEl,
		};
	}

	async render(data) {
		if (!this.#themeManagerContainerEl.hidden) {
			this.loadIndicator(false);

			this.#themeManagerEl.apply();

			await new Promise((resolve, reject) => {
				this.#themeManagerEl.addEventListener('loaded', (event: CustomEvent) => {
					resolve(event.detail.theme);
				});
			});

			this.#themeManagerContainerEl.hidden = true;
			this.#mainPageEl.hidden = false;
		}

		let pageEl = this.#pageMapping[data['@type']];

		Array.from(this.shadowRoot.querySelectorAll('.page')).forEach((el: HTMLElement) => {
			const hidden = el !== pageEl;
			Reconciler.reconcileProperty(el, 'hidden', hidden);
		});

		return pageEl.render(data, data.fields);
	}

	updateTurnStatus(playerStatus) {
		this.#mainPageEl.updateTurnStatus(playerStatus);
	}

	fleetMovements() {
		return this.#mainPageEl.fleetMovements();
	}

	beginNewTurn() {
		return this.#mainPageEl.beginNewTurn();
	}

	getStarMapViewport() {
		return this.#mainPageEl.getStarMapViewport();
	}

	loadIndicator(show) {
		Reconciler.reconcileProperty(this.#loadIndicatorEl, 'hidden', !show);
	}

	showConnected(connected: boolean) {
		this.#mainPageEl.showConnected(connected);
	}

	showPlayerError(type: 'kicked' | 'already-taken' | 'game-stopped') {
		this.#themeManagerContainerEl.hidden = true;
		this.loadIndicator(false);
		this.#playerErrorContainerEl.hidden = false;

		if (type === 'kicked') {
			this.#playerErrorTitleEl.innerText = 'Player kicked';
			this.#playerErrorTextEl.innerText = 'The player was kicked out of the game. Please join the game again.';
		} else if (type === 'already-taken') {
			this.#playerErrorTitleEl.innerText = 'Player already taken';
			this.#playerErrorTextEl.innerText =
				'The player is already taken for this game. Please pick another player.';
		} else if (type === 'game-stopped') {
			this.#playerErrorTitleEl.innerText = 'Game stopped';
			this.#playerErrorTextEl.innerText =
				'The game was stopped. Please create a new game or join an existing one.';
		}
	}

	forceTheme() {
		this.#themeManagerEl.forceTheme();
	}
}

customElements.define(Frontend.NAME, Frontend);
