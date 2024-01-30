package com.scheible.risingempire.game.api.view.fleet;

import java.util.Collections;
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

import static java.util.Collections.unmodifiableMap;

/**
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

	private final Optional<FleetId> parentId;

	private final FleetViewType type;

	private final Player player;

	private final Race race;

	private final Map<ShipTypeView, Integer> ships;

	private final Optional<Location> previousLocation;

	private final Optional<Boolean> previousJustLeaving;

	private final Location location;

	private final boolean deployable;

	private final Optional<Integer> scannerRange;

	private final Optional<SystemId> source;

	private final Optional<SystemId> destination;

	private final Optional<Integer> speed;

	private final Optional<SystemId> closest;

	private final Optional<HorizontalDirection> horizontalDirection;

	private final Optional<SystemId> orbiting;

	private final Set<FleetBeforeArrival> fleetsBeforeArrival;

	private final Optional<Boolean> justLeaving;

	private FleetView(FleetId id, Optional<FleetId> parentId, FleetViewType type, Player player, Race race,
			Map<ShipTypeView, Integer> ships, Optional<Location> previousLocation,
			Optional<Boolean> previousJustLeaving, Location location, boolean deployable,
			Optional<Integer> scannerRange, Optional<SystemId> source, Optional<SystemId> destination,
			Optional<Integer> speed, Optional<SystemId> closest, Optional<HorizontalDirection> horizontalDirection,
			Optional<SystemId> orbiting, Set<FleetBeforeArrival> fleetsBeforeArrival, Optional<Boolean> justLeaving) {
		this.id = id;
		this.parentId = parentId;

		this.type = type;
		this.player = player;
		this.race = race;
		this.ships = unmodifiableMap(ships);
		this.previousLocation = previousLocation;
		this.previousJustLeaving = previousJustLeaving;
		this.location = location;
		this.deployable = deployable;
		this.scannerRange = scannerRange;

		this.source = source;
		this.destination = destination;
		this.speed = speed;
		this.closest = closest;
		this.horizontalDirection = horizontalDirection;

		this.orbiting = orbiting;
		this.fleetsBeforeArrival = Collections.unmodifiableSet(fleetsBeforeArrival);
		this.justLeaving = justLeaving;
	}

	public static FleetView createDeployed(FleetId id, Optional<FleetId> parentId, Player player, Race race,
			Map<ShipTypeView, Integer> ships, Optional<SystemId> source, Optional<SystemId> destination,
			Location previousLocation, boolean previousJustLeaving, Location location, int speed, SystemId closest,
			HorizontalDirection orientation, boolean deployable, Optional<Integer> scannerRange,
			Set<FleetBeforeArrival> fleetsBeforeArrival, boolean justLeaving) {
		return new FleetView(id, parentId, FleetViewType.DEPLOYED, player, race, ships, Optional.of(previousLocation),
				Optional.of(previousJustLeaving), location, deployable, scannerRange, source, destination,
				Optional.of(speed), Optional.of(closest), Optional.of(orientation), Optional.empty(),
				fleetsBeforeArrival, Optional.of(justLeaving));
	}

	public static FleetView createOrbiting(FleetId id, Optional<FleetId> parentId, Player player, Race race,
			Map<ShipTypeView, Integer> ships, SystemId orbiting, Location location,
			Set<FleetBeforeArrival> fleetsBeforeArrival, boolean deployable, Optional<Integer> scannerRange) {
		return new FleetView(id, parentId, FleetViewType.ORBITING, player, race, ships, Optional.empty(),
				Optional.empty(), location, deployable, scannerRange, Optional.empty(), Optional.empty(),
				Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(orbiting), fleetsBeforeArrival,
				Optional.empty());
	}

	public FleetId getId() {
		return this.id;
	}

	public Optional<FleetId> getParentId() {
		return this.parentId;
	}

	public FleetViewType getType() {
		return this.type;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Race getRace() {
		return this.race;
	}

	public Map<ShipTypeView, Integer> getShips() {
		return this.ships;
	}

	public Optional<Location> getPreviousLocation() {
		return this.previousLocation;
	}

	public Optional<Boolean> isPreviousJustLeaving() {
		return this.previousJustLeaving;
	}

	public Location getLocation() {
		return this.location;
	}

	public Optional<HorizontalDirection> getHorizontalDirection() {
		return this.horizontalDirection;
	}

	public ShipTypeView getShipType(String name) {
		return this.ships.entrySet()
			.stream()
			.map(Entry::getKey)
			.filter(st -> st.getName().equals(name))
			.findFirst()
			.get();
	}

	public Optional<SystemId> getSource() {
		return this.source;
	}

	public Optional<SystemId> getDestination() {
		return this.destination;
	}

	public Optional<Integer> getSpeed() {
		return this.speed;
	}

	public Optional<SystemId> getClosest() {
		return this.closest;
	}

	public Optional<SystemId> getOrbiting() {
		return this.orbiting;
	}

	public boolean isDeployable() {
		return this.deployable;
	}

	public Optional<Integer> getScannerRange() {
		return this.scannerRange;
	}

	public Optional<Boolean> isJustLeaving() {
		return this.justLeaving;
	}

	public boolean didJustArrive() {
		return !this.fleetsBeforeArrival.isEmpty();
	}

	public Set<FleetBeforeArrival> getFleetsBeforeArrival() {
		return this.fleetsBeforeArrival;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			FleetView other = (FleetView) obj;
			return Objects.equals(this.id, other.id);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		return toString(SystemId::toString);
	}

	public String toString(Function<SystemId, String> starNameResolver) {
		StringJoiner values = new StringJoiner(", ",
				(this.type == FleetViewType.DEPLOYED ? "DeployedFleet" : "OrbitingFleet") + "View[", "]")
			.add("id=" + this.id);

		if (this.parentId.isPresent()) {
			values.add("parentId=" + this.parentId.get());
		}

		values.add("type=" + this.type).add("player=" + this.player).add("race=" + this.race);

		if (this.previousLocation.isPresent()) {
			values.add("previousLocation=" + this.previousLocation.get());
		}
		if (this.previousJustLeaving.isPresent()) {
			values.add("previousJustLeaving=" + this.previousJustLeaving.get());
		}

		values.add("location=" + this.location);

		values.add("deployable=" + this.deployable);
		if (this.horizontalDirection.isPresent()) {
			values.add("horizontalDirection=" + this.horizontalDirection.get());
		}

		if (this.scannerRange.isPresent()) {
			values.add("scannerRange=" + this.scannerRange.get());
		}

		if (this.type == FleetViewType.DEPLOYED) {
			values.add("source=" + starNameResolver.apply(this.source.get()));
			values.add("destination=" + starNameResolver.apply(this.destination.get()));
			values.add("speed=" + this.speed.get());
		}
		else if (this.type == FleetViewType.ORBITING) {
			values.add("orbiting=" + starNameResolver.apply(this.orbiting.get()));
		}

		if (!this.fleetsBeforeArrival.isEmpty()) {
			values.add("fleetsBeforeArrival=" + this.fleetsBeforeArrival);
		}

		if (this.justLeaving.isPresent()) {
			values.add("justLeaving=" + this.justLeaving.get());
		}

		values.add("ships=" + getShips().entrySet()
			.stream()
			.map(typeAndAmmount -> Map.entry(typeAndAmmount.getKey().getName(), typeAndAmmount.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));

		return values.toString();
	}

}
