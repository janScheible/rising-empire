class Communication {
	private _csrfToken : Server.Message.CsrfToken;
	private _stompClient : Client;
	
	constructor() {
		let csrfToken : Server.Message.CsrfToken = JSON.parse($.ajax({
			type: 'GET',
			url: 'csrf',
			dataType: 'json',
			success: function() { },
			data: {},
			async: false
		}).responseText);
		this._csrfToken = csrfToken;
		
		let socket = new SockJS('/risingempire');
		
		this._stompClient = Stomp.over(socket);        
		let headers = {};
		headers[csrfToken.headerName] = csrfToken.token;
		
		this._stompClient.connect(headers, () => {
			$(this).triggerHandler('connect', this);
		});
	}
	
	shutdown() {
		let headers = { };
		headers[this._csrfToken.headerName] = this._csrfToken.token;
		$.ajax({
			type: 'post', 
			url: 'shutdown', 
			headers: headers
		});
	}
	
	subscribePlayerJoin(handler: (message: Server.Message.PlayerEntry) => any) {
		this._stompClient.subscribe('/topic/player/join', function(message: StompMessage) {
			let playerEntry: Server.Message.PlayerEntry = JSON.parse(message.body);
			handler(playerEntry);
		});
	}
	
	subscribePlayerLeave(handler: (message: Server.Message.PlayerEntry) => any) {
		this._stompClient.subscribe('/topic/player/leave', function(message: StompMessage) {
			let playerEntry: Server.Message.PlayerEntry = JSON.parse(message.body);
			handler(playerEntry);
		});
	}	
	
	subscribeTurnUpdate(handler: (message: Server.Message.TurnFinishedMessage) => any) {
		this._stompClient.subscribe('/user/topic/turn/update', function(message: StompMessage) {
			let turnFinishedMessage: Server.Message.TurnFinishedMessage = JSON.parse(message.body);
			handler(turnFinishedMessage);
		});
	}	
	
	subscribeTurnInfo(handler: (message: Server.Message.TurnInfoMessage) => any) {
		this._stompClient.subscribe('/topic/turn/info', function(message: StompMessage) {
			let turnInfoMessage: Server.Message.TurnInfoMessage = JSON.parse(message.body);
			handler(turnInfoMessage);
		});
	}			
	
	sendCommands(message: Server.Message.CommandsMessage) {
		this._stompClient.send("/app/commands", {}, JSON.stringify(message));		
	}
}