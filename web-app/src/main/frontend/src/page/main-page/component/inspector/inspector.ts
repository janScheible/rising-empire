import ModalDialog from '~/component/modal-dialog';
import Colonization from '~/page/main-page/component/inspector/component/colonization';
import Annexation from '~/page/main-page/component/inspector/component/annexation';
import Exploration from '~/page/main-page/component/inspector/component/exploration';
import FleetDeployment from '~/page/main-page/component/inspector/component/fleet-deployment';
import SystemDetails from '~/page/main-page/component/inspector/component/system-details/system-details';
import SpaceCombat from '~/page/main-page/component/inspector/component/space-combat';
import FleetView from '~/page/main-page/component/inspector/component/fleet-view';
import Unexplored from '~/page/main-page/component/inspector/component/unexplored';

export default class Inspector extends HTMLElement {
	static NAME = 're-inspector';

	static WIDTH = 258;

	#systemDetailsEl: SystemDetails;
	#fleetDeploymentEl: FleetDeployment;
	#fleetViewEl: FleetView;
	#explorationEl: Exploration;
	#colonizationEl: Colonization;
	#annexationEl: Annexation;
	#spaceCombatEl: SpaceCombat;
	#unexploredEl: Unexplored;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				:host {
					position: relative;

					min-width: ${Inspector.WIDTH}px;
					max-width: ${Inspector.WIDTH}px;
				}

				${ModalDialog.NAME} {
					position: absolute;
				}
			</style>
			<${SystemDetails.NAME} hidden></${SystemDetails.NAME}>
			<${FleetDeployment.NAME} hidden></${FleetDeployment.NAME}>
			<${FleetView.NAME} hidden></${FleetView.NAME}>
			<${Exploration.NAME} hidden></${Exploration.NAME}>
			<${Colonization.NAME} hidden></${Colonization.NAME}>
			<${Annexation.NAME} hidden></${Annexation.NAME}>
			<${SpaceCombat.NAME} hidden></${SpaceCombat.NAME}>
			<${Unexplored.NAME} hidden></${Unexplored.NAME}>
			<${ModalDialog.NAME} hidden></${ModalDialog.NAME}>`;

		this.#systemDetailsEl = this.shadowRoot.querySelector(SystemDetails.NAME);
		this.#fleetDeploymentEl = this.shadowRoot.querySelector(FleetDeployment.NAME);
		this.#fleetViewEl = this.shadowRoot.querySelector(FleetView.NAME);
		this.#explorationEl = this.shadowRoot.querySelector(Exploration.NAME);
		this.#colonizationEl = this.shadowRoot.querySelector(Colonization.NAME);
		this.#annexationEl = this.shadowRoot.querySelector(Annexation.NAME);
		this.#spaceCombatEl = this.shadowRoot.querySelector(SpaceCombat.NAME);
		this.#unexploredEl = this.shadowRoot.querySelector(Unexplored.NAME);
	}

	render(data) {
		this.#systemDetailsEl.render(data.systemDetails);
		this.#fleetDeploymentEl.render(data.fleetDeployment);
		this.#fleetViewEl.render(data.fleetView);
		this.#explorationEl.render(data.exploration);
		this.#colonizationEl.render(data.colonization);
		this.#annexationEl.render(data.annexation);
		this.#spaceCombatEl.render(data.spaceCombat);
		this.#unexploredEl.render(data.unexplored);
	}
}

customElements.define(Inspector.NAME, Inspector);
