import TransferColonists from '~/page/main-page/component/inspector/component/system-details/component/transfer-colonists';
import Story from '~/storybook/stories/story';

export default class TransferColonistsStories {
	static showTransferColonists(story: Story) {
		story.setRenderData(`[{
				"colonists": 0,
				"maxColonists": 50,
				"_actions": [{ "name": "cancel" }]
			}, {
				"eta": 2,
				"colonists": 5,
				"maxColonists": 50,
				"_actions": [{ "name": "cancel" }, { "name": "transfer", "fields": [{"name": "colonists", "value": 5}] }]
			}, {
				"eta": 2,
				"colonists": 25,
				"maxColonists": 50,
				"warningThreshold": 20,
				"_actions": [{ "name": "cancel" }, { "name": "transfer", "fields": [{"name": "colonists", "value": 5}] }]
			}]`);

		story.showHtml(`<div>
			<${TransferColonists.NAME} data-json-index="0" class="inspector-child-story" style="border: 2px solid red;"></${TransferColonists.NAME}>
			<${TransferColonists.NAME} data-json-index="1" class="inspector-child-story" style="border: 2px solid orange;"></${TransferColonists.NAME}>
			<${TransferColonists.NAME} data-json-index="2" class="inspector-child-story" style="border: 2px solid orange;"></${TransferColonists.NAME}>
		</div>`);
	}
}
