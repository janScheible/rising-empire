import Container from '~/component/container';
import FlowLayout from '~/component/flow-layout';
import ModalDialog from '~/component/modal-dialog';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class TurnStatusDialog extends HTMLElement {
	static NAME = 're-turn-status-dialog';

	#turnFinishedPlayerOverviewEl: FlowLayout;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				.player-flag {
					height: 1em;
					vertical-align: top;
					stroke: var(--theme-border-color);
				}
			</style>
			<${ModalDialog.NAME}>
				<${Container.NAME} notification>
					<${FlowLayout.NAME} gap="XL" direction="column">
						<div>You finished your turn.</div>
						<${FlowLayout.NAME} id="turn-finished-player-overview" direction="column">
							<template id="player-template">
								<div class="player">
									<svg class="player-flag" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
										<rect width="16" height="16" stroke-width="2"/>
									</svg>
									<span class="player-name">Text</span>
								</div>
							</template>
						</${FlowLayout.NAME}>
					</${FlowLayout.NAME}>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#turnFinishedPlayerOverviewEl = this.shadowRoot.querySelector('#turn-finished-player-overview');
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data?.ownTurnFinished)) {
			this.updateTurnStatus(data.playerStatus);
		}
	}

	updateTurnStatus(playerStatus) {
		Reconciler.reconcileChildren(
			this.#turnFinishedPlayerOverviewEl,
			this.#turnFinishedPlayerOverviewEl.querySelectorAll(':scope > .player'),
			playerStatus,
			'#player-template',
			{
				renderCallbackFn: (playerEl: HTMLDivElement, player) => {
					playerEl.style.fill = player.finished ? `var(--${player.playerColor}-player-color)` : 'transparent';
					Reconciler.reconcileProperty(playerEl.querySelector('.player-name'), 'innerText', player.name);
				},
			}
		);
	}
}

customElements.define(TurnStatusDialog.NAME, TurnStatusDialog);
