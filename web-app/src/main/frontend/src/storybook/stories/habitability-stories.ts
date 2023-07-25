import FlowLayout from '~/component/flow-layout';
import Habitability from '~/page/main-page/component/inspector/component/habitability';
import Story from '~/storybook/stories/story';

export default class HabitabilityStories {
	static showHabitability(story: Story) {
		story.setRenderData(`[
			{
				"type": "OCEAN",
				"special": "ARTIFACTS",
				"maxPopulation": 250
			},
			null
		]`);

		story.showHtml(`
			<${FlowLayout.NAME} direction="column" gap="XL">
				<${Habitability.NAME} data-json-index="0"></${Habitability.NAME}>
				<${Habitability.NAME} data-json-index="1"></${Habitability.NAME}>				
			</${FlowLayout.NAME}>`);
	}
}
