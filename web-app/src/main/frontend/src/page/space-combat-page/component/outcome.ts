import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Outcome extends HTMLElement {
	static NAME = 're-outcome';

	#outcomeEl: HTMLSpanElement;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				:host {
					display: inline-block;
				}

				.huge-outcome-font {
					font-size: 200%;
					font-family: var(--theme-scifi-font);
				}

				#outcome.victory {
					color: darkgreen;
				}

				#outcome.defeat {
					color: darkred;
				}

				#outcome.retreat {
					color: darkorange;
				}
			</style>
			<span id="outcome" class="huge-outcome-font">&nbsp;</span>`;

		this.#outcomeEl = this.shadowRoot.querySelector('#outcome');
	}

	render(data) {
		const isVictory = data.outcome === 'VICTORY';
		const isDefeat = data.outcome === 'DEFEAT';
		const isRetreat = data.outcome === 'RETREAT';

		Reconciler.reconcileProperty(
			this.#outcomeEl,
			'innerHTML',
			isVictory ? 'Victory' : isDefeat ? 'Defeat' : isRetreat ? 'Retreat' : '&nbsp;'
		);

		Reconciler.reconcileClass(this.#outcomeEl, 'defeat', isDefeat);
		Reconciler.reconcileClass(this.#outcomeEl, 'victory', isVictory);
		Reconciler.reconcileClass(this.#outcomeEl, 'retreat', isRetreat);
	}
}

customElements.define(Outcome.NAME, Outcome);
