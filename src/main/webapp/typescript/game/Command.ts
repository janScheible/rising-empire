class Command {
}

class CommandBuilder {
	static createFleetDispatchCommand(fleedId: number, destinationStartName: string) : Command {
		return ['com.scheible.risingempire.web.game.message.client.command.FleetDispatchCommand', {
			'fleedId': fleedId,
			'destinationStar': destinationStartName
		}];
	}
	
	static bundleCommands(commands : Command[]) : Server.Message.CommandsMessage {
		return {
			'commands': ['java.util.ArrayList', commands]
		};
	}
}