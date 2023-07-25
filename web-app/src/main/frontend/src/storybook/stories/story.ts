export default class Story {
	#storyId;
	#storybook;

	constructor(storyId, storybook) {
		this.#storyId = storyId;
		this.#storybook = storybook;
	}
	showHtml(html) {
		this.#storybook.showHtml(html);
	}

	setRenderData(json) {
		this.#storybook.setRenderData(this.#storyId, json);
	}

	querySelector(selectors) {
		return this.#storybook.shadowRoot.querySelector(selectors);
	}

	querySelectorAll(selectors) {
		return this.#storybook.shadowRoot.querySelectorAll(selectors);
	}
}
