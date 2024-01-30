import Action from '~/util/action';
import HypermediaUtil from '~/util/hypermedia-util';
import SubmitInterceptor from '~/util/submit-interceptor';

export default class PartialUpdater extends SubmitInterceptor {
	#pageCache = {};

	constructor() {
		super();
	}

	override preHandle(action, values): Promise<any> | undefined {
		if (values['partial'] !== false) {
			values['partial'] = true;
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

			//
			// "tweak" the star select actions depending on $.stateDescription.stateName (they aren't
			// updated because that would require all stars to be tranfered every time). This has to be
			// kept in-sync with the server-side implementation.
			//

			const stateName = cachedData?.stateDescription?.stateName;

			const createOrUpdateStarSelectAction = (star, fields) =>
				HypermediaUtil.createOrUpdateAction(star, {
					name: 'select',
					method: 'GET',
					href: HypermediaUtil.getAction(cachedData, '_self').href,
					fields: fields,
				} as Action);

			if (stateName === 'FleetInspectionState' || stateName === 'FleetDeploymentState') {
				cachedData.starMap.stars.map((star) => {
					const selectable =
						!cachedData.starMap.fleetSelection.deployable ||
						star.id !== cachedData.starMap.fleetSelection.orbitingStarId;

					if (!selectable) {
						HypermediaUtil.removeAction(star, 'select');
					} else {
						createOrUpdateStarSelectAction(
							star,
							[
								{ name: 'selectedStarId', value: star.id },
								{ name: 'selectedFleetId', value: cachedData.starMap.fleetSelection.id },
							].filter(
								(f) =>
									f.name !== 'selectedFleetId' ||
									(f.name === 'selectedFleetId' && cachedData.starMap.fleetSelection.deployable)
							)
						);
					}
				});
			} else if (stateName === 'StarInspectionState') {
				cachedData.starMap.stars.map((star) =>
					createOrUpdateStarSelectAction(star, [{ name: 'selectedStarId', value: star.id }])
				);
			} else if (stateName === 'RelocateShipsState' || stateName === 'TransferColonistsState') {
				const secondStarFieldName = stateName === 'RelocateShipsState' ? 'relocateStarId' : 'transferStarId';
				cachedData.starMap.stars.map((star) => {
					const selectable = star.playerColor === cachedData.playerColor;

					if (!selectable) {
						HypermediaUtil.removeAction(star, 'select');
					} else {
						createOrUpdateStarSelectAction(star, [
							{ name: 'selectedStarId', value: cachedData.starMap.starSelection.id },
							{ name: secondStarFieldName, value: star.id },
						]);
					}
				});
			} else if (stateName === 'StarSpotlightState') {
				cachedData.starMap.stars.map((star) => HypermediaUtil.removeAction(star, 'select'));
			}

			return cachedData;
		}
	}

	static #patch(source, target, jsonPath: string) {
		if (jsonPath.startsWith('$.')) {
			jsonPath = jsonPath.substring(2);
		}

		// support for something like `$.starMap.stars[?(@.id=='s43x66')]`
		if (
			jsonPath.includes('?(@.') &&
			jsonPath.includes('==') &&
			jsonPath.replaceAll('.', '').length === jsonPath.length - 1
		) {
			const arrayPropertyName = jsonPath.substring(0, jsonPath.indexOf('['));
			const comparisonProperty = jsonPath.substring(jsonPath.indexOf('?(@.') + 4, jsonPath.indexOf("=='"));
			const comparisonValue = jsonPath.substring(jsonPath.indexOf("=='") + 3, jsonPath.indexOf("')]"));

			const sourceArray = source[arrayPropertyName];
			const sourceItem = sourceArray.find((item) => item[comparisonProperty] === comparisonValue);

			const targetArray = target[arrayPropertyName];
			let updated = false;
			for (let i = 0; i < targetArray.length; i++) {
				if (targetArray[i][comparisonProperty] === comparisonValue) {
					targetArray[i] = sourceItem;
					updated = true;
					break;
				}
			}
			if (!updated) {
				targetArray.push(sourceItem);
			}
		} else if (jsonPath.includes('.')) {
			const propertyName = jsonPath.substring(0, jsonPath.indexOf('.'));
			const remainingPath = jsonPath.substring(jsonPath.indexOf('.') + 1);

			PartialUpdater.#patch(source[propertyName], target[propertyName], remainingPath);
		} else {
			const propertyName = jsonPath;
			target[propertyName] = source[propertyName];
		}
	}
}
