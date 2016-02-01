class GameUserInterface {
	private _windowElement : JQuery;
	private _tabstrip: kendo.ui.TabStrip;
	
	private _starmap: Starmap;
	private _inspector: Inspector;
	private _turnManager: TurnManager;
	private _log: Log;
	private _commandList: CommandList;
	
	private _allowShutdown: boolean;

	constructor(windowElement : JQuery, starmapElement : JQuery, inspectorElement : JQuery, turnManagerElement : JQuery, 
			logElement: JQuery, commandListElement: JQuery, allowShutdown : boolean) {
		this._windowElement = windowElement;
		
		this._starmap = new Starmap(starmapElement);
		this._inspector = new Inspector(inspectorElement);
		this._turnManager = new TurnManager(turnManagerElement);
		this._log = new Log(logElement);
		this._commandList = new CommandList(commandListElement);
		
		this._allowShutdown = allowShutdown;
		
		$(this._starmap).on('selectStar', (event, star) => { this._inspector.showStar(star); });
		$(this._starmap).on('selectFleet', (event, fleet) => { this._inspector.showFleet(fleet); });
		$(this._starmap).on('selectVoid', (event, star) => { this._inspector.clear(); });
		$(this._starmap).on('dispatchFleet', (event, fleet, star) => 
				{ this._inspector.showDispatchFleetConfirmation(fleet, star); });
		
		$(this._inspector).on('dispatchFleet', (event, fleet, star) => { 
			this._tabstrip.select(1);
			this._commandList.addDispatchFleetCommand(fleet, star); 
		});
		
		$(this._turnManager).on('finishTurn', () => { $(this).triggerHandler('finishTurn', this); });
	}
	
	show() {
		this._windowElement.kendoWindow({
			title: 'Rising Empire',
			actions: [], draggable: false, resizable: false
		}).data("kendoWindow").maximize();
		
		let buttonDiv = $('<div>');
		
		buttonDiv.append($('<input>').attr('type', 'button').attr('value', 'Logout').kendoButton({ click:
			() => {
				$(this).triggerHandler('logout', this);
			}					 
		}));
		
		if (this._allowShutdown) {
			buttonDiv.prepend($('<span>').html('&nbsp;&nbsp;'));
			buttonDiv.prepend($('<input>').attr('type', 'button').attr('value', 'Shutdown').kendoButton({ click:
				() => {
					$(this).triggerHandler('shutdown', this);
				}					 
			}));
		}
		// NOTE Trick the logout button into the title of the window
		$('.k-window-actions').css('padding-top', '0px').prepend(buttonDiv.css('transform', 'scale(0.7)'));
		this._tabstrip = $('#tabstrip').kendoTabStrip({ animation: false }).data("kendoTabStrip");
		
		kendo.ui.progress($('body'), true);
		$('.k-loading-mask').css('z-index', '99999');
	}	

	update(turn: number, player: Server.Message.Player, view: Server.Message.UniverseView, colorMapping : any) {
		this._turnManager.enableTurnButton();
		kendo.ui.progress($('body'), false);
		this._turnManager.newTurn(turn, player, colorMapping[player.nation]);
		this._starmap.update(view, colorMapping);
	}

	log(message: string) {
		this._tabstrip.select(0);
		this._log.log(message);
	}

	getAndRemoveCommands(): Server.Message.CommandsMessage {
		return this._commandList.getAndRemoveCommands();
	}
}

class Starmap {
	private _starmapElement: JQuery;
	private _selected: Server.Message.View.Star | Server.Message.View.Fleet = null;

	constructor(starmapElement: JQuery) {
		this._starmapElement = starmapElement.css('background-color', 'black').css('cursor', 'default');
		
		this._starmapElement.click((event) => {
			if (event.target == this._starmapElement[0]) {
				this._starmapElement.find('.star').find('.image').css('background-color', '');
				this._starmapElement.find('.fleet').css('background-color', '');
				this._selected = null;
				$(this).triggerHandler('selectVoid');
			}
		});
	}

