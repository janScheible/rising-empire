package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemId;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 *
 * @author sj
 */
abstract class MainPageState {

	static class InitState extends MainPageState {

	}

	private abstract static class OneByOneSystemsState<T> extends MainPageState {

		private final SystemId selectedSystemId;
		private final SystemId actualSelectedSystemId;
		protected final List<T> systemIds;

		private OneByOneSystemsState(final SystemId selectedSystemId, final List<T> systemIds,
				final Function<T, SystemId> systemIdExtractor) {
			this.actualSelectedSystemId = selectedSystemId;
			this.selectedSystemId = systemIdExtractor.apply(systemIds.get(0));
			this.systemIds = systemIds.size() == 1 ? emptyList()
					: unmodifiableList(systemIds.subList(1, systemIds.size()));
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(selectedSystemId);
		}

		SystemId getActualSelectedSystemId() {
			return actualSelectedSystemId;
		}

		@Override
		boolean isMiniMap() {
			return true;
		}

		@Override
		boolean isSystemSelectable(final SystemId systemId) {
			return false;
		}

		@Override
		boolean isFleetSelectable(final FleetId fleetId) {
			return false;
		}
	}

	static class SpaceCombatSystemState extends OneByOneSystemsState<Entry<SystemId, Integer>> {

		private final int order;

		private SpaceCombatSystemState(final SystemId selectedSystemId,
				final List<Entry<SystemId, Integer>> spaceCombatSystemIds) {
			super(selectedSystemId, spaceCombatSystemIds, Entry::getKey);
			this.order = spaceCombatSystemIds.get(0).getValue();
		}

		List<Entry<SystemId, Integer>> getRemainingSpaceCombatSystemIds() {
			return systemIds;
		}

		boolean hasRemainingSpaceCombatSystems() {
			return !systemIds.isEmpty();
		}

		int getOrder() {
			return order;
		}
	}

	static class ExplorationState extends OneByOneSystemsState<SystemId> {
		private ExplorationState(final SystemId selectedSystemId, final List<SystemId> exploredSystemIds) {
			super(selectedSystemId, exploredSystemIds, Function.identity());
		}

		List<SystemId> getRemainingExplorationSystemIds() {
			return systemIds;
		}

		boolean hasRemainingExploredSystems() {
			return !systemIds.isEmpty();
		}
	}

	static class ColonizationState extends OneByOneSystemsState<SystemId> {
		private ColonizationState(final SystemId selectedSystemId, final List<SystemId> colonizableSystemIds) {
			super(selectedSystemId, colonizableSystemIds, Function.identity());
		}

		List<SystemId> getRemainingColonizableSystemIds() {
			return systemIds;
		}

		boolean hasRemainingColonizableSystems() {
			return !systemIds.isEmpty();
		}
	}

	static class NotificationState extends OneByOneSystemsState<SystemId> {
		public NotificationState(final SystemId selectedSystemId, final List<SystemId> systemIds) {
			super(selectedSystemId, systemIds, Function.identity());
		}

		List<SystemId> getRemainingNotificationSystemIds() {
			return systemIds;
		}

		boolean hasRemainingNotificationSystems() {
			return !systemIds.isEmpty();
		}

		@Override
		boolean isMiniMap() {
			return false;
		}
	}

	static class StarInspectionState extends MainPageState {
		private final SystemId selectedSystemId;
		private final Optional<String> lockedCategory;

		private StarInspectionState(final SystemId selectedSystemId, final Optional<String> lockedCategory) {
			this.selectedSystemId = selectedSystemId;
			this.lockedCategory = lockedCategory;
		}

		@Override
		boolean isSystemSelectable(final SystemId systemId) {
			return !selectedSystemId.equals(systemId);
		}

		@Override
		boolean isFleetSelectable(final FleetId fleetId) {
			return true;
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(selectedSystemId);
		}

		@Override
		Optional<String> getLockedCategory() {
			return lockedCategory;
		}
	}

	static class NewTurnState extends StarInspectionState {

		private NewTurnState(final SystemId selectedSystemId) {
			super(selectedSystemId, Optional.empty());
		}
	}

	static class TurnFinishedState extends StarInspectionState {

