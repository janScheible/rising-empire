import Colonization from '~/page/main-page/component/inspector/component/colonization';
import Story from '~/storybook/stories/story';

export default class ColonizationStories {
	static showColonization(story: Story) {
		story.setRenderData(`{
			"systemName": {
				"name": "Fieras"
			},
			"habitability": {
				"type": "DESERT",
				"special": "NONE",
				"maxPopulation": 60
			},
			"_actions": [
				{ "fields": [], "name": "colonize" },
				{ "fields": [], "name": "cancel" }
			]
		}`);

		story.showHtml(`<${Colonization.NAME} class="inspector-child-story"></${Colonization.NAME}>`);
	}
}
