import Exploration from '~/page/main-page/component/inspector/component/exploration';
import Story from '~/storybook/stories/story';

export default class ExplorationStories {
	static showExploration(story: Story) {
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
				{ "fields": [], "name": "continue" }
			]
		}`);

		story.showHtml(`<${Exploration.NAME} class="inspector-child-story"></${Exploration.NAME}>`);
	}
}
