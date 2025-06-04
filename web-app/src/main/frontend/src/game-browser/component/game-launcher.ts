import Container from '~/component/container';
import capitalize from '~/util/capitalize';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';
import LaunchGameUtil from '~/game-browser/component/launch-game-util';
import HypermediaUtil from '~/util/hypermedia-util';
import GridLayout from '~/component/grid-layout';
import ContainerButtons from '~/component/container-buttons';
import Action from '~/util/action';

export default class GameLauncher extends HTMLElement {
	static NAME = 're-game-launcher';

	#gameIdEl: HTMLInputElement;
	#playerEl: HTMLSelectElement;
	#savegameEl: HTMLInputElement;
	#startOrLoadButtonEl: HTMLButtonElement;

	#startAction: Action;
	#loadAction: Action;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#inputs {
					max-width: 450px;
				}

				#header {
					font-size: 150%;
					padding-bottom: 8px;
				}
			</style>

			<${Container.NAME} border gap="L">
				<div id="header">New game</div>

				<${GridLayout.NAME} id="inputs" cols="1fr 2fr" gap="L">
					<label for="game-id">Game id:</label>
					<input id="game-id" size="25" value="#INIT#"></input>

					<label for="player">Player color:</label>
					<select id="player"></select>

					<label for="savegame">Savegame:</label>
					<input id="savegame" type="file" accept="application/json">
				</${GridLayout.NAME}>

				<${ContainerButtons.NAME}>
					<button id="start-or-load-button" disabled>Start</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#startOrLoadButtonEl = this.shadowRoot.querySelector('#start-or-load-button');

		this.#gameIdEl = this.shadowRoot.querySelector('#game-id') as HTMLInputElement;
		this.#gameIdEl.addEventListener(
			'input',
			(event) => (this.#startOrLoadButtonEl.disabled = this.#disableStartButton())
		);

		this.#playerEl = this.shadowRoot.querySelector('#player') as HTMLSelectElement;
		this.#savegameEl = this.shadowRoot.querySelector('#savegame');

		this.#startOrLoadButtonEl.addEventListener('click', (event: MouseEvent) => {
			const gameId = this.#gameIdEl.value;
			const player = this.#playerEl.value;
			const file = this.#savegameEl.files[0];

			if (file) {
				const reader = new FileReader();
				reader.onload = (event) => {
					this.#savegameEl.value = null;
					this.#startOrLoadButtonEl.innerText = 'Start';

					HypermediaUtil.submitAction(this.#loadAction, { savegame: event.target.result, player, gameId });
				};
				reader.readAsText(file);
			} else {
				LaunchGameUtil.launchUrlTemplate(this.#startAction.href, gameId, player, event.ctrlKey);
			}
		});

		this.#savegameEl.addEventListener('change', (event) => (this.#startOrLoadButtonEl.innerText = 'Load'));
	}

	render(data) {
		this.#startAction = HypermediaUtil.getAction(data, 'start');
		this.#loadAction = HypermediaUtil.getAction(data, 'load');

		if (this.#gameIdEl.value === '#INIT#') {
			Reconciler.reconcileAttribute(this.#gameIdEl, 'value', data.defaultGameId ?? '');
		}

		Reconciler.reconcileProperty(this.#startOrLoadButtonEl, 'disabled', this.#disableStartButton());

		Reconciler.reconcileChildren(
			this.#playerEl,
			this.#playerEl.querySelectorAll(':scope > option'),
			data.playerColors,
			'option',
			{
				idValueFn: (color) => 'player-' + color,
				renderCallbackFn: (el, color) => {
					(el as HTMLOptionElement).innerText = capitalize(color);
					(el as HTMLOptionElement).value = color;
				},
			}
		);
	}

	#disableStartButton(): boolean {
		const gameId = this.#gameIdEl.value;
		return !gameId || gameId.trim().length === 0;
	}

	launchTestGame() {
		LaunchGameUtil.launchUrlTemplate(this.#startAction.href, 'test-game', 'blue', true);
	}
}

customElements.define(GameLauncher.NAME, GameLauncher);
