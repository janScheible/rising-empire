import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import ModalDialog from '~/component/modal-dialog';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';

export default class NewGamePage extends HTMLElement {
	static NAME = 're-new-game-page';

	#galaxySizeEl: HTMLSelectElement;
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
					</${GridLayout.NAME}>

					<${ContainerButtons.NAME}><button id="create-button">Create</button></${ContainerButtons.NAME}>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#galaxySizeEl = this.shadowRoot.querySelector('select[name="galaxy-size"]');

		this.shadowRoot.querySelector('#create-button').addEventListener('click', () =>
			HypermediaUtil.submitAction(this.#createAction, {
				galaxySize: this.#galaxySizeEl.selectedOptions[0].value,
			})
		);
	}

	render(data) {
		this.#createAction = HypermediaUtil.getAction(data, 'create');

		const autoCreate = HypermediaUtil.getField(data, 'create', 'auto-create')?.value;
		if (autoCreate) {
			HypermediaUtil.submitAction(this.#createAction, {});
		}
	}
}

customElements.define(NewGamePage.NAME, NewGamePage);
