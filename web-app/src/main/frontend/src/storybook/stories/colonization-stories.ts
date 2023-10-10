import Colonization from '~/page/main-page/component/inspector/component/colonization';
import Story from '~/storybook/stories/story';

export default class ColonizationStories {
	static showColonization(story: Story) {
		story.setRenderData(`[{
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
			}, {
				"systemName": {
					"name": "Sol"
				},
				"habitability": {
					"type": "TERRAN",
					"special": "NONE",
					"maxPopulation": 60
				},
				"colonizeCommand": true,
				"_actions": [
					{ "fields": [], "name": "colonize" },
					{ "fields": [], "name": "cancel" }
				]
			}]`);

		story.showHtml(`<div>
			<${Colonization.NAME} data-json-index="0" class="inspector-child-story"></${Colonization.NAME}>
			<${Colonization.NAME} data-json-index="1" class="inspector-child-story"></${Colonization.NAME}>
		</div>`);
	}
}
