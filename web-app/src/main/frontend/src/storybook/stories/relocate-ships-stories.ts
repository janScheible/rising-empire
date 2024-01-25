import RelocateShips from '~/page/main-page/component/inspector/component/system-details/component/relocate-ships';
import Story from '~/storybook/stories/story';

export default class RelocateStories {
	static showRelocateShips(story: Story) {
		story.setRenderData(`[{
				"_actions": [{ "name": "cancel" }, { "name": "relocate" }]
			}, {
				"delay": 2,
				"_actions": [{ "name": "cancel" }, { "name": "relocate" }]
			}]`);

		story.showHtml(`<div>
			<${RelocateShips.NAME} data-json-index="0" class="inspector-child-story" style="border: 2px solid red;"></${RelocateShips.NAME}>
			<${RelocateShips.NAME} data-json-index="1" class="inspector-child-story" style="border: 2px solid orange;"></${RelocateShips.NAME}>
		</div>`);
	}
}
