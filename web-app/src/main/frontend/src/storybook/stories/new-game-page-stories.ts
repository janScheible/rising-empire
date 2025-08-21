import NewGamePage from '~/page/new-game-page/new-game-page';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';
import Story from '~/storybook/stories/story';

export default class NewGamePageStories {
	static showNewGamePage(story: Story) {
		story.setRenderData(`{
			"galaxySizes": ["SMALL","MEDIUM","LARGE","HUGE"],
			"maxPlayerCount": 6,
			"testGame":true,
			"_actions": [
				{ "fields": [], "name": "create" }
			]
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME} close-action="create">
				<${NewGamePage.NAME}></${NewGamePage.NAME}>
			</${PageStoryWrapper.NAME}>`);
	}
}
