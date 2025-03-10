package com.scheible.risingempire.game.api.view.fleet;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public record FleetView(FleetId id, Optional<FleetId> parentId, FleetViewType type, Player player, Race race,
		ShipsView ships, boolean colonistTransporters, Optional<Location> previousLocation, boolean previousJustLeaving,
		Location location, boolean deployable, Optional<Integer> scannerRange, Optional<SystemId> source,
		Optional<SystemId> destination, Optional<Integer> speed, Optional<SystemId> closest,
		Optional<HorizontalDirection> horizontalDirection, Optional<SystemId> orbiting,
		Set<FleetBeforeArrivalView> fleetsBeforeArrival, boolean justLeaving) {

	public enum FleetViewType {

		ORBITING, DEPLOYED;

	}

	public enum HorizontalDirection {

		LEFT, RIGHT;

	}

	public FleetView {
		fleetsBeforeArrival = unmodifiableSet(fleetsBeforeArrival);
	}

	public static com.scheible.risingempire.game.api.view.fleet.FleetViewDeployedBuilder.IdStage deployedBuilder() {
		return FleetViewDeployedBuilder.builder();
	}

	public static com.scheible.risingempire.game.api.view.fleet.FleetViewOrbitingBuilder.IdStage orbitingBuilder() {
		return FleetViewOrbitingBuilder.builder();
	}

	public static FleetView create(FleetView.Deployed deployed) {
		return new FleetView(deployed.id(), deployed.parentId(), FleetViewType.DEPLOYED, deployed.player(),
				deployed.race(), deployed.ships(), deployed.colonistTransporters(),
				Optional.of(deployed.previousLocation()), deployed.previousJustLeaving(), deployed.location(),
				deployed.deployable(), deployed.scannerRange(), deployed.source(), deployed.destination(),
				Optional.of(deployed.speed()), Optional.of(deployed.closest()), Optional.of(deployed.orientation()),
				Optional.empty(), Set.of(), deployed.justLeaving());
	}

	public static FleetView create(FleetView.Orbiting orbiting) {
		return new FleetView(orbiting.id(), orbiting.parentId(), FleetViewType.ORBITING, orbiting.player(),
				orbiting.race(), orbiting.ships(), false, Optional.empty(), false, orbiting.location(),
				orbiting.deployable(), orbiting.scannerRange(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.of(orbiting.orbiting()), orbiting.fleetsBeforeArrival(),
				false);
	}

	@StagedRecordBuilder
	public record Deployed(FleetId id, Optional<FleetId> parentId, Player player, Race race, ShipsView ships,
			boolean colonistTransporters, Optional<SystemId> source, Optional<SystemId> destination,
			Location previousLocation, boolean previousJustLeaving, Location location, int speed, SystemId closest,
			HorizontalDirection orientation, boolean deployable, Optional<Integer> scannerRange, boolean justLeaving) {

	}

	@StagedRecordBuilder
	public record Orbiting(FleetId id, Optional<FleetId> parentId, Player player, Race race, ShipsView ships,
			SystemId orbiting, Location location, Set<FleetBeforeArrivalView> fleetsBeforeArrival, boolean deployable,
			Optional<Integer> scannerRange) {

	}

}
