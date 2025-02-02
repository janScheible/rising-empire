import NewShipsPage from '~/page/new-ships-page/new-ships-page';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';
import Story from '~/storybook/stories/story';

export default class NewShipsPageStories {
	static showNewShipsDialog(story: Story) {
		story.setRenderData(`{
			"newShips": [{
				"count": 5,
				"size": "SMALL",
				"name": "Scount"
			}, {
				"count": 1,
				"size": "HUGE",
				"name": "Colony Ship"
			}],
			"round": 42,
			"playerColor": "red",
			"_actions": [
				{ "fields": [], "name": "continue" }
			]
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME}>
				<${NewShipsPage.NAME}></${NewShipsPage.NAME}>
			</${PageStoryWrapper.NAME}>`);
	}
}