	update(view: Server.Message.UniverseView, colorMapping : any) {
		this._starmapElement.empty();

		let starMapping = {};
		for (let i = 0; i < view.stars.length; i++) {
			let star = view.stars[i];
			starMapping[star.name] = star;
			let color = colorMapping[star.nation] ? colorMapping[star.nation] : '#aaaaaa';
			let starElement : JQuery, imageElement : JQuery, nameElement : JQuery;
			this._starmapElement.append(starElement = $('<div>').addClass('star').css('position', 'absolute').css('cursor', 'pointer').append(
				imageElement = $('<div>').addClass('image').css('width', '24px').css('height', '24px').css('padding', '2px')
				.html('&nbsp;')
				.css('background-image', 'url("/images/star.png")').css('background-position', 'center').css('background-repeat', 'no-repeat'),
				nameElement = $('<div>').addClass('name').css('color', color).text(star.name)
			).css('left', star.x + 'px').css('top', star.y + 'px').data('star', star).click((event) => {
				let starElement = $(event.target);
				let star = starElement.data('star');
				if(!star) {
					starElement = starElement.parent();
					star = starElement.data('star');
				}				
				this._starmapElement.find('.star').find('.image').css('background-color', '');
				this._starmapElement.find('.fleet').css('background-color', '');
				starElement.find('.image').css('background-color', 'orange');
				
				if(this._selected != null && this._selected['id'] && this._selected['dispatchable'] && star['name'] !== this._selected['star']) {
					$(this).triggerHandler('dispatchFleet', [this._selected, star]);
				} else {
					$(this).triggerHandler('selectStar', star);
				}
				this._selected = star;
			}));
			
			// center the star name
			if(nameElement.width() > imageElement.width()) {
				imageElement.css('margin-left', ((nameElement.width() - imageElement.width()) / 2) + 'px');
			}
			
			// move center of whole DOM element to the original x and y coordinates
			starElement.css('left', ((star.x - starElement.width() / 2) - /* padding */ 2) + 'px')
					.css('top', ((star.y - nameElement.height() / 2) - /* mystery */ 4 - /* padding */ 2) + 'px');
			
			if (this._selected != null && this._selected['name'] == star.name) {
				starElement.find('.image').css('background-color', 'orange');
				$(this).triggerHandler('selectStar', star);
			}
		}
		
		let fleetSvg = "<svg class='fleet-image' xmlns='http://www.w3.org/2000/svg' width='140' height='60'><rect x='0' y='0'  width='140' height='60' fill='black'/><rect x='40' y='20'  width='80' height='20' fill='${fill-color}'/><rect x='20' y='0' width='40' height='20' fill='${fill-color}'/><rect x='20' y='40' width='40' height='20' fill='${fill-color}'/></svg>";
		for (let i = 0; i < view.fleets.length; i++) {
			let fleet = view.fleets[i];
			let color = colorMapping[fleet.nation];
			let x = fleet.x, y = fleet.y;
			
			if(fleet.star !== null) {
				let star = <Server.Message.View.Star>starMapping[fleet.star];
				
				x = star.x + 20; 
				y = star.y - 14;
			}
			
			let fleetElement : JQuery;
			this._starmapElement.append(fleetElement = $('<div>').addClass('fleet').css('position', 'absolute')
			.css('transform', 'scale(0.1)').css('cursor', 'pointer')
			.css('padding', '2px').css('left', x + 'px').css('top', y + 'px').css('z-index', '500')
			.data('fleet', fleet).append($('<div>').addClass('image').html('&nbsp;')
					.css('background-image', 'url("data:image/svg+xml;utf8,' + fleetSvg.replace(/\${fill-color}/g, color) + '")')
					.css('background-position', 'center').css('background-repeat', 'no-repeat')
					.css('width', '140px').css('height', '60px')
					.css('padding', '15px')
					.click((event) => {
				let fleetElement = $(event.target);
				let fleet = fleetElement.data('fleet');
				if(!fleet) {
					fleetElement = fleetElement.parent();
					fleet = fleetElement.data('fleet');
				}	
				this._starmapElement.find('.star').find('.image').css('background-color', '');
				this._starmapElement.find('.fleet').css('background-color', '');
				fleetElement.css('background-color', 'orange');
				this._selected = fleet;
				$(this).triggerHandler('selectFleet', fleet);
			})));
			
			// move center of whole DOM element to the original x and y coordinates
			fleetElement.css('left', (x - fleetElement.width() / 2) + 'px')
					.css('top', (y - fleetElement.height() / 2) + 'px');
			
			if (this._selected != null && this._selected['id'] == fleet.id) {
				fleetElement.css('background-color', 'orange');
				$(this).triggerHandler('selectFleet', fleet);
			}					
		}
	}
}

