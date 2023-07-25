import GameLauncher from '~/game-browser/component/game-launcher';
import Story from '~/storybook/stories/story';

export default class GameLauncherStories {
	static showGameLauncher(story: Story) {
		story.setRenderData(`{
			"defaultGameId": "my-game",
			"playerColors": [ "yellow", "blue", "White" ]
		}`);

		story.showHtml(`<${GameLauncher.NAME}></${GameLauncher.NAME}>`);
	}
}
