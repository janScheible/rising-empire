import Allocations from '~/page/main-page/component/inspector/component/system-details/component/allocations';
import Story from '~/storybook/stories/story';

export default class AllocationsStories {
	static showAllocations(story: Story) {
		story.setRenderData(`{
			"categories": {
				"ship": { "value": 20, "status": "None" },
				"defence": { "value": 20, "status": "None" },
				"industry": { "value": 20, "status": "2.7/y" },
				"ecology": { "value": 20, "status": "Clean" },
				"technology": { "value": 20, "status": "0RP" }
			},
			"_actions": [
				{
					"fields": [],
					"name": "allocate-spending"
				}
			]
		}`);

		story.showHtml(`<${Allocations.NAME} class="inspector-child-story"></${Allocations.NAME}>`);
	}
}
