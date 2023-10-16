import Star from '~/page/main-page/component/star-map/component/star';
import Story from '~/storybook/stories/story';

export default class StarStories {
	static showStars(story: Story) {
		story.setRenderData(
			`[
				{
					"type": "blue",
					"x": 60,
					"y": 40,
					"_actions": [
						{
							"name": "select"
						}
					]
				},
				{
					"type": "yellow",
					"x": 200,
					"y": 40,
					"name": "Sol",
					"_actions": [
						{
							"name": "select"
						}
					]
				},
				{
					"type": "green",
					"x": 340,
					"y": 40,
					"name": "Sol",
					"playerColor": "yellow",
					"_actions": [
						{
							"name": "select"
						}
					]
				},
				{
					"type": "red",
					"x": 460,
					"y": 40,
					"name": "Sol",
					"playerColor": "white",
					"siegePlayerColor": "blue",
					"siegeProgress": 60,
					"_actions": [
						{
							"name": "select"
						}
					]
				}
			]`
		);

		story.showHtml(`<div style="position: relative; background-color: black; width: 540px; height: 120px;">
			<${Star.NAME} data-json-index="0"></${Star.NAME}>
			<${Star.NAME} data-json-index="1"></${Star.NAME}>
			<${Star.NAME} data-json-index="2"></${Star.NAME}>
			<${Star.NAME} data-json-index="3"></${Star.NAME}>
		</div>`);
	}
}
