import FleetDeployment from '~/page/main-page/component/inspector/component/fleet-deployment';
import Story from '~/storybook/stories/story';

export default class FleetDeploymentStories {
	static showFleetDeploymentDeployableFleet(story: Story) {
		story.setRenderData(`{
			"playerColor": "yellow",
			"eta": 4,
			"deployable": true,
			"ships": [
				{
					"id": "Colony Ship@1",
					"name": "Colony Ship",
					"size": "LARGE",
					"count": 4,
					"maxACount": 4
				}, {
					"id": "Scout@0",
					"name": "Scout",
					"size": "SMALL",
					"count": 2,
					"maxACount": 2
				}
			],
			"_actions": [
				{ "fields":[], "name": "deploy" },
				{ "fields":[], "name": "cancel" },
				{ "fields":[], "name": "assign-ships" }
			]
		}`);

		story.showHtml(`<${FleetDeployment.NAME} class="inspector-child-story"></${FleetDeployment.NAME}>`);
	}
}
