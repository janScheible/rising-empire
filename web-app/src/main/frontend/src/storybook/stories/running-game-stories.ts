import RunningGame from '~/game-browser/component/running-game';
import Story from '~/storybook/stories/story';

export default class RunningGameStories {
	static showRunningGame(story: Story) {
		story.setRenderData(`{
			"gameId": "my-game",
			"players": [
				{ 
					"playerColor": "yellow",
					"interactive": true,
					"playerSessionId": "abcd",
					"canReceiveNotifications": true,
					"_actions": []
				},
				{ 
					"playerColor": "blue",
					"interactive": true,
					"playerSessionId": "xyz",
					"canReceiveNotifications": false,
					"_actions": [
						{
						  "fields": [],
						  "name": "kick",
						  "method": "DELETE",
						  "href": "/frontend/my-game/blue"
						},
						{
							"fields": [{ "name": "gameId", "value": "my-game" }, { "name": "player", "value": "blue" }],
							"name": "join",
							"method": "GET",
							"href": "/frontend/my-game/blue"
						  }
					  ]
				},
				{ 
					"playerColor": "white", 
					"_actions": [
						{
							"fields": [{ "name": "gameId", "value": "my-game" }, { "name": "player", "value": "blue" }],
							"name": "join",
							"method": "GET",
							"href": "/frontend/my-game/white"
						}
					] 
				}
			],
			"_actions": [
				{
				  "fields": [],
				  "name": "stop",
				  "method": "DELETE",
				  "href": "/frontend/my-game"
				}
			  ]
		}`);

		story.showHtml(`<${RunningGame.NAME}></${RunningGame.NAME}>`);
	}
}
