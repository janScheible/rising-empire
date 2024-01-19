import SubmitInterceptor from '~/util/submit-interceptor';

export default class PartialUpdater extends SubmitInterceptor {
	#pageCache = {};

	constructor() {
		super();
	}

	override preHandle(action, values): Promise<any> | undefined {
		if (values['partial'] !== false) {
			// values['partial'] = true;
		}

		return undefined;
	}

	override postHandle(data): any {
		if (!data.parts) {
			this.#pageCache[data['@type']] = data;
		} else {
			const cachedData = this.#pageCache[data['@type']];
			for (let path of data.parts) {
				PartialUpdater.#patch(data, cachedData, path);
			}

			return cachedData;
		}
	}

	static #patch(origin, target, jsonPath: string) {
		if (jsonPath.startsWith('$.')) {
			jsonPath = jsonPath.substring(2);
		}

		// also support for `$.phoneNumbers[?(@.number=='0123-4567-8888')]` is needed to patch stars and fleets with certain ids

		if (jsonPath.includes('.')) {
			const propertyName = jsonPath.substring(0, jsonPath.indexOf('.'));
			const remainingPath = jsonPath.substring(jsonPath.indexOf('.') + 1);

			PartialUpdater.#patch(origin[propertyName], target[propertyName], remainingPath);
		} else {
			const propertyName = jsonPath;
			target[propertyName] = origin[propertyName];
		}
	}
}
