export default class LaunchGameUtil {
	static launch(gameId: string, player: string, newTab: boolean) {
		const url = `/${encodeURIComponent(gameId)}/${encodeURIComponent(player)}`;
		if (newTab) {
			window.open(url, '_blank').focus();
		} else {
			location.pathname = url;
		}
	}
}
