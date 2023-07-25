import TechPage from '~/page/tech-page/tech-page';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';
import Story from '~/storybook/stories/story';

export default class TechPageStories {
	static showTechPage(story: Story) {
		story.setRenderData(`{
			"allocations": {
				"categories": {
					"computers": { "value": 16, "status": "1111RP" },
					"construction": { "value": 17, "status": "2222RP" },
					"force-fields": { "value": 16, "status": "3333RP" },
					"planetology": { "value": 17, "status": "4444RP" },
					"propulsion": { "value": 17, "status": "5555RP" },
					"weapons": { "value": 17, "status": "6666RP" }
				},
				"_actions": [
					{ "fields": [], "name": "allocate-research" }
				]
			},
			"_actions": [
				{ "fields": [], "name": "close" },
				{ "fields": [], "name": "allocate-research" }
			]
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME} close-action="close">
				<${TechPage.NAME}></${TechPage.NAME}>
			</${PageStoryWrapper.NAME}>`);
	}
}
