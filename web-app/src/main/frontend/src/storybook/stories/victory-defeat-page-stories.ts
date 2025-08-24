import VictoryDefeatPage from '~/page/victory-defeat-page/victory-defeat-page';
import Story from '~/storybook/stories/story';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';

export default class VictoryDefeatPageStories {
	static showVictoryDefeatPage(story: Story) {
		story.setRenderData(`{
			"victory": true
		}`);

		story.showHtml(`
			<${PageStoryWrapper.NAME}>
				<${VictoryDefeatPage.NAME}></${VictoryDefeatPage.NAME}>
			</${PageStoryWrapper.NAME}>`);
	}
}
