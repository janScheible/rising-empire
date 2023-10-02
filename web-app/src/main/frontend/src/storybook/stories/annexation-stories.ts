import Annexation from '~/page/main-page/component/inspector/component/annexation';
import Story from '~/storybook/stories/story';

export default class AnnexationStories {
	static showAnnexation(story: Story) {
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
				{ "fields": [], "name": "annex" },
				{ "fields": [], "name": "cancel" }
			]
		}`);

		story.showHtml(`<${Annexation.NAME} class="inspector-child-story"></${Annexation.NAME}>`);
	}
}
