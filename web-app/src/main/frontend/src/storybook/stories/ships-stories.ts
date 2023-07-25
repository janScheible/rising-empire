import Ships from '~/component/ships';
import Story from '~/storybook/stories/story';

export default class ShipsStories {
	static showShips(story: Story) {
		const renderData = `[
			{
				"count": 1,
				"playerColor": "red",
				"size": "HUGE"
			}, {
				"count": 3,
				"previousCount": 5,
				"playerColor": "green",
				"size": "MEDIUM"
			}, {
				"count": 2,
				"previousCount": 2,
				"playerColor": "yellow",
				"size": "SMALL"
			}
		]`;
		story.setRenderData(renderData);

		story.showHtml(
			`<div>
				<${Ships.NAME} data-json-index="0" style="border: 2px solid orange;"></${Ships.NAME}>
				<${Ships.NAME} data-json-index="1" style="border: 2px solid orange;"></${Ships.NAME}>
				<${Ships.NAME} id="ships-loose-hit-points" data-json-index="2" style="border: 2px solid orange;"></${Ships.NAME}>
				<button id="ships-loose-hit-points-button">lose hit points</button>
			</div>`
		);

		story
			.querySelector('#ships-loose-hit-points-button')
			.addEventListener('click', (event) =>
				story
					.querySelector('#ships-loose-hit-points')
					.render({ ...JSON.parse(renderData)[2], lostHitPoints: 8, count: 1 })
			);
	}
}
