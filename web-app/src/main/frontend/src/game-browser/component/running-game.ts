import Container from '~/component/container';
import GridLayout from '~/component/grid-layout';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import RunningGamePlayer from '~/game-browser/component/running-game-player';

export default class RunningGame extends HTMLElement {
	static NAME = 're-running-game';

	#gameIdEl: HTMLSpanElement;
	#playerGridEl: GridLayout;

	#stopGameAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>

			<${Container.NAME} border gap="L">
				<div>Game id: <span id="game-id"></span> <button id="stop-button">Stop</button></div>
				<${GridLayout.NAME} id="player-grid" cols="2">					
				</${GridLayout.NAME}>
			</${Container.NAME}>`;

		this.#gameIdEl = this.shadowRoot.querySelector('#game-id');
		this.#playerGridEl = this.shadowRoot.querySelector('#player-grid');

		this.shadowRoot.querySelector('#stop-button').addEventListener('click', (event) => {
			HypermediaUtil.submitAction(this.#stopGameAction);
		});
	}

	render(data) {
		this.#stopGameAction = HypermediaUtil.getAction(data, 'stop');

		Reconciler.reconcileProperty(this.#gameIdEl, 'innerText', data.gameId);

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
