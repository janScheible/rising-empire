import Unexplored from '~/page/main-page/component/inspector/component/unexplored';
import Story from '~/storybook/stories/story';

export default class UnexploredStories {
	static showUnexplored(story: Story) {
		story.setRenderData(`{
			"starType": "GREEN",
			"range": 7
		}`);

		story.showHtml(`<${Unexplored.NAME} class="inspector-child-story"></${Unexplored.NAME}>`);
	}
}
