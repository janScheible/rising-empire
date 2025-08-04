import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import ModalDialog from '~/component/modal-dialog';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';
import Reconciler from '~/util/reconciler';

export default class NewGamePage extends HTMLElement {
	static NAME = 're-new-game-page';

	#galaxySizeEl: HTMLSelectElement;
	#game2El: HTMLInputElement;

	#scenarioLabelEl: HTMLLabelElement;
	#scenarioSelectEl: HTMLSelectElement;

	#createAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};

				label {
					white-space: nowrap;
				}
			</style>
			<${ModalDialog.NAME}>
				<${Container.NAME} border>
					<${ContainerTtile.NAME}>New game</${ContainerTtile.NAME}>

					<${GridLayout.NAME} cols="1fr 2fr" gap="L">
						<label for="galaxy-size">Galaxy size</label>
						<select name="galaxy-size">
							<option value="SMALL">Small</option>
							<option value="MEDIUM" selected>Medium</option>
							<option value="LARGE">Large</option>
							<option value="HUGE">Huge</option>
						</select>

						<label for="player-count">Player count</label>
						<select name="player-count" disabled>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3" selected>3</option>
							<option value="4">4</option>
							<option value="5">5</option>
						</select>

						<label id="game2-label" for="game2">Game 2</label>
						<input name="game2" type="checkbox" checked data-row-align="left">

						<label id="scenario-label" hidden for="scenario">Scenario</label>
						<select hidden name="scenario">
						</select>
					</${GridLayout.NAME}>

					<${ContainerButtons.NAME}><button id="create-button">Create</button></${ContainerButtons.NAME}>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#galaxySizeEl = this.shadowRoot.querySelector('select[name="galaxy-size"]');
		this.#game2El = this.shadowRoot.querySelector('input[name="game2"]');

		this.#scenarioLabelEl = this.shadowRoot.querySelector('label[for="scenario"]');
		this.#scenarioSelectEl = this.shadowRoot.querySelector('select[name="scenario"]');

		this.shadowRoot.querySelector('#create-button').addEventListener('click', () => {
			const selectedScenarioId = this.#scenarioSelectEl.selectedOptions[0]?.value;
			const values = Object.assign(
				{
					galaxySize: this.#galaxySizeEl.selectedOptions[0].value,
					game2: this.#game2El.checked,
				},
				selectedScenarioId ? { scenarioId: selectedScenarioId } : null
			);

			HypermediaUtil.submitAction(this.#createAction, values);
		});
	}

	render(data) {
		if (
			!Reconciler.isHiddenAfterPropertyReconciliation(this.#scenarioLabelEl, !data.gameScenarios) &&
			!Reconciler.isHiddenAfterPropertyReconciliation(this.#scenarioSelectEl, !data.gameScenarios)
		) {
			Reconciler.reconcileChildren(
				this.#scenarioSelectEl,
				this.#scenarioSelectEl.querySelectorAll(':scope > option'),
				data.gameScenarios,
				'option',
				{
					renderCallbackFn: (el: HTMLInputElement, data) => {
						el.value = data.id;
						el.innerText = data.name;
					},
					idAttributName: 'value',
				}
			);
		}
		this.#createAction = HypermediaUtil.getAction(data, 'create');
	}
}

customElements.define(NewGamePage.NAME, NewGamePage);
