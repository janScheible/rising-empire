import FlowLayout from '~/component/flow-layout';
import SliderGroupCategory from '~/component/silder-group-category';
import SliderGroup from '~/component/slider-group';
import cssUrl from '~/util/cssUrl';
import Reconciler from '~/util/reconciler';

export default class Allocations extends HTMLElement {
	static NAME = 're-allocations';

	#sliderGroupEl: SliderGroup;

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				@import ${cssUrl('~/element.css', import.meta.url)};
			</style>
			<${FlowLayout.NAME} id="allocation-spending" direction="column">
				<div class="bold">Allocate spending</div>
				<${SliderGroup.NAME} select-action="allocate-spending">
					<${SliderGroupCategory.NAME} qualifier="ship">Ship</${SliderGroupCategory.NAME}>
					<${SliderGroupCategory.NAME} qualifier="defence">Def</${SliderGroupCategory.NAME}>
					<${SliderGroupCategory.NAME} qualifier="industry">Ind</${SliderGroupCategory.NAME}>
					<${SliderGroupCategory.NAME} qualifier="ecology">Eco</${SliderGroupCategory.NAME}>
					<${SliderGroupCategory.NAME} qualifier="technology">Tech</${SliderGroupCategory.NAME}>
				</${SliderGroup.NAME}>
			</${FlowLayout.NAME}>`;

		this.#sliderGroupEl = this.shadowRoot.querySelector(SliderGroup.NAME);
	}

	render(data) {
		if (!Reconciler.isHiddenAfterPropertyReconciliation(this, !data)) {
			this.#sliderGroupEl.render(data);
		}
	}
}

customElements.define(Allocations.NAME, Allocations);
