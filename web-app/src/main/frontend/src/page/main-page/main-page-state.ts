import Action from '~/util/action';
import HypermediaUtil from '~/util/hypermedia-util';
import Logger from '~/util/logger/logger';
import LoggerFactory from '~/util/logger/logger-factory';

export default class MainPageState {
	static #logger: Logger = LoggerFactory.get(`${import.meta.url}`);

	#round = -1;

	#state: string;
	#stateChanged: boolean;

	#miniMap = false;
	#fleetMovements = false;

	#explorations = [];
	#colonizations = [];
	#annexations = [];
	#spaceCombats = [];
	#newShipsAction: Action = undefined;
	#victoryDefeatAction: Action = undefined;
	#selectTechActions: Action[] = [];
	#starNotifications = [];

	next(data) {
		let previousState = this.#state;

		if (data.round !== this.#round) {
			previousState = undefined;
			this.#round = data.round;

			this.#state = 'fleet-movements';

			this.#miniMap = true;
			this.#fleetMovements = true;

			this.#explorations = data.explorations;
			this.#colonizations = data.colonizations.filter((colonization) => !colonization.hasCommand);
			this.#annexations = data.annexations.filter((annexation) => !annexation.hasCommand);
			this.#spaceCombats = data.spaceCombats;
			this.#victoryDefeatAction = HypermediaUtil.getAction(data, 'show-victory-defeat');
			this.#newShipsAction = HypermediaUtil.getAction(data, 'show-new-ships');
			this.#selectTechActions = HypermediaUtil.getActions(data, 'select-tech');
			this.#starNotifications = data.starMap.starNotifications;
		} else {
			this.#fleetMovements = false;

			let stateChanged = false;

			if (!stateChanged && this.#victoryDefeatAction) {
				const victoryDefeatAction = this.#victoryDefeatAction;
				this.#victoryDefeatAction = undefined;

				this.#state = 'victory-defeat';
				stateChanged = true;

				HypermediaUtil.submitAction(victoryDefeatAction);
			}

			if (!stateChanged && this.#newShipsAction) {
				const newShipsAction = this.#newShipsAction;
				this.#newShipsAction = undefined;

				this.#state = 'show-new-ships';
				stateChanged = true;

				HypermediaUtil.submitAction(newShipsAction);
			}

			if (!stateChanged && this.#selectTechActions.length > 0) {
				const selectTechAction = this.#selectTechActions.shift();

				this.#state = `select-tech ${this.#selectTechActions.length}`;
				stateChanged = true;

				HypermediaUtil.submitAction(selectTechAction);
			}

			if (!stateChanged) {
				for (const spotlightState of [
					{ occurences: this.#explorations, name: 'explorations' },
					{ occurences: this.#spaceCombats, name: 'spaceCombats' },
					{ occurences: this.#colonizations, name: 'colonizations' },
					{ occurences: this.#annexations, name: 'annexations' },
				]) {
					if (spotlightState.occurences.length > 0) {
						const occurence = spotlightState.occurences[0];
						if (occurence.spotlighted) {
							spotlightState.occurences.shift();
						}

						this.#state = `${spotlightState.name} ${spotlightState.occurences.length} (spotlighted: ${
							occurence.spotlighted === true
						})`;
						stateChanged = true;

						if (!occurence.spotlighted) {
							occurence.spotlighted = true;
							HypermediaUtil.submitAction(HypermediaUtil.getAction(occurence, 'spotlight'));
						}

						break;
					}
				}
			}

			if (!stateChanged && this.#starNotifications.length > 0) {
				this.#miniMap = false;

				this.#state = 'star-notifications';
				stateChanged = true;
			}

			if (!stateChanged) {
				this.#miniMap = false;

				this.#state = 'turn';
			}
		}

		if (this.#state !== previousState) {
			this.#stateChanged = true;

			MainPageState.#logger.info(`'${this.#state}' in round ${this.#round}`);
		} else {
			this.#stateChanged = false;
		}
	}

	/** For notifications this is the entry point (instead of `next(data)`) because their display is done soley on the client-side. */
	onNotificationsDone(mainPageSelfAction: Action, lastNotificationStarId) {
		this.#starNotifications = [];
		HypermediaUtil.submitAction(mainPageSelfAction, { selectedStarId: lastNotificationStarId });
	}

	get miniMap() {
		return this.#miniMap;
	}

	get fleetMovements() {
		return this.#fleetMovements;
	}

	get destroyedFleets() {
		return this.#spaceCombats.flatMap((sc) => sc.destroyedFleets);
	}

	get starNotifications() {
		return this.#starNotifications;
	}

	isNotificationState() {
		return this.#state === 'star-notifications';
	}

	isTurnState() {
		return this.#state === 'turn';
	}

	isFleetMovementsState() {
		return this.#state === 'fleet-movements';
	}

	didStateChange() {
		return this.#stateChanged;
	}
}
