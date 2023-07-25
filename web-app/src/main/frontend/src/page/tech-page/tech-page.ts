import Container from '~/component/container';
import ContainerButtons from '~/component/container-buttons';
import ContainerTtile from '~/component/container-title';
import FlowLayout from '~/component/flow-layout';
import GridLayout from '~/component/grid-layout';
import ModalDialog from '~/component/modal-dialog';
import SliderGroupCategory from '~/component/silder-group-category';
import SliderGroup from '~/component/slider-group';
import cssUrl from '~/util/cssUrl';
import HypermediaUtil from '~/util/hypermedia-util';

export default class TechPage extends HTMLElement {
	static NAME = 're-tech-page';

	#sliderGroup: SliderGroup;

	#closeAction;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>
			<${ModalDialog.NAME}>
				<${Container.NAME} border>
					<${ContainerTtile.NAME}>Technology research</${ContainerTtile.NAME}>

					<${GridLayout.NAME} cols="3fr 1.2fr" gap="XL">
						<${FlowLayout.NAME} direction="column">
							<table>
								<tr><th>Category</th><th>Researching</th><th>Progress 1</th><th>Progress 2</th></tr>

								<tr><td>Computers</td><td>Deep Space Scanner</td>
										<td>Factory Controls: 2 per colonist</td><td>Scanner Range: None</td></tr>
								<tr><td>Construction</td><td>Improved Industrial Tech 9</td>
										<td>Waste Reduction: None</td><td>Ground Combat: No bonus</td></tr>
								<tr><td>Force Fields</td><td>Class II Deflector Shields</td>
										<td>Planetary Shields: None</td><td>ground Combat: No bonus</td></tr>
								<tr><td>Planetology</td><td>Imprved Eco Restoration</td>
										<td>Terraform: No bonus</td><td>Waste cleanup: 2 Waste/BC</td></tr>
								<tr><td>Propulsion</td><td>Hydrogen Fuel Cells</td>
										<td>Ship range: 3 light-years</td><td>Top speed: Warp 1</td></tr>
								<tr><td>Weapons</td><td>Gatling Laser</td>
										<td>Ground Combat: No bonuss</td><td>-</td></tr>									
							</table>
						</${FlowLayout.NAME}>

						<${FlowLayout.NAME} direction="column">
							<div class="bold">Allocate research</div>
							<${SliderGroup.NAME} select-action="allocate-research">
								<${SliderGroupCategory.NAME} qualifier="computers">Computers</${SliderGroupCategory.NAME}>
								<${SliderGroupCategory.NAME} qualifier="construction">Construction</${SliderGroupCategory.NAME}>
								<${SliderGroupCategory.NAME} qualifier="force-fields">Force Fields</${SliderGroupCategory.NAME}>
								<${SliderGroupCategory.NAME} qualifier="planetology">Planetology</${SliderGroupCategory.NAME}>
								<${SliderGroupCategory.NAME} qualifier="propulsion">Propulsion</${SliderGroupCategory.NAME}>
								<${SliderGroupCategory.NAME} qualifier="weapons">Weapons</${SliderGroupCategory.NAME}>
							</${SliderGroup.NAME}>							
						</${FlowLayout.NAME}>
					</${GridLayout.NAME}>

					<${ContainerButtons.NAME}><button id="close-button">Close</button></${ContainerButtons.NAME}>
				</${Container.NAME}>
			</${ModalDialog.NAME}>`;

		this.#sliderGroup = this.shadowRoot.querySelector(SliderGroup.NAME);

		this.shadowRoot
			.querySelector('#close-button')
			.addEventListener('click', () => HypermediaUtil.submitAction(this.#closeAction, {}));
	}

	render(data) {
		this.#closeAction = HypermediaUtil.getAction(data, 'close');

		this.#sliderGroup.render(data.allocations);
	}
}

customElements.define(TechPage.NAME, TechPage);
