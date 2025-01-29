import NewShipsDialog from '~/page/main-page/component/new-ships-dialog';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';
import Story from '~/storybook/stories/story';

export default class NewShipsDialogStories {
	static showNewShipsDialog(story: Story) {
		story.setRenderData(`{
			"newShips": [{
				"count": 5,
				"playerColor": "red",
				"size": "SMALL",
				"name": "Scount"
			}, {
				"count": 1,
				"playerColor": "red",
				"size": "HUGE",
				"name": "Colony Ship"
			}],
			"round": 42
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME}>
				<${NewShipsDialog.NAME}></${NewShipsDialog.NAME}>
			</${PageStoryWrapper.NAME}>`);
	}
}
