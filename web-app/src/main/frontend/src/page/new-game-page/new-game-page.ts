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

	#playerCountEl: HTMLSelectElement;

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
						</select>

						<label for="player-count">Player count</label>
						<select name="player-count" disabled>
						</select>

						<label id="scenario-label" hidden for="scenario">Scenario</label>
						<select hidden name="scenario">
						</select>
					</${GridLayout.NAME}>

					<${ContainerButtons.NAME}><button id="create-button">Create</button></${ContainerButtons.NAME}>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#galaxySizeEl = this.shadowRoot.querySelector('select[name="galaxy-size"]');
		this.#playerCountEl = this.shadowRoot.querySelector('select[name="player-count"]');
		this.#scenarioLabelEl = this.shadowRoot.querySelector('label[for="scenario"]');
		this.#scenarioSelectEl = this.shadowRoot.querySelector('select[name="scenario"]');

		this.shadowRoot.querySelector('#create-button').addEventListener('click', () => {
			const selectedScenarioId = this.#scenarioSelectEl.selectedOptions[0]?.value;
			const values = Object.assign(
				{
					galaxySize: this.#galaxySizeEl.selectedOptions[0].value,
					playerCount: this.#playerCountEl.selectedOptions[0].value,
				},
				selectedScenarioId !== '-1' ? { scenarioId: selectedScenarioId } : null
			);

			HypermediaUtil.submitAction(this.#createAction, values);
		});
	}

	render(data) {
		Reconciler.reconcileProperty(this.#playerCountEl, 'disabled', data.testGame);

		Reconciler.reconcileChildren(
			this.#galaxySizeEl,
			this.#galaxySizeEl.querySelectorAll(':scope > option'),
			data.galaxySizes,
			'option',
			{
				renderCallbackFn: (el: HTMLInputElement, size) => {
					el.value = size;
					el.innerText = size.substring(0, 1) + size.substring(1).toLowerCase();

					if (size === 'MEDIUM') {
						el.setAttribute('selected', '');
					}
				},
				idAttributName: 'value',
			}
		);

		Reconciler.reconcileChildren(
			this.#playerCountEl,
			this.#playerCountEl.querySelectorAll(':scope > option'),
			Array.from({ length: data.maxPlayerCount }, (_, i) => i + 1).splice(1),
			'option',
			{
				renderCallbackFn: (el: HTMLInputElement, count) => {
					el.value = count;
					el.innerText = count;

					if (count === 3) {
						el.setAttribute('selected', '');
					}
				},
				idAttributName: 'value',
			}
		);

		if (
			!Reconciler.isHiddenAfterPropertyReconciliation(this.#scenarioLabelEl, !data.gameScenarios) &&
			!Reconciler.isHiddenAfterPropertyReconciliation(this.#scenarioSelectEl, !data.gameScenarios)
		) {
			Reconciler.reconcileChildren(
				this.#scenarioSelectEl,
				this.#scenarioSelectEl.querySelectorAll(':scope > option'),
				[{ id: -1, name: '-' }].concat(data.gameScenarios),
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
