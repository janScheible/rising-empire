class Game {
	static init(logoutCallback, devMode : boolean) {
		let userInterface = new GameUserInterface($('#gameWindow'), $('#starmap'), $('#inspector'), $('#turnManager'), $('#log'), $('#commandList'), devMode);
		userInterface.show();
		
		$(userInterface).on('logout', (event, userInterface: GameUserInterface) => {
			logoutCallback();
		});

		let communication = new Communication();
		$(communication).on('connect', function(event, communication : Communication) {
			communication.subscribePlayerJoin(function(playerEntry) {
				userInterface.log(playerEntry.leaderName + ' of the ' + playerEntry.nation + ' joined the game.');
			});

			communication.subscribePlayerLeave(function(playerEntry) {
				userInterface.log(playerEntry.leaderName + ' of the ' + playerEntry.nation + ' left the game.');
			});

			communication.subscribeTurnUpdate(function(turnFinishedMessage) {
				userInterface.update(turnFinishedMessage.turn, turnFinishedMessage.player, turnFinishedMessage.view, turnFinishedMessage.colorMapping);					
				
				for (let i = 0; i < turnFinishedMessage.view.events.length; i++) {
					let event : Server.Message.View.Event = turnFinishedMessage.view.events[i];
					if(event.type === 'RANDOM_MESSAGE') {
						userInterface.log('Event: ' + event['message']);		
					} else {
						console.warn('Events of type "' + event.type + "' are not supported by the client.");
					}
				}
				
				userInterface.log('Turn ' + turnFinishedMessage.turn + '...');
			});				

			communication.subscribeTurnInfo(function(turnInfoMessage) {
				userInterface.log(JSON.stringify(turnInfoMessage.status).replace(/"/g, '').replace('{', '').replace('}', '').replace(/,/g, ', '));
			});
		});

		$(userInterface).on('finishTurn', function(event, userInterface: GameUserInterface) {
			let message : Server.Message.CommandsMessage = userInterface.getAndRemoveCommands();
			userInterface.log('Sending ' + message.commands[1].length + ' commands to the server...');			
			communication.sendCommands(message);
		});
		
		$(userInterface).on('shutdown', function(event) {
			communication.shutdown();
		});
        
        $(userInterface).on('h2-console', function(event) {
			alert('Please see browser console for settings.');
            console.log("Saved Settings: 'Generic H2 (Embedded)'");
            console.log("JDBC URL: 'jdbc:h2:mem:testdb' (Spring Boot default)");
            window.open('/h2-console', '_blank')
		});        
	}
}
