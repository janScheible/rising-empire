import Outcome from '~/page/space-combat-page/component/outcome';
import Story from '~/storybook/stories/story';

export default class Outcometories {
	static showOutcomes(story: Story) {
		const renderData = `[
			{
				"outcome": "VICTORY"
			}, {
				"outcome": "DEFEAT"
			}, {
				"outcome": "RETREAT"
			}
		]`;
		story.setRenderData(renderData);

		story.showHtml(
			`<div>
				<${Outcome.NAME} data-json-index="0" style="border: 2px solid orange;"></${Outcome.NAME}>
				<${Outcome.NAME} data-json-index="1" style="border: 2px solid orange;"></${Outcome.NAME}>
				<${Outcome.NAME} data-json-index="2" style="border: 2px solid orange;"></${Outcome.NAME}>
			</div>`
		);
	}
}