		private TurnFinishedState(final SystemId selectedSystemId) {
			super(selectedSystemId, Optional.empty());
		}

		@Override
		boolean isMiniMap() {
			return true;
		}

		@Override
		boolean isSystemSelectable(final SystemId systemId) {
			return false;
		}

		@Override
		boolean isFleetSelectable(final FleetId fleetId) {
			return false;
		}
	}

	static class FleetMovementState extends StarInspectionState {

		private FleetMovementState(final SystemId selectedSystemId) {
			super(selectedSystemId, Optional.empty());
		}

		@Override
		boolean isMiniMap() {
			return true;
		}

		@Override
		boolean isSystemSelectable(final SystemId systemId) {
			return false;
		}

		@Override
		boolean isFleetSelectable(final FleetId fleetId) {
			return false;
		}
	}

	static class FleetInspectionState extends MainPageState {

		private final FleetId selectedFleetId;
		private final Map<ShipTypeView, Integer> ships;

		private final DeployableFleetProvider deployableFleetProvider;
		private final OrbitingSystemProvider orbitingSystemProvider;

		private FleetInspectionState(final FleetId selectedFleetId, final Map<ShipTypeView, Integer> ships,
				final DeployableFleetProvider deployableFleetProvider,
				final OrbitingSystemProvider orbitingSystemProvider) {
			this.selectedFleetId = selectedFleetId;
			this.ships = ships.isEmpty() ? null : unmodifiableMap(ships);

			this.deployableFleetProvider = deployableFleetProvider;
			this.orbitingSystemProvider = orbitingSystemProvider;
		}

		@Override
		boolean isSystemSelectable(final SystemId systemId) {
			return !deployableFleetProvider.is(selectedFleetId)
					|| !orbitingSystemProvider.is(selectedFleetId, systemId);
		}

		@Override
		boolean isFleetSelectable(final FleetId fleetId) {
			return !selectedFleetId.equals(fleetId);
		}

		@Override
		Optional<FleetId> getSelectedFleetId() {
			return Optional.of(selectedFleetId);
		}

		@Override
		Optional<Map<ShipTypeView, Integer>> getShips() {
			return Optional.ofNullable(ships);
		}
	}

	static class FleetDeploymentState extends MainPageState {

		private final SystemId selectedSystemId;
		private final FleetId selectedFleetId;
		private final Map<ShipTypeView, Integer> ships;

		private final OrbitingSystemProvider orbitingSystemProvider;

		private FleetDeploymentState(final FleetId selectedFleetId, final SystemId selectedSystemId,
				final Map<ShipTypeView, Integer> ships, final OrbitingSystemProvider orbitingSystemProvider) {
			this.selectedFleetId = selectedFleetId;
			this.selectedSystemId = selectedSystemId;
			this.ships = ships.isEmpty() ? null : unmodifiableMap(ships);

			this.orbitingSystemProvider = orbitingSystemProvider;
		}

		@Override
		boolean isSystemSelectable(final SystemId systemId) {
			return !selectedSystemId.equals(systemId) && !orbitingSystemProvider.is(selectedFleetId, systemId);
		}

		@Override
		boolean isFleetSelectable(final FleetId fleetId) {
			return !selectedFleetId.equals(fleetId);
		}

		@Override
		Optional<SystemId> getSelectedSystemId() {
			return Optional.of(selectedSystemId);
		}

		@Override
		Optional<FleetId> getSelectedFleetId() {
			return Optional.of(selectedFleetId);
		}

