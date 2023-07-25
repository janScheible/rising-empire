import GridLayout from '~/component/grid-layout';
import ShipSpecs from '~/page/space-combat-page/component/ship-specs';
import Story from '~/storybook/stories/story';

export default class ShipSpecStories {
	static showShipSpesc(story: Story) {
		story.setRenderData(`[
			{
				"name": "Scout",
				"shield": 0,
				"beamDefence": 3,
				"attackLevel": 0,
				"warp": 0,
				"missleDefence": 3,
				"hits": 3,
				"speed": 1,
				"ships": {
					"count": 2,
					"previousCount": 5,
					"playerColor": "green",
					"size": "SMALL"
				},
				"equipment": ["Reserve Tanks"]
			}, {
				"name": "Destroyer",
				"shield": 2,
				"beamDefence": 3,
				"attackLevel": 0,
				"damage": 100,
				"missleDefence": 3,
				"hits": 3,
				"speed": 1,
				"ships": {
					"count": 3,
					"previousCount": 4,
					"playerColor": "green",
					"size": "MEDIUM"
				},
				"equipment": []
			}, {
				"name": "Attacker",
				"ships": {
					"count": 3,
					"previousCount": 3,
					"playerColor": "red",
					"size": "MEDIUM"
				}
			}
		]`);

		story.showHtml(`
			<${GridLayout.NAME} row-align="center" col-align="middle" gap="XL" style="margin: 20px;">
				<${ShipSpecs.NAME} data-json-index="0"></${ShipSpecs.NAME}>
				<${ShipSpecs.NAME} data-json-index="1"></${ShipSpecs.NAME}>
				<${ShipSpecs.NAME} data-json-index="2"></${ShipSpecs.NAME}>
			</${GridLayout.NAME}>`);
	}
}
