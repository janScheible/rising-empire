import SpaceCombatPage from '~/page/space-combat-page/space-combat-page';
import PageStoryWrapper from '~/storybook/component/page-story-wrapper';
import Story from '~/storybook/stories/story';

export default class SpaceCombatPageStories {
	static #RENDER_DATA = `{
			"systemName": "Sol",
			"attacker": "MYXALOR",
			"attackerPlayerColor": "blue",
			"fireExchangeCount": 4,
			"attackerShipSpecs": [
				{
					"id": "Attacker@1",
					"name": "Attacker",
					"ships": {
						"count": 3,
						"previousCount": 3,
						"playerColor": "red",
						"size": "MEDIUM"
					},
					"fireExchanges": {
						"0": {
							"lostHitPoints": 7,
							"count": 2
						},
						"1": {
							"lostHitPoints": 3,
							"count": 1
						},
						"3": {
							"lostHitPoints": 12,
							"count": 0
						}
					}
				}
			],
			"defender": "LUMERISKS",
			"defenderPlayerColor": "white",
			"defenderShipSpecs": [
				{
					"id": "Scout@1",
					"name": "Scout",
					"shield": 0,
					"beamDefence": 3,
					"attackLevel": 0,
					"damage": 0,
					"missleDefence": 3,
					"hits": 3,
					"speed": 1,
					"ships": {
						"count": 5,
						"previousCount": 5,
						"playerColor": "green",
						"size": "SMALL"
					},
					"fireExchanges": {},
					"equipment": [
						"Reserve Tanks"
					]
				},
				{
					"id": "Destroyer@2",
					"name": "Destroyer",
					"shield": 2,
					"beamDefence": 3,
					"attackLevel": 0,
					"damage": 0,
					"missleDefence": 3,
					"hits": 3,
					"speed": 1,
					"ships": {
						"count": 4,
						"previousCount": 4,
						"playerColor": "green",
						"size": "MEDIUM"
					},
					"fireExchanges": {
						"2": {
							"lostHitPoints": 9,
							"damage": 2,
							"count": 3
						}
					},
					"equipment": []
				}
			],
			"combatOutcome": {
				"outcome": "VICTORY"
			},			
			"_actions": [
				{
					"fields": [],
					"name": "continue"
				}
			]
		}`;

	static showSpaceCombatPageAnimated(story: Story) {
		story.setRenderData(SpaceCombatPageStories.#RENDER_DATA);
		SpaceCombatPageStories.#showStoryHtml(story);
	}

	static showSpaceCombatPage(story: Story) {
		const renderData = JSON.parse(SpaceCombatPageStories.#RENDER_DATA);
		renderData.fireExchangeCount = 0;
		renderData.attackerShipSpecs[0].ships.count = 0;
		renderData.attackerShipSpecs[0].fireExchanges = {};
		renderData.defenderShipSpecs[1].damage = 2;
		renderData.defenderShipSpecs[1].ships.count = 3;
		renderData.defenderShipSpecs[1].fireExchanges = {};
		story.setRenderData(JSON.stringify(renderData));

		SpaceCombatPageStories.#showStoryHtml(story);
	}

	static #showStoryHtml(story: Story) {
		story.showHtml(`
		<${PageStoryWrapper.NAME} close-action="continue">
			<${SpaceCombatPage.NAME}></${SpaceCombatPage.NAME}>
		</${PageStoryWrapper.NAME}>`);
	}
}
