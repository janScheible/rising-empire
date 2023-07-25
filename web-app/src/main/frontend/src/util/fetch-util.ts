export default class FetchUtil {
	/**
	 * Fetch with response parsed as JSON and default backend error handler.
	 *
	 * @return The promise of the fetch call with the parsed data.
	 */
	static async jsonFetch(input: RequestInfo | URL, init: RequestInit = {}): Promise<string> {
		try {
			init.headers = init.headers ?? {};
			init.headers['Accept'] = init.headers['Accept'] ?? 'application/json';
			init.cache = 'no-store';

			const response = await fetch(input, init);
			const json = await response.json();

			if (!response.ok) {
				throw new Error(`${json.error} (${response.status}) for ${json.message} in ${json.path}`);
			} else {
				return json;
			}
		} catch (error) {
			console.error(error);
			alert('A backend error occurred, please reload the page.');
		}
	}
}
