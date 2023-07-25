import SystemName from '~/page/main-page/component/inspector/component/system-name';
import Story from '~/storybook/stories/story';

export default class SystemNameStories {
	static showSystemName(story: Story) {
		story.setRenderData(`{
			"name": "Fieras"
		}`);

		story.showHtml(`<${SystemName.NAME} class="inspector-child-story"></${SystemName.NAME}>`);
	}
}
