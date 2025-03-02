import Fleet from '~/page/main-page/component/star-map/component/fleet';
import Story from '~/storybook/stories/story';

export default class FleetStories {
	static showFleets(story: Story) {
		story.setRenderData(
			`[
				{
					"id": "f9973x5626->7680x7226@2w/1000",
					"playerColor": "yellow",
					"x": 30,
					"y": 76,
					"orbiting": true,
					"horizontalDirection": "left",
					"_actions": []
				},
				{
					"id": "f9973x5626->7680x7226@2w/1000",
					"playerColor": "blue",
					"colonistTransporters": true,
					"x": 110,
					"y": 76,
					"orbiting": true,
					"horizontalDirection": "left",
					"_actions": []
				}				
			]`
		);

		story.showHtml(`<div style="position: relative; background-color: black; width: 218px; height: 120px;">
			<${Fleet.NAME} data-json-index="0"></${Fleet.NAME}>
			<${Fleet.NAME} data-json-index="1"></${Fleet.NAME}>
		</div>`);
	}
}
