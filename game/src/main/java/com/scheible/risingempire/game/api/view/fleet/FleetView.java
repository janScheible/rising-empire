package com.scheible.risingempire.game.api.view.fleet;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public record FleetView(FleetId id, Optional<FleetId> parentId, FleetViewType type, Player player, Race race,
		ShipsView ships, Optional<Location> previousLocation, Optional<Boolean> previousJustLeaving, Location location,
		boolean deployable, Optional<Integer> scannerRange, Optional<SystemId> source, Optional<SystemId> destination,
		Optional<Integer> speed, Optional<SystemId> closest, Optional<HorizontalDirection> horizontalDirection,
		Optional<SystemId> orbiting, Set<FleetBeforeArrival> fleetsBeforeArrival, Optional<Boolean> justLeaving) {

	public enum FleetViewType {

		ORBITING, DEPLOYED;

	}

	public enum HorizontalDirection {

		LEFT, RIGHT;

	}

	public FleetView {
		fleetsBeforeArrival = unmodifiableSet(fleetsBeforeArrival);
	}

	public static FleetView create(FleetView.Deployed deployed) {
		return new FleetView(deployed.id(), deployed.parentId(), FleetViewType.DEPLOYED, deployed.player(),
				deployed.race(), deployed.ships(), Optional.of(deployed.previousLocation()),
				Optional.of(deployed.previousJustLeaving()), deployed.location(), deployed.deployable(),
				deployed.scannerRange(), deployed.source(), deployed.destination(), Optional.of(deployed.speed()),
				Optional.of(deployed.closest()), Optional.of(deployed.orientation()), Optional.empty(), Set.of(),
				Optional.of(deployed.justLeaving()));
	}

	public static FleetView create(FleetView.Orbiting orbiting) {
		return new FleetView(orbiting.id(), orbiting.parentId(), FleetViewType.ORBITING, orbiting.player(),
				orbiting.race(), orbiting.ships(), Optional.empty(), Optional.empty(), orbiting.location(),
				orbiting.deployable(), orbiting.scannerRange(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.of(orbiting.orbiting()), orbiting.fleetsBeforeArrival(),
				Optional.empty());
	}

	@StagedRecordBuilder
	public record Deployed(FleetId id, Optional<FleetId> parentId, Player player, Race race, ShipsView ships,
			Optional<SystemId> source, Optional<SystemId> destination, Location previousLocation,
			boolean previousJustLeaving, Location location, int speed, SystemId closest,
			HorizontalDirection orientation, boolean deployable, Optional<Integer> scannerRange, boolean justLeaving) {

	}

	@StagedRecordBuilder
	public record Orbiting(FleetId id, Optional<FleetId> parentId, Player player, Race race, ShipsView ships,
			SystemId orbiting, Location location, Set<FleetBeforeArrival> fleetsBeforeArrival, boolean deployable,
			Optional<Integer> scannerRange) {

	}

}
