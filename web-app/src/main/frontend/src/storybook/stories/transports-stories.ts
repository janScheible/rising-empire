import Transports from '~/page/main-page/component/inspector/component/transports';
import Story from '~/storybook/stories/story';

export default class TransportsStories {
	static showTransports(story: Story) {
		story.setRenderData(`{
			"playerColor": "blue",
			"race": "LUMERISKS",
			"transports": 4,
			"eta": 3
		}`);

		story.showHtml(`<${Transports.NAME} class="inspector-child-story"></${Transports.NAME}>`);
	}
}
