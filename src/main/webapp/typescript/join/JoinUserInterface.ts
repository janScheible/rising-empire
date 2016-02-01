class JoinUserInterface {
	private _windowElement: JQuery;
	private _loginFormElement : JQuery;
	private _usernameElement : JQuery;
	
	constructor(windowElement : JQuery, loginFormElement : JQuery, usernameElement : JQuery) {
		this._windowElement = windowElement;
		this._loginFormElement = loginFormElement;
		this._usernameElement = usernameElement;
	}
	
	show() {
		this._windowElement.kendoWindow({
			title: 'Join Rising Empire',
			width: '640px', height: '480px',
			actions: [], draggable: false, resizable: false
		}).data("kendoWindow").center();
		
		kendo.ui.progress(this._windowElement, true);
	}
	
	setPlayerEntries(playerEntries: Server.Message.PlayerEntry[]) {
		let entriesElement = $('<table>').addClass('k-widget').append(
			$('<tr>').addClass('k-header').append(
				$('<td>').css('padding-right', '20px').text('Color'),
				$('<td>').css('padding-right', '20px').text('Leader Name'),
				$('<td>').css('padding-right', '20px').text('Nation'),
				$('<td>').css('padding-right', '20px').text('Action')
			)
		);
		
		playerEntries.forEach((playerEntry, index ) => {
			let buttonText = playerEntry.state !== 'DETACHED' ? "Join" : "Re-Join";
			let buttonDisabled = playerEntry.state === 'ACTIVE' || playerEntry.state === 'AI';

			let buttonCellElement : JQuery;
			$('<tr>').append(
				$('<td>').addClass('k-item').append($('<span>').css('background-color', playerEntry.color).css('width', '16px').css('height', '16px').css('display', 'inline-block').html('&nbsp;')),
				$('<td>').append($('<span>').text(playerEntry.leaderName + (playerEntry.state === 'AI' ? ' (AI)' : ''))),
				$('<td>').append($('<span>').text(playerEntry.nation)),
				buttonCellElement = $('<td>').append($('<input>').attr('type', 'button').attr('value', buttonText)
					.kendoButton({ enable: !buttonDisabled, click:
						() => {
							this._usernameElement.val(playerEntry.username);
							this._loginFormElement.submit();
						}					 
					})
				)
			).appendTo(entriesElement);
			
			if(playerEntry.state === 'NON_PARTICIPATING') {
				buttonCellElement.append($('<input>').attr('type', 'button').attr('value', 'AI')
					.kendoButton({ click:
						() => {
							kendo.ui.progress(this._windowElement, true);

							$.ajax({
								type: 'GET',
								url: 'csrf',
								dataType: 'json',
								success: function() { },
								data: {},
							}).done((csrfToken: Server.Message.CsrfToken) => {
								let headers = { Accept: 'application/json', 'Content-Type': 'application/json' };
								headers[csrfToken.headerName] = csrfToken.token;
								$.ajax({
									type: 'post', url: 'addAi', headers: headers, 
									data: JSON.stringify({ name: playerEntry.leaderName, nation: playerEntry.nation })									
								}).done(() => {
									location.reload();
								});
							});
						}					 
					})
				);
			}
		});
		
		this._windowElement.data("kendoWindow").content(entriesElement);
	}
}