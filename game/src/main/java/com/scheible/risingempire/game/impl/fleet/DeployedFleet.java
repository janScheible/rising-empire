package com.scheible.risingempire.game.impl.fleet;

import static com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection.LEFT;
import static com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection.RIGHT;
import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

import java.util.Map;
import java.util.Objects;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class DeployedFleet extends Fleet {

	private final SystemOrb source;
	private final SystemOrb destination;

	private Location location;

	private int speed;

	public DeployedFleet(final FleetId id, final Player player, final Map<DesignSlot, Integer> ships,
			final SystemOrb source, final SystemOrb destination, final int speed) {
		super(id, player, ships);

		this.source = source;
		this.destination = destination;

		this.location = source.getLocation();

		this.speed = speed;
	}

	public void turn() {
		if (hasArrived()) {
			throw new IllegalStateException("The fleet " + id + " already arrived!");
		}

		location = location.moveAlong(destination.getLocation(), speed);
	}

	public void setSpeed(final int speed) {
		this.speed = speed;
	}

	public boolean isJustLeaving() {
		return location.equals(source.getLocation());
	}

	public boolean hasArrived() {
		return location.equals(destination.getLocation());
	}

	public HorizontalDirection getHorizontalDirection() {
		return getSource().getLocation().getX() < getDestination().getLocation().getX() ? RIGHT : LEFT;
	}

	@Override
	public double getDestinationDistance() {
		return destination.getLocation().getDistance(location);
	}

	public SystemOrb getSource() {
		return source;
	}

	public SystemOrb getDestination() {
		return destination;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public int getSpeed() {
		return speed;
	}

	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	@Override
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(id, other.id));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("id", id).add("source", source.getName())
				.add("destination", destination.getName()).add("location", location)
				.add("horizontalDirection", getHorizontalDirection()).add("speed", speed).add("ships", ships)
				.toString();
	}
}
