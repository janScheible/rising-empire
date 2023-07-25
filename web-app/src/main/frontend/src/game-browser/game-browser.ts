import ErrorUtil from '~/util/error-util';
import GameLauncher from '~/game-browser/component/game-launcher';
import RunningGame from '~/game-browser/component/running-game';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';
import Container from '~/component/container';
import FetchUtil from '~/util/fetch-util';
import HypermediaUtil from '~/util/hypermedia-util';
import Sockette from '~/sockette-2.0.6';
import RisingEmppireLogo from '~/component/rising-empire-logo';

class GameBrowser extends HTMLElement {
	static NAME = 're-game-browser';

	#gameLauncherEl;
	#runningGamesEl;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: flex;
					flex-direction: column;					

					width: 100%;
				}

				@media only screen and (min-width: 800px) and (max-width: 1099px) {
					:host {
						width: 80%;
					}
				}

				@media only screen and (min-width: 1100px) and (max-width: 999999px) {
					:host {
						width: 900px;
					}
				}

				${RisingEmppireLogo.NAME}::part(text) {
					font-size: clamp(10px, 1rem + 8vw, 85px);
				}
			</style>

			<${Container.NAME} id="running-games" gap="L">
				<${RisingEmppireLogo.NAME}></${RisingEmppireLogo.NAME}>
				<${GameLauncher.NAME}></${GameLauncher.NAME}>
			</${Container.NAME}>`;

		this.#gameLauncherEl = this.shadowRoot.querySelector(GameLauncher.NAME);
		this.#runningGamesEl = this.shadowRoot.querySelector('#running-games');
	}

	render(data) {
		this.#gameLauncherEl.render(data.gameLauncher);

		Reconciler.reconcileChildren(
			this.#runningGamesEl,
			this.#runningGamesEl.querySelectorAll(RunningGame.NAME),
			data.runningGames,
			RunningGame.NAME,
			{ idValueFn: (data) => data.gameId }
		);
	}
}

customElements.define(GameBrowser.NAME, GameBrowser);

ErrorUtil.registerGlobalErrorListener(document.body.dataset.errorsUri);

const gameBrowserEl = new GameBrowser();
document.body.appendChild(gameBrowserEl);

// noop cause all updates are triggered via web socketes
HypermediaUtil.setActionResponseCallbackFn((data) => {});

const notificationWebSocketUri = new URL(document.body.dataset.notificationUri, window.location.href);
notificationWebSocketUri.protocol = notificationWebSocketUri.protocol.replace(/^https/, 'wss').replace(/^http/, 'ws');

const notificationWebSocket = new Sockette(notificationWebSocketUri.toString(), {
	onmessage: async (event) => {
		const data = JSON.parse(event.data);

		if (data.type === 'game-change') {
			update();
		}
	},
});

async function update() {
	const data = await FetchUtil.jsonFetch(document.body.dataset.gameBrowserInitUri);
	gameBrowserEl.render(data);
}
update();
