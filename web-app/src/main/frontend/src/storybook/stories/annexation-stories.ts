import Annexation from '~/page/main-page/component/inspector/component/annexation';
import Story from '~/storybook/stories/story';

export default class AnnexationStories {
	static showAnnexation(story: Story) {
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
				{ "fields": [], "name": "annex" },
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
				"annexCommand": true,
				"_actions": [
					{ "fields": [], "name": "annex" },
					{ "fields": [], "name": "cancel" }
				]
			}]`);

		story.showHtml(`<div>
			<${Annexation.NAME} data-json-index="0" class="inspector-child-story"></${Annexation.NAME}>
			<${Annexation.NAME} data-json-index="1" class="inspector-child-story"></${Annexation.NAME}>
		</div>`);
	}
}
