import Container from '~/component/container';
import capitalize from '~/util/capitalize';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';
import LaunchGameUtil from '~/game-browser/component/launch-game-util';
import HypermediaUtil from '~/util/hypermedia-util';
import GridLayout from '~/component/grid-layout';
import ContainerButtons from '~/component/container-buttons';

export default class GameLauncher extends HTMLElement {
	static NAME = 're-game-launcher';

	#startEl: HTMLButtonElement;
	#gameIdEl: HTMLInputElement;
	#playerEl: HTMLSelectElement;

	#startAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				#inputs {
					max-width: 370px;
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
				</${GridLayout.NAME}>

				<${ContainerButtons.NAME}>
					<button id="start-button" disabled>Start</button>
				</${ContainerButtons.NAME}>
			</${Container.NAME}>`;

		this.#startEl = this.shadowRoot.querySelector('#start-button');

		this.#gameIdEl = this.shadowRoot.querySelector('#game-id') as HTMLInputElement;

		this.#gameIdEl.addEventListener('input', (event) => (this.#startEl.disabled = this.#disableStartButton()));

		this.#playerEl = this.shadowRoot.querySelector('#player') as HTMLSelectElement;

		this.shadowRoot.querySelector('#start-button').addEventListener('click', (event: MouseEvent) => {
			const gameId = this.#gameIdEl.value;
			const player = this.#playerEl.value;

			LaunchGameUtil.launchUrlTemplate(this.#startAction.href, gameId, player, event.ctrlKey);
		});
	}

	render(data) {
		this.#startAction = HypermediaUtil.getAction(data, 'start');

		if (this.#gameIdEl.value === '#INIT#') {
			Reconciler.reconcileAttribute(this.#gameIdEl, 'value', data.defaultGameId ?? '');
		}

		Reconciler.reconcileProperty(this.#startEl, 'disabled', this.#disableStartButton());

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
}

customElements.define(GameLauncher.NAME, GameLauncher);
