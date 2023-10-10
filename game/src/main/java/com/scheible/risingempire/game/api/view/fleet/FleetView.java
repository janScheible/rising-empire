package com.scheible.risingempire.game.api.view.fleet;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import static com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType.DEPLOYED;
import static com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType.ORBITING;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class FleetView {

	public enum FleetViewType {

		ORBITING, DEPLOYED;
	}

	public enum HorizontalDirection {

		LEFT, RIGHT;
	}

	private final FleetId id;

	private final FleetViewType type;
	private final Player player;
	private final Race race;
	private final Map<ShipTypeView, Integer> ships;
	private final Location location;
	private final boolean deployable;
	@Nullable
	private final Integer scannerRange;

	@Nullable
	private final SystemId source;
	@Nullable
	private final SystemId destination;
	@Nullable
	private final Integer speed;
	@Nullable
	private final SystemId closest;
	@Nullable
	private final HorizontalDirection horizontalDirection;

	@Nullable
	private final SystemId orbiting;

	final Set<FleetBeforeArrival> fleetsBeforeArrival;

	@Nullable
	private final Boolean justLeaving;

	private FleetView(final FleetId id, final FleetViewType type, final Player player, final Race race,
			final Map<ShipTypeView, Integer> ships, final Location location, final boolean deployable,
			@Nullable final Integer scannerRange, @Nullable final SystemId source, @Nullable final SystemId destination,
			@Nullable final Integer speed, @Nullable final SystemId closest,
			@Nullable final HorizontalDirection horizontalDirection, @Nullable final SystemId orbiting,
			@Nullable final Set<FleetBeforeArrival> fleetsBeforeArrival, @Nullable final Boolean justLeaving) {
		this.id = id;

		this.type = type;
		this.player = player;
		this.race = race;
		this.ships = unmodifiableMap(ships);
		this.location = location;
		this.deployable = deployable;
		this.scannerRange = scannerRange;

		this.source = source;
		this.destination = destination;
		this.speed = speed;
		this.closest = closest;
		this.horizontalDirection = horizontalDirection;

		this.orbiting = orbiting;
		this.fleetsBeforeArrival = fleetsBeforeArrival != null ? unmodifiableSet(fleetsBeforeArrival) : emptySet();
		this.justLeaving = justLeaving;
	}

	public static FleetView createDeployed(final FleetId id, final Player player, final Race race,
			final Map<ShipTypeView, Integer> ships, final SystemId source, final SystemId destination,
			final Location location, final int speed, @Nullable final SystemId closest,
			final HorizontalDirection orientation, final boolean deployable, @Nullable final Integer scannerRange,
			@Nullable final Set<FleetBeforeArrival> fleetsBeforeArrival, final boolean justLeaving) {
		return new FleetView(id, DEPLOYED, player, race, ships, location, deployable, scannerRange, source, destination,
				speed, closest, orientation, null, fleetsBeforeArrival, justLeaving);
	}

	public static FleetView createOrbiting(final FleetId id, final Player player, final Race race,
			final Map<ShipTypeView, Integer> ships, final SystemId orbiting, final Location location,
			@Nullable final Set<FleetBeforeArrival> fleetsBeforeArrival, final boolean deployable,
			@Nullable final Integer scannerRange) {
		return new FleetView(id, ORBITING, player, race, ships, location, deployable, scannerRange, null, null, null,
				null, null, orbiting, fleetsBeforeArrival, null);
	}

	public FleetId getId() {
		return id;
	}

	public FleetViewType getType() {
		return type;
	}

	public Player getPlayer() {
		return player;
	}

	public Race getRace() {
		return race;
	}

	public Map<ShipTypeView, Integer> getShips() {
		return ships;
	}

	public Location getLocation() {
		return location;
	}

	public Optional<HorizontalDirection> getHorizontalDirection() {
		return Optional.ofNullable(horizontalDirection);
	}

	public ShipTypeView getShipType(final String name) {
		return ships.entrySet().stream().map(Entry::getKey).filter(st -> st.getName().equals(name)).findFirst().get();
	}

	public Optional<SystemId> getSource() {
		return Optional.ofNullable(source);
	}

	public Optional<SystemId> getDestination() {
		return Optional.ofNullable(destination);
	}

	public Optional<Integer> getSpeed() {
		return Optional.ofNullable(speed);
	}

	public Optional<SystemId> getClosest() {
		return Optional.ofNullable(closest);
	}

	public Optional<SystemId> getOrbiting() {
		return Optional.ofNullable(orbiting);
	}

	public boolean isDeployable() {
		return deployable;
	}

	public Optional<Integer> getScannerRange() {
		return Optional.ofNullable(scannerRange);
	}

	public Optional<Boolean> isJustLeaving() {
		return Optional.ofNullable(justLeaving);
	}

	public boolean didJustArrive() {
		return !fleetsBeforeArrival.isEmpty();
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP")
	public Set<FleetBeforeArrival> getFleetIdsBeforeArrive() {
		return fleetsBeforeArrival;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && getClass().equals(obj.getClass())) {
			final FleetView other = (FleetView) obj;
			return Objects.equals(id, other.id);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return toString(SystemId::toString);
	}

	public String toString(final Function<SystemId, String> starNameResolver) {
		final StringJoiner values = new StringJoiner(", ",
				(type == DEPLOYED ? "DeployedFleet" : "OrbitingFleet") + "View[", "]").add("id=" + id)
						.add("type=" + type).add("player=" + player).add("race=" + race).add("location=" + location)
						.add("horizontalDirection=" + horizontalDirection).add("deployable=" + deployable);
		if (scannerRange != null) {
			values.add("scannerRange=" + scannerRange);
		}

		if (type == DEPLOYED) {
			values.add("source=" + starNameResolver.apply(source));
			values.add("destination=" + starNameResolver.apply(destination));
			values.add("speed=" + speed);
		} else if (type == ORBITING) {
			values.add("orbiting=" + starNameResolver.apply(orbiting));
		}

		if (!fleetsBeforeArrival.isEmpty()) {
			values.add("fleetsBeforeArrival=" + fleetsBeforeArrival);
		}

		if (justLeaving != null) {
			values.add("justLeaving=" + justLeaving);
		}

		values.add("ships=" + getShips().entrySet().stream()
				.map(typeAndAmmount -> new SimpleImmutableEntry<>(typeAndAmmount.getKey().getName(),
						typeAndAmmount.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

		return values.toString();
	}
}
