import FleetView from '~/page/main-page/component/inspector/component/fleet-view';
import Story from '~/storybook/stories/story';

export default class FleetViewStories {
	static showFleetView(story: Story) {
		story.setRenderData(`{
			"race": "Human",
			"playerColor": "green",
			"eta": 4,
			"ships": [
				{
					"id": "Colony Ship@1",
					"name": "Colony Ship",
					"size": "LARGE",
					"count": 4,
					"maxCount": 4
				}, {
					"id": "Scout@0",
					"name": "Scout",
					"size": "SMALL",
					"count": 2,
					"maxcount": 2
				}
			]
		}`);

		story.showHtml(`<${FleetView.NAME} no-animated-background class="inspector-child-story"></${FleetView.NAME}>`);
	}
}
