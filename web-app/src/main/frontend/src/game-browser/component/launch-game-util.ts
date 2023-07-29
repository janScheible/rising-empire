export default class LaunchGameUtil {
	static launchUrlTemplate(urlTemplate: string, gameId: string, player: string, newTab: boolean) {
		const url = urlTemplate
			.replace('{gameId}', encodeURIComponent(gameId))
			.replace('{player}', encodeURIComponent(player));
		LaunchGameUtil.launchUrl(url, newTab);
	}

	static launchUrl(url: string, newTab: boolean) {
		if (newTab) {
			window.open(url, '_blank').focus();
		} else {
			location.pathname = url;
		}
	}
}
