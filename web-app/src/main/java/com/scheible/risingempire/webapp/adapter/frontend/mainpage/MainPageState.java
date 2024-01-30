package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableMap;

/**
 * @author sj
 */
abstract class MainPageState {

	private static boolean onlyStar(Optional<SystemId> selectedSystemId, Optional<FleetId> selectedFleetId) {
		return selectedSystemId.isPresent() && selectedFleetId.isEmpty();
	}

	private static boolean onlyFleet(Optional<SystemId> selectedSystemId, Optional<FleetId> selectedFleetId) {
		return selectedSystemId.isEmpty() && selectedFleetId.isPresent();
	}

	private static boolean starAndFleet(Optional<SystemId> selectedSystemId, Optional<FleetId> selectedFleetId) {
		return selectedSystemId.isPresent() && selectedFleetId.isPresent();
	}

	static MainPageState fromParameters(Optional<String> rawSelectedStarId, Optional<String> rawSpotlightedStarId,
			Optional<String> rawTransferStarId, Optional<String> rawRelocateStarId, Optional<String> rawSelectedFleetId,
			Map<String, String> rawShipTypeIdsAndCounts, ShipProvider shipProvider,
			OrbitingSystemProvider orbitingSystemProvider, DeployableFleetProvider deployableFleetProvider,
			OwnColonyProvider ownColonyProvider) {
		Optional<SystemId> selectedSystemId = rawSelectedStarId.map(id -> new SystemId(id));
		Optional<SystemId> spotlightedSystemId = rawSpotlightedStarId.map(id -> new SystemId(id));
		Optional<SystemId> transferSystemId = rawTransferStarId.map(id -> new SystemId(id));
		Optional<SystemId> relocateSystemId = rawRelocateStarId.map(id -> new SystemId(id));
		Optional<FleetId> selectedFleetId = rawSelectedFleetId.map(id -> new FleetId(id));

		if (onlyStar(selectedSystemId, selectedFleetId) && spotlightedSystemId.isPresent()) {
			return new StarSpotlightState(selectedSystemId.get(), spotlightedSystemId.get());
		}
		else if (onlyStar(selectedSystemId, selectedFleetId) && transferSystemId.isPresent()) {
			return new TransferColonistsState(selectedSystemId.get(), transferSystemId.get(), ownColonyProvider);
		}
		else if (onlyStar(selectedSystemId, selectedFleetId) && relocateSystemId.isPresent()) {
			return new RelocateShipsState(selectedSystemId.get(), relocateSystemId.get(), ownColonyProvider);
		}
		else if (onlyStar(selectedSystemId, selectedFleetId)) {
			return new StarInspectionState(selectedSystemId.get());
		}
		else if (onlyFleet(selectedSystemId, selectedFleetId)) {
			return new FleetInspectionState(selectedFleetId.get(),
					shipProvider.get(selectedFleetId.get(), rawShipTypeIdsAndCounts), deployableFleetProvider,
					orbitingSystemProvider);
		}
		else if (starAndFleet(selectedSystemId, selectedFleetId)) {
			return new FleetDeploymentState(selectedFleetId.get(), selectedSystemId.get(),
					shipProvider.get(selectedFleetId.get(), rawShipTypeIdsAndCounts), orbitingSystemProvider);
		}
		else {
			return new InitState();
		}
	}

	boolean isSystemSelectable(SystemId systemId) {
		return false;
	}

	boolean isFleetSelectable(FleetId fleetId) {
		return false;
	}

	Optional<SystemId> getSelectedSystemId() {
		return Optional.empty();
	}

	Optional<FleetId> getSelectedFleetId() {
		return Optional.empty();
	}

	boolean isInitState() {
		return this instanceof InitState;
	}

	boolean isStarInspectionState() {
		return this instanceof StarInspectionState;
	}

	boolean isStarSpotlightState() {
		return this instanceof StarSpotlightState;
	}

	boolean isFleetInspectionState() {
		return this instanceof FleetInspectionState;
	}

	boolean isFleetDeploymentState() {
		return this instanceof FleetDeploymentState;
	}

	boolean isTransferColonistsState() {
		return this instanceof TransferColonistsState;
	}

	boolean isRelocateShipsState() {
		return this instanceof RelocateShipsState;
	}

	StarInspectionState asStarInspectionState() {
		return (StarInspectionState) this;
	}

	StarSpotlightState asStarSpotlightState() {
		return (StarSpotlightState) this;
	}

	TransferColonistsState asTransferColonistsState() {
		return (TransferColonistsState) this;
	}

	RelocateShipsState asRelocateShipsState() {
		return (RelocateShipsState) this;
	}

	FleetDeploymentState asFleetDeploymentState() {
		return (FleetDeploymentState) this;
	}

	FleetInspectionState asFleetInspectionState() {
		return (FleetInspectionState) this;
	}

	static class InitState extends MainPageState {

	}

	static class StarInspectionState extends MainPageState {

		private final SystemId selectedSystemId;

		private StarInspectionState(SystemId selectedSystemId) {
			this.selectedSystemId = selectedSystemId;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return !this.selectedSystemId.equals(systemId);
		}

