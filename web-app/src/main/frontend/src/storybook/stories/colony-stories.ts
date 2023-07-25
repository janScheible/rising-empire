import FlowLayout from '~/component/flow-layout';
import Colony from '~/page/main-page/component/inspector/component/system-details/component/colony';
import Story from '~/storybook/stories/story';

export default class ColonyStories {
	static showColony(story: Story) {
		story.setRenderData(`[
			{
				"population": 50,
				"bases": 0,
				"production": {
					"net":266,
					"gross": 444
				} 
			},
			{
				"population": 50,
				"bases": 0,
				"race": "Borg",
				"playerColor": "red"
			},
			null
		]`);

		story.showHtml(`
			<${FlowLayout.NAME} direction="column" gap="XL">
				<${Colony.NAME} class="inspector-child-story" data-json-index="0"></${Colony.NAME}>
				<${Colony.NAME} class="inspector-child-story" data-json-index="1"></${Colony.NAME}>
				<${Colony.NAME} class="inspector-child-story" data-json-index="2"></${Colony.NAME}>
			</${FlowLayout.NAME}>
		`);
	}
}
