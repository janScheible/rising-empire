import Container from '~/component/container';
import capitalize from '~/util/capitalize';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';
import LaunchGameUtil from '~/game-browser/component/launch-game-util';
import ConnectionIndicator from '~/component/connection-indicator';

export default class RunningGamePlayer extends HTMLElement {
	static NAME = 're-running-game-player';

	#playerColorEl: HTMLSpanElement;
	#connectionIndicatorEl: ConnectionIndicator;

	#kickButtonEl: HTMLButtonElement;
	#joinButtonEl: HTMLButtonElement;

	#kickAction;
	#joinAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>

			<${Container.NAME} border gap="L">
				<div>
					Color: <span id="player-color"></span>
					<${ConnectionIndicator.NAME}></${ConnectionIndicator.NAME}>
					<button id="kick-button" hidden>Kick</button>
					<button id="join-button" hidden>Join</button>
				</div>
			</${Container.NAME}>`;

		this.#playerColorEl = this.shadowRoot.querySelector('#player-color');
		this.#connectionIndicatorEl = this.shadowRoot.querySelector(ConnectionIndicator.NAME);

		this.#kickButtonEl = this.shadowRoot.querySelector('#kick-button');
		this.#kickButtonEl.addEventListener('click', (event) => {
			HypermediaUtil.submitAction(this.#kickAction);
		});

		this.#joinButtonEl = this.shadowRoot.querySelector('#join-button');
		this.#joinButtonEl.addEventListener('click', (event) => {
			const joinModel = { _actions: [this.#joinAction] };

			const joinGameIdField = HypermediaUtil.getField(joinModel, 'join', 'gameId');
			const joinPlayerField = HypermediaUtil.getField(joinModel, 'join', 'player');

			LaunchGameUtil.launch(joinGameIdField.value, joinPlayerField.value, event.ctrlKey);
		});
	}

	render(data) {
		this.#kickAction = HypermediaUtil.getAction(data, 'kick');
		this.#joinAction = HypermediaUtil.getAction(data, 'join');

		Reconciler.reconcileProperty(this.#playerColorEl, 'innerText', capitalize(data.playerColor));
		Reconciler.reconcileProperty(this.#kickButtonEl, 'hidden', !this.#kickAction);
		Reconciler.reconcileProperty(this.#joinButtonEl, 'hidden', !this.#joinAction);

		if (!Reconciler.isHiddenAfterPropertyReconciliation(this.#connectionIndicatorEl, !data.interactive)) {
			Reconciler.reconcileProperty(this.#connectionIndicatorEl, 'connected', data.canReceiveNotifications);
			Reconciler.reconcileAttribute(this.#connectionIndicatorEl, 'title', data.playerSessionId ?? '');
		}
	}
}

customElements.define(RunningGamePlayer.NAME, RunningGamePlayer);
