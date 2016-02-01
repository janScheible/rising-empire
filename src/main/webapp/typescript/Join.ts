class Join {
	static init() {
		let userInterface = new JoinUserInterface($("#joinWindow"), $("form[name='loginForm']"), $('input[name=username]'));
		userInterface.show();

		$.getJSON('players', {}).done(userInterface.setPlayerEntries.bind(userInterface));
	}
}