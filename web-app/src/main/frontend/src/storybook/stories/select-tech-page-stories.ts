import SelectTechPage from '~/page/select-tech-page/select-tech-page';
import Story from '~/storybook/stories/story';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';

export default class SelectTechPageStories {
	static showSelectTechPage(story: Story) {
		story.setRenderData(`{
			"researchedTech": {
				"name": "Controlled Barren Environment",
				"description": "Permits the colonization of barren planets."
			},
			"category": "planetology",
			"techs": [
				{
					"id": "cte",
					"name": "Controlled Trundra Environment",
					"expense": 570,
					"description": "Permits the colonization of tundra planets.",
					"_actions": [
						{ "fields": [{ "name": "id", "value": "cte" }], "name": "select" }
					]
				}, {
					"id": "gl",
					"name": "Improved terraforming +20",
					"expense": 1020,
					"description": "Increases the population capacity of planets by 20 million for a cost of 5 BC per million.",
					"_actions": [
						{ "fields": [{ "name": "id", "value": "itf20" }], "name": "select" }
					]
				}, {
					"id": "hvr",
					"name": "Controlled Dead Environment",
					"expense": 1290,
					"description": "Permits the colonization of dead planets.",
					"_actions": [
						{ "fields": [{ "name": "id", "value": "cde" }], "name": "select" }
					]
				}
			]
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME} close-action="select">
			  <${SelectTechPage.NAME}></${SelectTechPage.NAME}>
			</${PageStoryWrapper.NAME}`);
	}
}
