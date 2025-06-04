import Container from '~/component/container';
import GridLayout from '~/component/grid-layout';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import RunningGamePlayer from '~/game-browser/component/running-game-player';
import Action from '~/util/action';

export default class RunningGame extends HTMLElement {
	static NAME = 're-running-game';

	#gameIdEl: HTMLSpanElement;
	#roundEl: HTMLSpanElement;
	#playerGridEl: GridLayout;
	#saveButton: HTMLButtonElement;

	#stopGameAction: Action;
	#saveGameAction: Action;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#download-link {
					display: none;
				}
			</style>

			<${Container.NAME} border gap="L">
				<div>Game id: <span id="game-id"></span>, round: <span id="round"></span> <button id="stop-button">Stop</button> <button id="save-button">Save</button></div>
				<${GridLayout.NAME} id="player-grid" cols="2">
				</${GridLayout.NAME}>
			</${Container.NAME}>`;

		this.#gameIdEl = this.shadowRoot.querySelector('#game-id');
		this.#roundEl = this.shadowRoot.querySelector('#round');
		this.#playerGridEl = this.shadowRoot.querySelector('#player-grid');

		this.shadowRoot.querySelector('#stop-button').addEventListener('click', (event) => {
			HypermediaUtil.submitAction(this.#stopGameAction);
		});

		this.#saveButton = this.shadowRoot.querySelector('#save-button');
		this.#saveButton.addEventListener('click', (event) => {
			const linkEl = document.createElement('a');
			linkEl.href = this.#saveGameAction.href;
			linkEl.download = '';

			this.shadowRoot.appendChild(linkEl);
			linkEl.click();
			this.shadowRoot.removeChild(linkEl);
		});
	}

	render(data) {
		this.#stopGameAction = HypermediaUtil.getAction(data, 'stop');
		this.#saveGameAction = HypermediaUtil.getAction(data, 'save');

		Reconciler.reconcileProperty(this.#saveButton, 'disabled', !this.#saveGameAction);

		Reconciler.reconcileProperty(this.#gameIdEl, 'innerText', data.gameId);
		Reconciler.reconcileProperty(this.#roundEl, 'innerText', data.round);

		Reconciler.reconcileChildren(
			this.#playerGridEl,
			this.#playerGridEl.querySelectorAll(RunningGamePlayer.NAME),
			data.players,
			RunningGamePlayer.NAME,
			{
				idValueFn: (data) => 'player-' + data.playerColor,
			}
		);
	}
}

customElements.define(RunningGame.NAME, RunningGame);