class Inspector {
	private _inspectorElement: JQuery;

	constructor(inspectorElement: JQuery) {
		this._inspectorElement = inspectorElement;
	}
	
	clear() {
		this._inspectorElement.empty();
	}
	
	showStar(star: Server.Message.View.Star) {
		this._inspectorElement.empty().append($('<div>').text('Star: ' + star.name));
	}

	showFleet(fleet: Server.Message.View.Fleet) {
		this._inspectorElement.empty().append($('<div>').text('Fleet id: ' + fleet.id));
	}

	showDispatchFleetConfirmation(fleet: Server.Message.View.Fleet, star: Server.Message.View.Star) {
		this._inspectorElement.empty().append($('<div>').text('Dispatch fleet confirmation for: ' + fleet.id + ' to ' + star.name));
		
		this._inspectorElement.append($('<input>').attr('type', 'button').attr('value', 'Yes')
				.kendoButton({ click: () => { $(this).triggerHandler('dispatchFleet', [fleet, star]); this.showStar(star); }}));
		this._inspectorElement.append($('<input>').attr('type', 'button').attr('value', 'No')
				.kendoButton({ click: () => { this.showStar(star) }}));
	}
}

class TurnManager {
	private _turnManagerElement: JQuery;
	private _turnLabelElement: JQuery;
	private _turnButton: kendo.ui.Button;

	constructor(turnManagerElement: JQuery) {
		this._turnManagerElement = turnManagerElement;
	}
	
	enableTurnButton() {
		if(this._turnButton) {
			this._turnButton.enable(true);
		}
	}
	
	newTurn(turn : number, player: Server.Message.Player, color: string) {
		if (!this._turnManagerElement.children().length) {
			let rowsElement = $('<table>').append(
					$('<tr>').append(
						$('<td>').append($('<span>').css('background-color', color).css('width', '16px')
								.css('height', '16px').css('display', 'inline-block').html('&nbsp;')), 
						$('<td>').text(player.name), 
						$('<td>').text(player.nation)),
					$('<tr>').append(
						$('<td>').html('&nbsp;'), 
						$('<td>').text('Turn'), 
						$('<td>').append(this._turnLabelElement = $('<span>'))
					)
				);
			this._turnManagerElement.append(rowsElement);
			let turnButtonElement : JQuery;
			this._turnManagerElement.append(turnButtonElement = $('<input>').attr('type', 'button').attr('value', 'Turn')
				.kendoButton({ click: () => { this._turnButton.enable(false); $(this).triggerHandler('finishTurn'); }}));
			this._turnButton = turnButtonElement.data('kendoButton');
		}
		
		this._turnLabelElement.text(turn);
	}
}

class Log {
	private _logElement: JQuery;

	constructor(logElement: JQuery) {
		this._logElement = logElement;
	}

	log(message: string) {
		let timestamp = moment().format('D.M.YYYY HH:mm:ss');
		this._logElement.prepend($('<div>').text(timestamp + ' ' + message));
	}
}

class CommandList {
	private _commandListElement: JQuery;
	private _commands: Command[] = [];

	constructor(commandListElement: JQuery) {
		this._commandListElement = commandListElement;
	}
	
	getAndRemoveCommands() : Server.Message.CommandsMessage{
		let message = CommandBuilder.bundleCommands(this._commands);
		this._commands = [];
		this._commandListElement.empty();
		return message;
	}
	
	addDispatchFleetCommand(fleet: Server.Message.View.Fleet, star: Server.Message.View.Star) {
		this._commandListElement.prepend($('<div>').text('Dispatch fleet with id = ' + fleet.id + ' to star "' + star.name + '".'));
		this._commands.push(CommandBuilder.createFleetDispatchCommand(fleet.id, star.name));
	}
}
