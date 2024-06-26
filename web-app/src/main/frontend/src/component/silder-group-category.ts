export default class SliderGroupCategory extends HTMLElement {
	static NAME = 're-slider-group-category';

	constructor() {
		super();

		this.attachShadow({ mode: 'open' }).innerHTML = `
			<style>
				:host {
					display: hidden;
				}
			</style>`;
	}

	get qualifier() {
		return this.getAttribute('qualifier');
	}
}

customElements.define(SliderGroupCategory.NAME, SliderGroupCategory);
