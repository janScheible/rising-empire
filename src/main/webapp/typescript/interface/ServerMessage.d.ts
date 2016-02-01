declare module Server.Message {
	class CsrfToken {
		headerName: string;
		token: string;
	}

	class TurnFinishedMessage {
		player: Player;
		turn: number;
		view: UniverseView;
		colorMapping: any;
	}
	
	class Player {
		name: string;
		nation: string;
	}

	class PlayerEntry {
		color: string;
		username: string;
		leaderName: string;
		nation: string;
		state: string; // {ACTIVE, DETACHED, AI, NON_PARTICIPATING}
	}

	class UniverseView {
		nations: Server.Message.View.Nation[];
		stars: Server.Message.View.Star[];
		fleets: Server.Message.View.Fleet[];
		
		events: Server.Message.View.Event[];
	}

	class TurnInfoMessage {
		status: any;
	}

	class CommandsMessage {
		commands: any[];
	}
}

declare module Server.Message.View {
	class Nation {
	}

	class Star {
		name: string;
		
		nation: string;
		population: number;

		x: number;
		y: number;
	}

	class Fleet {
		id: number;
		
		star: string;
		nation: string;
		
		x : number;
		y : number;
		
		dispatchable: boolean;
	}
	
	class Event {
		type: string; // {RANDOM_MESSAGE}
	}
}