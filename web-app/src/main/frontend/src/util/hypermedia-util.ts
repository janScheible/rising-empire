import FetchUtil from '~/util/fetch-util';
import LoggerFactory from '~/util/logger/logger-factory';
import Logger from '~/util/logger/logger';
import Action from '~/util/action';

export default class HypermediaUtil {
	static #logger: Logger = LoggerFactory.get(`${import.meta.url}`);

	static toggleDebugLog() {
		const debug = HypermediaUtil.#logger.getLevel() === 'DEBUG';
		HypermediaUtil.#logger.setLevel(debug ? 'NONE' : 'DEBUG');
		return !debug;
	}

	static #actionResponseCallbackFn = undefined;
	static #submitInterceptors = [];

	static setActionResponseCallbackFn(callbackFn) {
		HypermediaUtil.#actionResponseCallbackFn = callbackFn;
	}

	/**
	 * @param {(action, values) => void} interceptor
	 */
	static addSubmitInterceptor(interceptor) {
		HypermediaUtil.#submitInterceptors.push(interceptor);
	}

	static getElements(collectionModel) {
		return collectionModel.elements;
	}

	static getAction(model, name: string) {
		return model['_actions'].filter((a) => a.name === name)[0];
	}

	static getField(model, actionName, fieldName) {
		const action = model['_actions'].filter((a) => a.name === actionName)[0];
		const field = action?.fields.filter((f) => f.name === fieldName);
		return field && field.length ? field[0] : undefined;
	}

	static setField(model, actionName, fieldName, value) {
		const field = HypermediaUtil.getField(model, actionName, fieldName);
		if (field) {
			field.value = value;
			return true;
		} else {
			return false;
		}
	}

	static async submitAction(action: Action, values?: { [key: string]: any }, callbackFn?) {
		values = values ?? {};
		for (const interceptor of HypermediaUtil.#submitInterceptors) {
			if (interceptor(action, values) === false) {
				return;
			}
		}

		// todo check if all values are allowed (mandatory/optinal concept does not yet exist)
		const valuesAsArrayMap = Object.keys(values).reduce((map, key) => ((map[key] = [values[key]]), map), {});
		const body = action.fields.reduce((map, obj) => {
			if (!map[obj.name]) {
				map[obj.name] = [obj.value];
			} else {
				map[obj.name].push(obj.value);
			}

			return map;
		}, valuesAsArrayMap);

		let href = action.href;

		const query = new URLSearchParams();
		action.fields.forEach((f) => query.append(f.name, f.value));
		Object.entries(values).forEach(([key, value]) => {
			const templateKey = `{${key}}`;
			if (href.includes(templateKey)) {
				href = href.replace(templateKey, encodeURIComponent(value));
			} else {
				query.delete(key);
				if (!Array.isArray(value)) {
					query.append(key, value);
				} else {
					value.forEach((v) => query.append(key, v));
				}
			}
		});

		const encodedQuery = action.method === 'GET' && Array.from(query).length > 0 ? '?' + query.toString() : '';

		if (HypermediaUtil.#logger.isDebugEnabled) {
			HypermediaUtil.#logger.debug(
				`submit ${action.synthetic ? 'synthetic ' : ''}action ${action.name} as ${action.method} to ${
					href + decodeURIComponent(encodedQuery)
				}${action.method !== 'GET' ? ' with body ' + JSON.stringify(body) : ''}`
			);
		}

		const entity = await FetchUtil.jsonFetch(href + encodedQuery, {
			method: action.method,
			headers: action.method !== 'GET' ? { 'Content-Type': action.contentType } : undefined,
			body: action.method !== 'GET' ? JSON.stringify(body) : undefined,
		});

		if (callbackFn) {
			callbackFn(entity);
		} else if (HypermediaUtil.#actionResponseCallbackFn) {
			HypermediaUtil.#actionResponseCallbackFn(entity);
		} else {
			throw new Error('Neither was a callback passed as argument nor a global one was defined!');
		}

		return entity;
	}
}
