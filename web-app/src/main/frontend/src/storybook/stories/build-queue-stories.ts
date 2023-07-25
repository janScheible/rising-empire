import BuildQueue from '~/page/main-page/component/inspector/component/system-details/component/build-queue';
import Story from '~/storybook/stories/story';

export default class BuildQueueStories {
	static showBuildQueue(story: Story) {
		story.setRenderData(`{
			"count": 1,
			"playerColor": "red",
			"size": "HUGE",
			"name": "Colony",
			"_actions": [
				{
					"fields": [],
					"name": "next-ship-type"
				}
			]
		}`);

		story.showHtml(`<${BuildQueue.NAME} class="inspector-child-story"></${BuildQueue.NAME}>`);
	}
}
