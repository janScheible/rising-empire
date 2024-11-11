import SystemDetails from '~/page/main-page/component/inspector/component/system-details/system-details';
import Story from '~/storybook/stories/story';

export default class SystemDetailsStories {
	static showSystemDetailsOwnColony(story: Story) {
		story.setRenderData(`{
			"systemName": {
				"name": "Fieras"
			},
			"habitability": {
				"type": "DESERT",
				"special": "NONE",
				"maxPopulation": 60
			},
			"colony": {
				"population": 50,
				"bases": 0,
				"production": {
					"net":266,
					"gross": 444
				} 
			},
			"allocations": {
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
			},
			"buildQueue": {
				"count": 1,
				"playerColor": "blue",
				"size": "HUGE",
				"name": "Colony II",
				"_actions": [
					{
						"fields": [],
						"name": "next-ship-type"
					}
				]
			}
		}`);

		story.showHtml(`<${SystemDetails.NAME} class="inspector-child-story"></${SystemDetails.NAME}>`);
	}

	static showSystemDetailsNoColony(story: Story) {
		story.setRenderData(`{
				"systemName": {
					"name": "Fieras"
				},
				"habitability": {
					"type": "JUNGLE",
					"special": "NONE",
					"maxPopulation": 120
				},
				"range": 7
			}`);

		story.showHtml(`<${SystemDetails.NAME} class="inspector-child-story"></${SystemDetails.NAME}>`);
	}

	static showSystemDetailsForeignColony(story: Story) {
		story.setRenderData(`{
			"systemName": {
				"name": "Fieras"
			},
			"habitability": {
				"type": "JUNGLE",
				"special": "NONE",
				"maxPopulation": 120
			},
			"colony": {
				"population": 50,
				"bases": 0,
				"race": "MYXALOR",
				"playerColor": "red"
			},
			"range": 7
		}`);

		story.showHtml(`<${SystemDetails.NAME} class="inspector-child-story"></${SystemDetails.NAME}>`);
	}
}