		@Override
		boolean isFleetSelectable(FleetId fleetId) {
			return true;
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(this.selectedSystemId);
		}

	}

	static class StarSpotlightState extends MainPageState {

		private final SystemId selectedSystemId;

		private final SystemId spotlightedSystemId;

		StarSpotlightState(SystemId selectedSystemId, SystemId spotlightedSystemId) {
			this.selectedSystemId = selectedSystemId;
			this.spotlightedSystemId = spotlightedSystemId;
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(this.spotlightedSystemId);
		}

		SystemId getActualSelectedSystemId() {
			return this.selectedSystemId;
		}

	}

	static class TransferColonistsState extends MainPageState {

		private final SystemId selectedSystemId;

		private final SystemId transferSystemId;

		private final OwnColonyProvider ownColonyProvider;

		TransferColonistsState(SystemId selectedSystemId, SystemId transferSystemId,
				OwnColonyProvider ownColonyProvider) {
			this.selectedSystemId = selectedSystemId;
			this.transferSystemId = transferSystemId;
			this.ownColonyProvider = ownColonyProvider;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return this.ownColonyProvider.hasOwnColony(systemId);
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(this.selectedSystemId);
		}

		SystemId getTransferSystemId() {
			return this.transferSystemId;
		}

	}

	static class RelocateShipsState extends MainPageState {

		private final SystemId selectedSystemId;

		private final SystemId relocateSystemId;

		private final OwnColonyProvider ownColonyProvider;

		RelocateShipsState(SystemId selectedSystemId, SystemId relocateSystemId, OwnColonyProvider ownColonyProvider) {
			this.selectedSystemId = selectedSystemId;
			this.relocateSystemId = relocateSystemId;
			this.ownColonyProvider = ownColonyProvider;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return this.ownColonyProvider.hasOwnColony(systemId);
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(this.selectedSystemId);
		}

		SystemId getRelocateSystemId() {
			return this.relocateSystemId;
		}

	}

	static class FleetInspectionState extends MainPageState {

		private final FleetId selectedFleetId;

		private final Map<ShipTypeView, Integer> ships;

		private final DeployableFleetProvider deployableFleetProvider;

		private final OrbitingSystemProvider orbitingSystemProvider;

		private FleetInspectionState(FleetId selectedFleetId, Map<ShipTypeView, Integer> ships,
				DeployableFleetProvider deployableFleetProvider, OrbitingSystemProvider orbitingSystemProvider) {
			this.selectedFleetId = selectedFleetId;
			this.ships = ships.isEmpty() ? null : unmodifiableMap(ships);

			this.deployableFleetProvider = deployableFleetProvider;
			this.orbitingSystemProvider = orbitingSystemProvider;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return !this.deployableFleetProvider.is(this.selectedFleetId)
					|| !this.orbitingSystemProvider.is(this.selectedFleetId, systemId);
		}

		@Override
		boolean isFleetSelectable(FleetId fleetId) {
			return !this.selectedFleetId.equals(fleetId);
		}

		@Override
		Optional<FleetId> getSelectedFleetId() {
			return Optional.of(this.selectedFleetId);
		}

		Optional<Map<ShipTypeView, Integer>> getShips() {
			return Optional.ofNullable(this.ships);
		}

	}

	static class FleetDeploymentState extends MainPageState {

		private final SystemId selectedSystemId;

		private final FleetId selectedFleetId;

		private final Map<ShipTypeView, Integer> ships;

		private final OrbitingSystemProvider orbitingSystemProvider;

		private FleetDeploymentState(FleetId selectedFleetId, SystemId selectedSystemId,
				Map<ShipTypeView, Integer> ships, OrbitingSystemProvider orbitingSystemProvider) {
			this.selectedFleetId = selectedFleetId;
			this.selectedSystemId = selectedSystemId;
			this.ships = ships.isEmpty() ? null : unmodifiableMap(ships);

			this.orbitingSystemProvider = orbitingSystemProvider;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return !this.selectedSystemId.equals(systemId)
					&& !this.orbitingSystemProvider.is(this.selectedFleetId, systemId);
		}

		@Override
		boolean isFleetSelectable(FleetId fleetId) {
			return !this.selectedFleetId.equals(fleetId);
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(this.selectedSystemId);
		}

		@Override
		Optional<FleetId> getSelectedFleetId() {
			return Optional.of(this.selectedFleetId);
		}

		Optional<Map<ShipTypeView, Integer>> getShips() {
			return Optional.ofNullable(this.ships);
		}

	}

	@FunctionalInterface
	interface ShipProvider {

		Map<ShipTypeView, Integer> get(FleetId fleetId, Map<String, String> parameters);

	}

	@FunctionalInterface
	interface OrbitingSystemProvider {

		boolean is(FleetId fleetId, SystemId orbitingSystemId);

	}

	@FunctionalInterface
	interface DeployableFleetProvider {

		boolean is(FleetId fleetId);

	}

	@FunctionalInterface
	interface OwnColonyProvider {

		boolean hasOwnColony(SystemId systemId);

	}

}
