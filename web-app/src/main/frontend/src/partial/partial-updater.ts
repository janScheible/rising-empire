import FieldUtils from '~/partial/field-utils';
import HypermediaUtil from '~/util/hypermedia-util';

export default class PartialUpdater {
	#viewportFn;
	#renderFn;

	#mainPageData;

	constructor(viewportFn, renderFn) {
		this.#viewportFn = viewportFn;
		this.#renderFn = renderFn;
	}

	beforeRender(data) {
		if (data['@type'] !== 'MainPageDto') {
			if (data['@type'] === 'TechPageDto') {
				const closeAction = HypermediaUtil.getAction(data, 'close');
				closeAction.fields.push({ name: 'fields', value: '' });
			}

			return data;
		}

		if (!this.#mainPageData && data.fields) {
			throw new Error(`First rendered data can't be partial!`);
		}

		if (!data.fields) {
			this.#mainPageData = data;
		} else {
			this.#mainPageData._actions = data._actions;

			if (FieldUtils.shouldBeRendered(data.fields, '$.buttonBar')) {
				this.#mainPageData.buttonBar = data.buttonBar;
			}

			if (FieldUtils.shouldBeRendered(data.fields, '$.inspector')) {
				this.#mainPageData.inspector = data.inspector;
			}

			if (FieldUtils.shouldBeRendered(data.fields, '$.starMap')) {
				const stars = this.#mainPageData.starMap.stars;
				const fleetRanges = this.#mainPageData.starMap.ranges.fleetRanges;
				const colonyScannerRanges = this.#mainPageData.starMap.ranges.colonyScannerRanges;

				const fleets = this.#mainPageData.starMap.fleets;
				const fleetScannerRanges = this.#mainPageData.starMap.ranges.fleetScannerRanges;

				this.#mainPageData.starMap = data.starMap;

				if (FieldUtils.shouldBeRendered(data.fields, '$.starMap.stars')) {
					const starMapViewport = FieldUtils.getStarMapViewport(data.fields, 'stars');
					this.#mainPageData.starMap.stars = stars
						.filter((s) => !starMapViewport.contains(s))
						.concat(data.starMap.stars);

					this.#mainPageData.starMap.ranges.fleetRanges = fleetRanges
						.filter((fr) => !starMapViewport.intersects(fr.centerX, fr.centerY, fr.radius))
						.concat(data.starMap.ranges.fleetRanges);

					this.#mainPageData.starMap.ranges.colonyScannerRanges = colonyScannerRanges
						.filter((csr) => !starMapViewport.intersects(csr.centerX, csr.centerY, csr.radius))
						.concat(data.starMap.ranges.colonyScannerRanges);
				} else {
					this.#mainPageData.starMap.stars = stars;
					this.#mainPageData.starMap.ranges.fleetRanges = fleetRanges;
					this.#mainPageData.starMap.ranges.colonyScannerRanges = colonyScannerRanges;
				}

				if (FieldUtils.shouldBeRendered(data.fields, '$.starMap.fleets')) {
					const starMapViewport = FieldUtils.getStarMapViewport(data.fields, 'fleets');
					this.#mainPageData.starMap.fleets = fleets
						.filter((f) => !starMapViewport.contains(f))
						.concat(data.starMap.fleets);

					this.#mainPageData.starMap.ranges.fleetScannerRanges = fleetScannerRanges
						.filter((dsr) => !starMapViewport.intersects(dsr.centerX, dsr.centerY, dsr.radius))
						.concat(data.starMap.ranges.fleetScannerRanges);
				} else {
					this.#mainPageData.starMap.fleets = fleets;
					this.#mainPageData.starMap.ranges.fleetScannerRanges = fleetScannerRanges;
				}
			}
		}

		if (!HypermediaUtil.getAction(this.#mainPageData.starMap, 'start-scroll')) {
			this.#mainPageData.starMap._actions.push({ name: 'start-scroll', origin: '$.starMap', synthetic: true });
		}
		if (!HypermediaUtil.getAction(this.#mainPageData.starMap, 'end-scroll')) {
			this.#mainPageData.starMap._actions.push({ name: 'end-scroll', origin: '$.starMap', synthetic: true });
		}

		const mainPageSelfAction = HypermediaUtil.getAction(this.#mainPageData, '_self');
		let starMapReloadAction = HypermediaUtil.getAction(this.#mainPageData.starMap, 'reload');
		if (!starMapReloadAction) {
			this.#mainPageData.starMap._actions.push(
				(starMapReloadAction = {
					...structuredClone(mainPageSelfAction),
					name: 'reload',
					origin: '$.starMap',
					synthetic: true,
				})
			);
		} else {
			starMapReloadAction.fields = structuredClone(mainPageSelfAction.fields);
		}
		starMapReloadAction.fields = starMapReloadAction.fields.filter((f) => f.name !== 'newTurn');

		if (this.#mainPageData.inspector.fleetDeployment) {
			const cancelAction = HypermediaUtil.getAction(this.#mainPageData.inspector.fleetDeployment, 'cancel');
			if (cancelAction) {
				cancelAction.origin = '$.inspector.fleetDeployment';
			}

			const deployAction = HypermediaUtil.getAction(this.#mainPageData.inspector.fleetDeployment, 'deploy');
			if (deployAction) {
				deployAction.origin = '$.inspector.fleetDeployment';
			}

			const reloadAction = HypermediaUtil.getAction(this.#mainPageData.inspector.fleetDeployment, 'assign-ships');
			if (reloadAction) {
				reloadAction.origin = '$.inspector.fleetDeployment';
			}
		}

		if (this.#mainPageData.inspector.systemDetails) {
			if (this.#mainPageData.inspector.systemDetails.allocations) {
				const allocateSpendingAction = HypermediaUtil.getAction(
					this.#mainPageData.inspector.systemDetails.allocations,
					'allocate-spending'
				);
				if (allocateSpendingAction) {
					allocateSpendingAction.origin = '$.inspector.systemDetails';
				}
			}

			if (this.#mainPageData.inspector.systemDetails.buildQueue) {
				const nextShipTypeAction = HypermediaUtil.getAction(
					this.#mainPageData.inspector.systemDetails.buildQueue,
					'next-ship-type'
				);
				if (nextShipTypeAction) {
					nextShipTypeAction.origin = '$.inspector.systemDetails';
				}
			}
		}

		for (const star of this.#mainPageData.starMap.stars) {
			for (const starAction of star._actions) {
				if (starAction.name === 'select') {
					starAction.origin = '$.starMap.stars[' + star.id + ']';
				}
			}
		}
		for (const fleet of this.#mainPageData.starMap.fleets) {
			for (const fleetAction of fleet._actions) {
				if (fleetAction.name === 'select') {
					fleetAction.origin = '$.starMap.fleets[' + fleet.id + ']';
				}
			}
		}

		return this.#mainPageData;
	}

	interceptSubmit(action, values) {
		if (action.origin === '$.starMap' && (action.name === 'start-scroll' || action.name === 'end-scroll')) {
			const blocked = action.name.includes('start-');
			this.#mainPageData.buttonBar.blocked = blocked;
			this.#mainPageData.inspector.blocked = blocked;

			for (const star of this.#mainPageData.starMap.stars) {
				star.blocked = blocked;
			}
			for (const fleet of this.#mainPageData.starMap.fleets) {
				fleet.blocked = blocked;
			}

			this.#renderFn(this.#mainPageData);

			return false;
		}

		const starOrFleetSelect =
			action.name === 'select' &&
			action.origin &&
			(action.origin.startsWith('$.starMap.stars') || action.origin.startsWith('$.starMap.fleets'));
		const fleetDeploymentCancelOrDeploy =
			(action.name === 'cancel' || action.name === 'deploy') && action.origin == '$.inspector.fleetDeployment';
		if (starOrFleetSelect || fleetDeploymentCancelOrDeploy) {
			const starMapViewport = this.#viewportFn();
			values.fields = [
				'$.inspector',
				'$.buttonBar',
				FieldUtils.toStarMapFieldsViewport(starMapViewport, 'stars'),
				FieldUtils.toStarMapFieldsViewport(starMapViewport, 'fleets'),
			].join(',');
		} else if (action.name === 'reload' && action.origin === '$.starMap') {
			const starMapViewport = this.#viewportFn();
			values.fields = [
				FieldUtils.toStarMapFieldsViewport(starMapViewport, 'stars'),
				FieldUtils.toStarMapFieldsViewport(starMapViewport, 'fleets'),
			].join(',');
		} else if (action.name === 'assign-ships' && action.origin === '$.inspector.fleetDeployment') {
			const fleetDeploymentData = this.#mainPageData.inspector.fleetDeployment;

			const shipType = Object.keys(values)[0];
			const newCount = values[shipType];
			const previousCount = action.fields.filter((f) => f.name === shipType)[0].value;

			for (const star of this.#mainPageData.starMap.stars) {
				HypermediaUtil.setField(star, 'select', shipType, newCount);
			}

			// NOTE In case of no ship type added or removed we can cheat and update the fleet deployment and all
			//      star select actions on the client side.
			const shipTypeAddedOrRemoved =
				(previousCount === 0 && newCount !== 0) || (previousCount !== 0 && newCount === 0);
			if (!shipTypeAddedOrRemoved) {
				fleetDeploymentData.ships.filter((s) => s.id === shipType)[0].count = newCount;

				HypermediaUtil.setField(fleetDeploymentData, 'assign-ships', shipType, newCount);
				HypermediaUtil.setField(fleetDeploymentData, 'deploy', shipType, newCount);

				this.#renderFn(this.#mainPageData);
				return false;
			} else {
				values.fields = ['$.inspector', '$.starMap'].join(',');
			}
		} else if (
			(action.name === 'allocate-spending' || action.name === 'next-ship-type') &&
			action.origin === '$.inspector.systemDetails'
		) {
			values.fields = ['$.inspector'].join(',');
		}

		return true;
	}
}
