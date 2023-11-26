package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	private static MainPageState createStarInspectionState(SystemId selectedSystemId, Optional<String> lockedCategory,
			Optional<Boolean> finishedTurnInCurrentRound) {
		if (finishedTurnInCurrentRound.isPresent()) {

			if (finishedTurnInCurrentRound.get()) {
				return new TurnFinishedState(selectedSystemId);
			}
			else {
				return new FleetMovementState(selectedSystemId);
			}
		}
		else {

			return new StarInspectionState(selectedSystemId, lockedCategory);
		}
	}

	static MainPageState fromParameters(Optional<String> rawSelectedStarId, Optional<String> rawSelectedFleetId,
			Map<String, String> rawShipTypeIdsAndCounts, Optional<List<String>> rawSpaceCombatSystemIds,
			Optional<List<String>> rawExploredSystemIds, Optional<List<String>> rawColonizableSystemIds,
			Optional<List<String>> rawAnnexableSystemIds, Optional<List<String>> rawNotificationSystemIds,
			Optional<String> lockedCategory, Optional<Boolean> finishedTurnInCurrentRound, boolean newTurn,
			ShipProvider shipProvider, OrbitingSystemProvider orbitingSystemProvider,
			DeployableFleetProvider deployableFleetProvider) {
		Optional<SystemId> selectedSystemId = rawSelectedStarId.map(id -> new SystemId(id));
		Optional<FleetId> selectedFleetId = rawSelectedFleetId.map(id -> new FleetId(id));

		if (rawSpaceCombatSystemIds.isPresent()) {
			List<Entry<SystemId, Integer>> spaceCombatSystemIds = rawSpaceCombatSystemIds
				.map(ids -> ids.stream()
					.map(id -> Map.entry(new SystemId(id.split("@")[0]), Integer.valueOf(id.split("@")[1]))))
				.get()
				.sorted((a, b) -> a.getValue().compareTo(b.getValue()))
				.collect(Collectors.toList());
			return new SpaceCombatSystemState(selectedSystemId.get(), spaceCombatSystemIds);
		}
		else if (rawExploredSystemIds.isPresent()) {
			List<SystemId> exploredSystemIds = rawExploredSystemIds.map(ids -> ids.stream().map(id -> new SystemId(id)))
				.get()
				.collect(Collectors.toList());
			return new ExplorationState(selectedSystemId.get(), exploredSystemIds);
		}
		else if (rawColonizableSystemIds.isPresent()) {
			List<SystemId> colonizableSystemIds = rawColonizableSystemIds
				.map(ids -> ids.stream().map(id -> new SystemId(id)))
				.get()
				.collect(Collectors.toList());
			return new ColonizationState(selectedSystemId.get(), colonizableSystemIds);
		}
		else if (rawAnnexableSystemIds.isPresent()) {
			List<SystemId> annexableSystemIds = rawAnnexableSystemIds
				.map(ids -> ids.stream().map(id -> new SystemId(id)))
				.get()
				.collect(Collectors.toList());
			return new AnnexationState(selectedSystemId.get(), annexableSystemIds);
		}
		else if (rawNotificationSystemIds.isPresent()) {
			List<SystemId> notificationSystemIds = rawNotificationSystemIds
				.map(ids -> ids.stream().map(id -> new SystemId(id)))
				.get()
				.collect(Collectors.toList());
			return new NotificationState(selectedSystemId.get(), notificationSystemIds);
		}
		else if (newTurn && onlyStar(selectedSystemId, selectedFleetId)) {
			return new NewTurnState(selectedSystemId.get());
		}
		else if (onlyStar(selectedSystemId, selectedFleetId)) {
			return createStarInspectionState(selectedSystemId.get(), lockedCategory, finishedTurnInCurrentRound);
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

	Optional<Map<ShipTypeView, Integer>> getShips() {
		return Optional.empty();
	}

	Optional<String> getLockedCategory() {
		return Optional.empty();
	}

	boolean isMiniMap() {
		return false;
	}

	boolean isInitState() {
		return this instanceof InitState;
	}

	boolean isOneByOneSystemsState() {
		return this instanceof OneByOneSystemsState;
	}

	boolean isSpaceCombatSystemState() {
		return this instanceof SpaceCombatSystemState;
	}

	boolean isExplorationState() {
		return this instanceof ExplorationState;
	}

	boolean isColonizationState() {
		return this instanceof ColonizationState;
	}

	boolean isAnnexationState() {
		return this instanceof AnnexationState;
	}

	boolean isNotificationState() {
		return this instanceof NotificationState;
	}

	ExplorationState asExplorationState() {
		return (ExplorationState) this;
	}

	ColonizationState asColonizationState() {
		return (ColonizationState) this;
	}

	AnnexationState asAnnexationState() {
		return (AnnexationState) this;
	}

	NotificationState asNotificationState() {
		return (NotificationState) this;
	}

	SpaceCombatSystemState asSpaceCombatSystemState() {
		return (SpaceCombatSystemState) this;
	}

	boolean isStarInspectionState() {
		return this instanceof StarInspectionState;
	}

	boolean isFleetInspectionState() {
		return this instanceof FleetInspectionState;
	}

	boolean isFleetDeploymentState() {
		return this instanceof FleetDeploymentState;
	}

	boolean isTurnFinishedState() {
		return this instanceof TurnFinishedState;
	}

	boolean isFleetMovementState() {
		return this instanceof FleetMovementState;
	}

	boolean isNewTurnState() {
		return this instanceof NewTurnState;
	}

	static class InitState extends MainPageState {

	}

	static class SpaceCombatSystemState extends OneByOneSystemsState<Entry<SystemId, Integer>> {

		private final int order;

		private SpaceCombatSystemState(SystemId selectedSystemId, List<Entry<SystemId, Integer>> spaceCombatSystemIds) {
			super(selectedSystemId, spaceCombatSystemIds, Entry::getKey);
			this.order = spaceCombatSystemIds.get(0).getValue();
		}

		List<Entry<SystemId, Integer>> getRemainingSpaceCombatSystemIds() {
			return this.systemIds;
		}

		boolean hasRemainingSpaceCombatSystems() {
			return !this.systemIds.isEmpty();
		}

		int getOrder() {
			return this.order;
		}

	}

	static class ExplorationState extends OneByOneSystemsState<SystemId> {

		private ExplorationState(SystemId selectedSystemId, List<SystemId> exploredSystemIds) {
			super(selectedSystemId, exploredSystemIds, Function.identity());
		}

		List<SystemId> getRemainingExplorationSystemIds() {
			return this.systemIds;
		}

		boolean hasRemainingExploredSystems() {
			return !this.systemIds.isEmpty();
		}

	}

	static class ColonizationState extends OneByOneSystemsState<SystemId> {

		private ColonizationState(SystemId selectedSystemId, List<SystemId> colonizableSystemIds) {
			super(selectedSystemId, colonizableSystemIds, Function.identity());
		}

		List<SystemId> getRemainingColonizableSystemIds() {
			return this.systemIds;
		}

		boolean hasRemainingColonizableSystems() {
			return !this.systemIds.isEmpty();
		}

	}

	static class AnnexationState extends OneByOneSystemsState<SystemId> {

		private AnnexationState(SystemId selectedSystemId, List<SystemId> annexableSystemIds) {
			super(selectedSystemId, annexableSystemIds, Function.identity());
		}

		List<SystemId> getRemainingAnnexableSystemIds() {
			return this.systemIds;
		}

		boolean hasRemainingAnnexableSystemIds() {
			return !this.systemIds.isEmpty();
		}

	}

	static class NotificationState extends OneByOneSystemsState<SystemId> {

		NotificationState(SystemId selectedSystemId, List<SystemId> systemIds) {
			super(selectedSystemId, systemIds, Function.identity());
		}

		List<SystemId> getRemainingNotificationSystemIds() {
			return this.systemIds;
		}

		boolean hasRemainingNotificationSystems() {
			return !this.systemIds.isEmpty();
		}

		@Override
		boolean isMiniMap() {
			return false;
		}

	}

	static class StarInspectionState extends MainPageState {

		private final SystemId selectedSystemId;

		private final Optional<String> lockedCategory;

		private StarInspectionState(SystemId selectedSystemId, Optional<String> lockedCategory) {
			this.selectedSystemId = selectedSystemId;
			this.lockedCategory = lockedCategory;
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

		@Override
		Optional<String> getLockedCategory() {
			return this.lockedCategory;
		}

	}

	static class NewTurnState extends StarInspectionState {

		private NewTurnState(SystemId selectedSystemId) {
			super(selectedSystemId, Optional.empty());
		}

	}

	static class TurnFinishedState extends StarInspectionState {

		private TurnFinishedState(SystemId selectedSystemId) {
			super(selectedSystemId, Optional.empty());
		}

		@Override
		boolean isMiniMap() {
			return true;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return false;
		}

		@Override
		boolean isFleetSelectable(FleetId fleetId) {
			return false;
		}

	}

	static class FleetMovementState extends StarInspectionState {

		private FleetMovementState(SystemId selectedSystemId) {
			super(selectedSystemId, Optional.empty());
		}

		@Override
		boolean isMiniMap() {
			return true;
		}

		@Override
		boolean isSystemSelectable(SystemId systemId) {
			return false;
		}

		@Override
		boolean isFleetSelectable(FleetId fleetId) {
			return false;
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

		@Override
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

		@Override
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

}
