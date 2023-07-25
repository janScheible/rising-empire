import SliderGroup from '~/component/slider-group';
import SliderGroupCategory from '~/component/silder-group-category';
import Story from '~/storybook/stories/story';

export default class SliderGroupStories {
	static showSliderGroup(story: Story) {
		story.setRenderData(`{
			"categories": {
				"ship": { "value": 10, "status": "None" },
				"defence": { "value": 15, "status": "None" },
				"industry": { "value": 20, "status": "2.7/y" },
				"ecology": { "value": 25, "status": "Clean" },
				"technology": { "value": 30, "status": "0RP" }				
			},
			"locked": "defence",
				"_actions": [
			{
					"fields": [],
					"name": "allocate-spending"
				}
			]
		}`);

		story.showHtml(`
			<${SliderGroup.NAME} class="inspector-child-story" select-action="allocate-spending">
				<${SliderGroupCategory.NAME} qualifier="ship">Ship</${SliderGroupCategory.NAME}>
				<${SliderGroupCategory.NAME} qualifier="defence">Def</${SliderGroupCategory.NAME}>
				<${SliderGroupCategory.NAME} qualifier="industry">Ind</${SliderGroupCategory.NAME}>
				<${SliderGroupCategory.NAME} qualifier="ecology">Eco</${SliderGroupCategory.NAME}>
				<${SliderGroupCategory.NAME} qualifier="technology">Tech</${SliderGroupCategory.NAME}>
			</${SliderGroup.NAME}>`);
	}
}
