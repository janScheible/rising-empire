import TurnFinishedFialog from '~/page/main-page/component/turn-finish-dialog';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';
import Story from '~/storybook/stories/story';

export default class TurnFinishedDialogStories {
	static showTurnFinishedDialogWaitingForOthers(story: Story) {
		story.setRenderData(`{
			"ownTurnFinished": true,
			"playerStatus": [
				{ "id": "red-player", "name": "Red", "playerColor": "red", "finished": false },
				{ "id": "green-player", "name": "Green", "playerColor": "green", "finished": true },
				{ "id": "yellow-player", "name": "Yellow", "playerColor": "yellow", "finished": true }
			]
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME}>
				<${TurnFinishedFialog.NAME}></${TurnFinishedFialog.NAME}>
			</${PageStoryWrapper.NAME}>`);
	}
}
