var Game = (function () {
    function Game() {
    }
    Game.init = function (logoutCallback, devMode) {
        var userInterface = new GameUserInterface($('#gameWindow'), $('#starmap'), $('#inspector'), $('#turnManager'), $('#log'), $('#commandList'), devMode);
        userInterface.show();
        $(userInterface).on('logout', function (event, userInterface) {
            logoutCallback();
        });
        var communication = new Communication();
        $(communication).on('connect', function (event, communication) {
            communication.subscribePlayerJoin(function (playerEntry) {
                userInterface.log(playerEntry.leaderName + ' of the ' + playerEntry.nation + ' joined the game.');
            });
            communication.subscribePlayerLeave(function (playerEntry) {
                userInterface.log(playerEntry.leaderName + ' of the ' + playerEntry.nation + ' left the game.');
            });
            communication.subscribeTurnUpdate(function (turnFinishedMessage) {
                userInterface.update(turnFinishedMessage.turn, turnFinishedMessage.player, turnFinishedMessage.view, turnFinishedMessage.colorMapping);
                for (var i = 0; i < turnFinishedMessage.view.events.length; i++) {
                    var event_1 = turnFinishedMessage.view.events[i];
                    if (event_1.type === 'RANDOM_MESSAGE') {
                        userInterface.log('Event: ' + event_1['message']);
                    }
                    else {
                        console.warn('Events of type "' + event_1.type + "' are not supported by the client.");
                    }
                }
                userInterface.log('Turn ' + turnFinishedMessage.turn + '...');
            });
            communication.subscribeTurnInfo(function (turnInfoMessage) {
                userInterface.log(JSON.stringify(turnInfoMessage.status).replace(/"/g, '').replace('{', '').replace('}', '').replace(/,/g, ', '));
            });
        });
        $(userInterface).on('finishTurn', function (event, userInterface) {
            var message = userInterface.getAndRemoveCommands();
            userInterface.log('Sending ' + message.commands[1].length + ' commands to the server...');
            communication.sendCommands(message);
        });
        $(userInterface).on('shutdown', function (event) {
            communication.shutdown();
        });
        $(userInterface).on('h2-console', function (event) {
            alert('Please see browser console for settings.');
            console.log("Saved Settings: 'Generic H2 (Embedded)'");
            console.log("JDBC URL: 'jdbc:h2:mem:testdb' (Spring Boot default)");
            window.open('/h2-console', '_blank');
        });
    };
    return Game;
}());
var Join = (function () {
    function Join() {
    }
    Join.init = function () {
        var userInterface = new JoinUserInterface($("#joinWindow"), $("form[name='loginForm']"), $('input[name=username]'));
        userInterface.show();
        $.getJSON('players', {}).done(userInterface.setPlayerEntries.bind(userInterface));
    };
    return Join;
}());
var Command = (function () {
    function Command() {
    }
    return Command;
}());
var CommandBuilder = (function () {
    function CommandBuilder() {
    }
    CommandBuilder.createFleetDispatchCommand = function (fleedId, destinationStartName) {
        return ['com.scheible.risingempire.web.game.message.client.command.FleetDispatchCommand', {
                'fleedId': fleedId,
                'destinationStar': destinationStartName
            }];
    };
    CommandBuilder.bundleCommands = function (commands) {
        return {
            'commands': ['java.util.ArrayList', commands]
        };
    };
    return CommandBuilder;
}());
var Communication = (function () {
    function Communication() {
        var _this = this;
        var csrfToken = JSON.parse($.ajax({
            type: 'GET',
            url: 'csrf',
            dataType: 'json',
            success: function () { },
            data: {},
            async: false
        }).responseText);
        this._csrfToken = csrfToken;
        var socket = new SockJS('/risingempire');
        this._stompClient = Stomp.over(socket);
        var headers = {};
        headers[csrfToken.headerName] = csrfToken.token;
        this._stompClient.connect(headers, function () {
            $(_this).triggerHandler('connect', _this);
        });
    }
    Communication.prototype.shutdown = function () {
        var headers = {};
        headers[this._csrfToken.headerName] = this._csrfToken.token;
        $.ajax({
            type: 'post',
            url: 'shutdown',
            headers: headers
        });
    };
    Communication.prototype.subscribePlayerJoin = function (handler) {
        this._stompClient.subscribe('/topic/player/join', function (message) {
            var playerEntry = JSON.parse(message.body);
            handler(playerEntry);
        });
    };
    Communication.prototype.subscribePlayerLeave = function (handler) {
        this._stompClient.subscribe('/topic/player/leave', function (message) {
            var playerEntry = JSON.parse(message.body);
            handler(playerEntry);
        });
    };
    Communication.prototype.subscribeTurnUpdate = function (handler) {
        this._stompClient.subscribe('/user/topic/turn/update', function (message) {
            var turnFinishedMessage = JSON.parse(message.body);
            handler(turnFinishedMessage);
        });
    };
    Communication.prototype.subscribeTurnInfo = function (handler) {
        this._stompClient.subscribe('/topic/turn/info', function (message) {
            var turnInfoMessage = JSON.parse(message.body);
            handler(turnInfoMessage);
        });
    };
    Communication.prototype.sendCommands = function (message) {
        this._stompClient.send("/app/commands", {}, JSON.stringify(message));
    };
    return Communication;
}());
var GameUserInterface = (function () {
    function GameUserInterface(windowElement, starmapElement, inspectorElement, turnManagerElement, logElement, commandListElement, allowShutdown) {
        var _this = this;
        this._windowElement = windowElement;
        this._starmap = new Starmap(starmapElement);
        this._inspector = new Inspector(inspectorElement);
        this._turnManager = new TurnManager(turnManagerElement);
        this._log = new Log(logElement);
        this._commandList = new CommandList(commandListElement);
        this._allowShutdown = allowShutdown;
        $(this._starmap).on('selectStar', function (event, star) { _this._inspector.showStar(star); });
        $(this._starmap).on('selectFleet', function (event, fleet) { _this._inspector.showFleet(fleet); });
        $(this._starmap).on('selectVoid', function (event, star) { _this._inspector.clear(); });
        $(this._starmap).on('dispatchFleet', function (event, fleet, star) { _this._inspector.showDispatchFleetConfirmation(fleet, star); });
        $(this._inspector).on('dispatchFleet', function (event, fleet, star) {
            _this._tabstrip.select(1);
            _this._commandList.addDispatchFleetCommand(fleet, star);
        });
        $(this._turnManager).on('finishTurn', function () { $(_this).triggerHandler('finishTurn', _this); });
    }
    GameUserInterface.prototype.show = function () {
        var _this = this;
        this._windowElement.kendoWindow({
            title: 'Rising Empire',
            actions: [], draggable: false, resizable: false
        }).data("kendoWindow").maximize();
        var buttonDiv = $('<div>');
        buttonDiv.append($('<input>').attr('type', 'button').attr('value', 'Logout').kendoButton({ click: function () {
                $(_this).triggerHandler('logout', _this);
            }
        }));
        if (this._allowShutdown) {
            buttonDiv.prepend($('<span>').html('&nbsp;&nbsp;'));
            buttonDiv.prepend($('<input>').attr('type', 'button').attr('value', 'Shutdown').kendoButton({ click: function () {
                    $(_this).triggerHandler('shutdown', _this);
                }
            }));
        }
        buttonDiv.prepend($('<span>').html('&nbsp;&nbsp;'));
        buttonDiv.prepend($('<input>').attr('type', 'button').attr('value', 'H2 Console').kendoButton({ click: function () {
                $(_this).triggerHandler('h2-console', _this);
            }
        }));
        // NOTE Trick the logout button into the title of the window
        $('.k-window-actions').css('padding-top', '0px').prepend(buttonDiv.css('transform', 'scale(0.7)'));
        this._tabstrip = $('#tabstrip').kendoTabStrip({ animation: false }).data("kendoTabStrip");
        kendo.ui.progress($('body'), true);
        $('.k-loading-mask').css('z-index', '99999');
    };
    GameUserInterface.prototype.update = function (turn, player, view, colorMapping) {
        this._turnManager.enableTurnButton();
        kendo.ui.progress($('body'), false);
        this._turnManager.newTurn(turn, player, colorMapping[player.nation]);
        this._starmap.update(view, colorMapping);
    };
    GameUserInterface.prototype.log = function (message) {
        this._tabstrip.select(0);
        this._log.log(message);
    };
    GameUserInterface.prototype.getAndRemoveCommands = function () {
        return this._commandList.getAndRemoveCommands();
    };
    return GameUserInterface;
}());
var Starmap = (function () {
    function Starmap(starmapElement) {
        var _this = this;
        this._selected = null;
        this._starmapElement = starmapElement.css('background-color', 'black').css('cursor', 'default');
        this._starmapElement.click(function (event) {
            if (event.target == _this._starmapElement[0]) {
                _this._starmapElement.find('.star').find('.image').css('background-color', '');
                _this._starmapElement.find('.fleet').css('background-color', '');
                _this._selected = null;
                $(_this).triggerHandler('selectVoid');
            }
        });
    }
    Starmap.prototype.update = function (view, colorMapping) {
        var _this = this;
        this._starmapElement.empty();
        var starMapping = {};
        for (var i = 0; i < view.stars.length; i++) {
            var star = view.stars[i];
            starMapping[star.name] = star;
            var color = colorMapping[star.nation] ? colorMapping[star.nation] : '#aaaaaa';
            var starElement = void 0, imageElement = void 0, nameElement = void 0;
            this._starmapElement.append(starElement = $('<div>').addClass('star').css('position', 'absolute').css('cursor', 'pointer').append(imageElement = $('<div>').addClass('image').css('width', '24px').css('height', '24px').css('padding', '2px')
                .html('&nbsp;')
                .css('background-image', 'url("/images/star.png")').css('background-position', 'center').css('background-repeat', 'no-repeat'), nameElement = $('<div>').addClass('name').css('color', color).text(star.name)).css('left', star.x + 'px').css('top', star.y + 'px').data('star', star).click(function (event) {
                var starElement = $(event.target);
                var star = starElement.data('star');
                if (!star) {
                    starElement = starElement.parent();
                    star = starElement.data('star');
                }
                _this._starmapElement.find('.star').find('.image').css('background-color', '');
                _this._starmapElement.find('.fleet').css('background-color', '');
                starElement.find('.image').css('background-color', 'orange');
                if (_this._selected != null && _this._selected['id'] && _this._selected['dispatchable'] && star['name'] !== _this._selected['star']) {
                    $(_this).triggerHandler('dispatchFleet', [_this._selected, star]);
                }
                else {
                    $(_this).triggerHandler('selectStar', star);
                }
                _this._selected = star;
            }));
            // center the star name
            if (nameElement.width() > imageElement.width()) {
                imageElement.css('margin-left', ((nameElement.width() - imageElement.width()) / 2) + 'px');
            }
            // move center of whole DOM element to the original x and y coordinates
            starElement.css('left', ((star.x - starElement.width() / 2) - 2) + 'px')
                .css('top', ((star.y - nameElement.height() / 2) - 4 - 2) + 'px');
            if (this._selected != null && this._selected['name'] == star.name) {
                starElement.find('.image').css('background-color', 'orange');
                $(this).triggerHandler('selectStar', star);
            }
        }
        var fleetSvg = "<svg class='fleet-image' xmlns='http://www.w3.org/2000/svg' width='140' height='60'><rect x='0' y='0'  width='140' height='60' fill='black'/><rect x='40' y='20'  width='80' height='20' fill='${fill-color}'/><rect x='20' y='0' width='40' height='20' fill='${fill-color}'/><rect x='20' y='40' width='40' height='20' fill='${fill-color}'/></svg>";
        for (var i = 0; i < view.fleets.length; i++) {
            var fleet = view.fleets[i];
            var color = colorMapping[fleet.nation];
            var x = fleet.x, y = fleet.y;
            if (fleet.star !== null) {
                var star = starMapping[fleet.star];
                x = star.x + 20;
                y = star.y - 14;
            }
            var fleetElement = void 0;
            this._starmapElement.append(fleetElement = $('<div>').addClass('fleet').css('position', 'absolute')
                .css('transform', 'scale(0.1)').css('cursor', 'pointer')
                .css('padding', '2px').css('left', x + 'px').css('top', y + 'px').css('z-index', '500')
                .data('fleet', fleet).append($('<div>').addClass('image').html('&nbsp;')
                .css('background-image', 'url("data:image/svg+xml;utf8,' + fleetSvg.replace(/\${fill-color}/g, color) + '")')
                .css('background-position', 'center').css('background-repeat', 'no-repeat')
                .css('width', '140px').css('height', '60px')
                .css('padding', '15px')
                .click(function (event) {
                var fleetElement = $(event.target);
                var fleet = fleetElement.data('fleet');
                if (!fleet) {
                    fleetElement = fleetElement.parent();
                    fleet = fleetElement.data('fleet');
                }
                _this._starmapElement.find('.star').find('.image').css('background-color', '');
                _this._starmapElement.find('.fleet').css('background-color', '');
                fleetElement.css('background-color', 'orange');
                _this._selected = fleet;
                $(_this).triggerHandler('selectFleet', fleet);
            })));
            // move center of whole DOM element to the original x and y coordinates
            fleetElement.css('left', (x - fleetElement.width() / 2) + 'px')
                .css('top', (y - fleetElement.height() / 2) + 'px');
            if (this._selected != null && this._selected['id'] == fleet.id) {
                fleetElement.css('background-color', 'orange');
                $(this).triggerHandler('selectFleet', fleet);
            }
        }
    };
    return Starmap;
}());
var Inspector = (function () {
    function Inspector(inspectorElement) {
        this._inspectorElement = inspectorElement;
    }
    Inspector.prototype.clear = function () {
        this._inspectorElement.empty();
    };
    Inspector.prototype.showStar = function (star) {
        this._inspectorElement.empty().append($('<div>').text('Star: ' + star.name));
    };
    Inspector.prototype.showFleet = function (fleet) {
        this._inspectorElement.empty().append($('<div>').text('Fleet id: ' + fleet.id));
    };
    Inspector.prototype.showDispatchFleetConfirmation = function (fleet, star) {
        var _this = this;
        this._inspectorElement.empty().append($('<div>').text('Dispatch fleet confirmation for: ' + fleet.id + ' to ' + star.name));
        this._inspectorElement.append($('<input>').attr('type', 'button').attr('value', 'Yes')
            .kendoButton({ click: function () { $(_this).triggerHandler('dispatchFleet', [fleet, star]); _this.showStar(star); } }));
        this._inspectorElement.append($('<input>').attr('type', 'button').attr('value', 'No')
            .kendoButton({ click: function () { _this.showStar(star); } }));
    };
    return Inspector;
}());
var TurnManager = (function () {
    function TurnManager(turnManagerElement) {
        this._turnManagerElement = turnManagerElement;
    }
    TurnManager.prototype.enableTurnButton = function () {
        if (this._turnButton) {
            this._turnButton.enable(true);
        }
    };
    TurnManager.prototype.newTurn = function (turn, player, color) {
        var _this = this;
        if (!this._turnManagerElement.children().length) {
            var rowsElement = $('<table>').append($('<tr>').append($('<td>').append($('<span>').css('background-color', color).css('width', '16px')
                .css('height', '16px').css('display', 'inline-block').html('&nbsp;')), $('<td>').text(player.name), $('<td>').text(player.nation)), $('<tr>').append($('<td>').html('&nbsp;'), $('<td>').text('Turn'), $('<td>').append(this._turnLabelElement = $('<span>'))));
            this._turnManagerElement.append(rowsElement);
            var turnButtonElement = void 0;
            this._turnManagerElement.append(turnButtonElement = $('<input>').attr('type', 'button').attr('value', 'Turn')
                .kendoButton({ click: function () { _this._turnButton.enable(false); $(_this).triggerHandler('finishTurn'); } }));
            this._turnButton = turnButtonElement.data('kendoButton');
        }
        this._turnLabelElement.text(turn);
    };
    return TurnManager;
}());
var Log = (function () {
    function Log(logElement) {
        this._logElement = logElement;
    }
    Log.prototype.log = function (message) {
        var timestamp = moment().format('D.M.YYYY HH:mm:ss');
        this._logElement.prepend($('<div>').text(timestamp + ' ' + message));
    };
    return Log;
}());
var CommandList = (function () {
    function CommandList(commandListElement) {
        this._commands = [];
        this._commandListElement = commandListElement;
    }
    CommandList.prototype.getAndRemoveCommands = function () {
        var message = CommandBuilder.bundleCommands(this._commands);
        this._commands = [];
        this._commandListElement.empty();
        return message;
    };
    CommandList.prototype.addDispatchFleetCommand = function (fleet, star) {
        this._commandListElement.prepend($('<div>').text('Dispatch fleet with id = ' + fleet.id + ' to star "' + star.name + '".'));
        this._commands.push(CommandBuilder.createFleetDispatchCommand(fleet.id, star.name));
    };
    return CommandList;
}());
var JoinUserInterface = (function () {
    function JoinUserInterface(windowElement, loginFormElement, usernameElement) {
        this._windowElement = windowElement;
        this._loginFormElement = loginFormElement;
        this._usernameElement = usernameElement;
    }
    JoinUserInterface.prototype.show = function () {
        this._windowElement.kendoWindow({
            title: 'Join Rising Empire',
            width: '640px', height: '480px',
            actions: [], draggable: false, resizable: false
        }).data("kendoWindow").center();
        kendo.ui.progress(this._windowElement, true);
    };
    JoinUserInterface.prototype.setPlayerEntries = function (playerEntries) {
        var _this = this;
        var entriesElement = $('<table>').addClass('k-widget').append($('<tr>').addClass('k-header').append($('<td>').css('padding-right', '20px').text('Color'), $('<td>').css('padding-right', '20px').text('Leader Name'), $('<td>').css('padding-right', '20px').text('Nation'), $('<td>').css('padding-right', '20px').text('Action')));
        playerEntries.forEach(function (playerEntry, index) {
            var buttonText = playerEntry.state !== 'DETACHED' ? "Join" : "Re-Join";
            var buttonDisabled = playerEntry.state === 'ACTIVE' || playerEntry.state === 'AI';
            var buttonCellElement;
            $('<tr>').append($('<td>').addClass('k-item').append($('<span>').css('background-color', playerEntry.color).css('width', '16px').css('height', '16px').css('display', 'inline-block').html('&nbsp;')), $('<td>').append($('<span>').text(playerEntry.leaderName + (playerEntry.state === 'AI' ? ' (AI)' : ''))), $('<td>').append($('<span>').text(playerEntry.nation)), buttonCellElement = $('<td>').append($('<input>').attr('type', 'button').attr('value', buttonText)
                .kendoButton({ enable: !buttonDisabled, click: function () {
                    _this._usernameElement.val(playerEntry.username);
                    _this._loginFormElement.submit();
                }
            }))).appendTo(entriesElement);
            if (playerEntry.state === 'NON_PARTICIPATING') {
                buttonCellElement.append($('<input>').attr('type', 'button').attr('value', 'AI')
                    .kendoButton({ click: function () {
                        kendo.ui.progress(_this._windowElement, true);
                        $.ajax({
                            type: 'GET',
                            url: 'csrf',
                            dataType: 'json',
                            success: function () { },
                            data: {},
                        }).done(function (csrfToken) {
                            var headers = { Accept: 'application/json', 'Content-Type': 'application/json' };
                            headers[csrfToken.headerName] = csrfToken.token;
                            $.ajax({
                                type: 'post', url: 'addAi', headers: headers,
                                data: JSON.stringify({ name: playerEntry.leaderName, nation: playerEntry.nation })
                            }).done(function () {
                                location.reload();
                            });
                        });
                    }
                }));
            }
        });
        this._windowElement.data("kendoWindow").content(entriesElement);
    };
    return JoinUserInterface;
}());