		@Override
		Optional<Map<ShipTypeView, Integer>> getShips() {
			return Optional.ofNullable(ships);
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

	private static boolean onlyStar(@Nullable final SystemId selectedSystemId,
			@Nullable final FleetId selectedFleetId) {
		return selectedSystemId != null && selectedFleetId == null;
	}

	private static boolean onlyFleet(@Nullable final SystemId selectedSystemId,
			@Nullable final FleetId selectedFleetId) {
		return selectedSystemId == null && selectedFleetId != null;
	}

	private static boolean starAndFleet(@Nullable final SystemId selectedSystemId,
			@Nullable final FleetId selectedFleetId) {
		return selectedSystemId != null && selectedFleetId != null;
	}

	private static MainPageState createStarInspectionState(final SystemId selectedSystemId,
			final Optional<String> lockedCategory, final Optional<Boolean> finishedTurnInCurrentRound) {
		if (finishedTurnInCurrentRound.isPresent()) {

			if (finishedTurnInCurrentRound.get()) {
				return new TurnFinishedState(selectedSystemId);
			} else {
				return new FleetMovementState(selectedSystemId);
			}
		} else {

			return new StarInspectionState(selectedSystemId, lockedCategory);
		}
	}

	static MainPageState fromParameters(final Optional<String> rawSelectedStarId,
			final Optional<String> rawSelectedFleetId, final Map<String, String> rawShipTypeIdsAndCounts,
			final Optional<List<String>> rawSpaceCombatSystemIds, final Optional<List<String>> rawExploredSystemIds,
			final Optional<List<String>> rawColonizableSystemIds, final Optional<List<String>> rawNotificationSystemIds,
			final Optional<String> lockedCategory, final Optional<Boolean> finishedTurnInCurrentRound,
			final boolean newTurn, final ShipProvider shipProvider, final OrbitingSystemProvider orbitingSystemProvider,
			final DeployableFleetProvider deployableFleetProvider) {
		final SystemId selectedSystemId = rawSelectedStarId.map(id -> new SystemId(id)).orElse(null);
		final FleetId selectedFleetId = rawSelectedFleetId.map(id -> new FleetId(id)).orElse(null);

		if (rawSpaceCombatSystemIds.isPresent()) {
			final List<Entry<SystemId, Integer>> spaceCombatSystemIds = rawSpaceCombatSystemIds
					.map(ids -> ids.stream()
							.map(id -> new SimpleImmutableEntry<>(new SystemId(id.split("@")[0]),
									Integer.valueOf(id.split("@")[1]))))
					.get().sorted((a, b) -> a.getValue().compareTo(b.getValue())).collect(Collectors.toList());
			return new SpaceCombatSystemState(selectedSystemId, spaceCombatSystemIds);
		} else if (rawExploredSystemIds.isPresent()) {
			final List<SystemId> exploredSystemIds = rawExploredSystemIds
					.map(ids -> ids.stream().map(id -> new SystemId(id))).get().collect(Collectors.toList());
			return new ExplorationState(selectedSystemId, exploredSystemIds);
		} else if (rawColonizableSystemIds.isPresent()) {
			final List<SystemId> colonizableSystemIds = rawColonizableSystemIds
					.map(ids -> ids.stream().map(id -> new SystemId(id))).get().collect(Collectors.toList());
			return new ColonizationState(selectedSystemId, colonizableSystemIds);
		} else if (rawNotificationSystemIds.isPresent()) {
			final List<SystemId> notificationSystemIds = rawNotificationSystemIds
					.map(ids -> ids.stream().map(id -> new SystemId(id))).get().collect(Collectors.toList());
			return new NotificationState(selectedSystemId, notificationSystemIds);
		} else if (newTurn && onlyStar(selectedSystemId, selectedFleetId)) {
			return new NewTurnState(selectedSystemId);
		} else if (onlyStar(selectedSystemId, selectedFleetId)) {
			return createStarInspectionState(selectedSystemId, lockedCategory, finishedTurnInCurrentRound);
		} else if (onlyFleet(selectedSystemId, selectedFleetId)) {
			return new FleetInspectionState(selectedFleetId, shipProvider.get(selectedFleetId, rawShipTypeIdsAndCounts),
					deployableFleetProvider, orbitingSystemProvider);
		} else if (starAndFleet(selectedSystemId, selectedFleetId)) {
			return new FleetDeploymentState(selectedFleetId, selectedSystemId,
					shipProvider.get(selectedFleetId, rawShipTypeIdsAndCounts), orbitingSystemProvider);
		} else {
			return new InitState();
		}
	}

	boolean isSystemSelectable(final SystemId systemId) {
		return false;
	}

	boolean isFleetSelectable(final FleetId fleetId) {
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

	boolean isNotificationState() {
		return this instanceof NotificationState;
	}

	ExplorationState asExplorationState() {
		return (ExplorationState) this;
	}

	ColonizationState asColonizationState() {
		return (ColonizationState) this;
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
}
