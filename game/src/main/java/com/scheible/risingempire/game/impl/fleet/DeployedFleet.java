package com.scheible.risingempire.game.impl.fleet;

import java.util.Map;
import java.util.Objects;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.util.jdk.Objects2;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

/**
 * @author sj
 */
public class DeployedFleet extends Fleet {

	private final SystemOrb source;

	private final SystemOrb destination;

	private Location previousLocation;

	private boolean previousJustLeaving;

	private Location location;

	private int speed;

	public DeployedFleet(FleetId id, Player player, Map<DesignSlot, Integer> ships, SystemOrb source,
			SystemOrb destination, int speed) {
		super(id, player, ships);

		this.source = source;
		this.destination = destination;

		this.previousLocation = source.getLocation();
		this.previousJustLeaving = true;
		this.location = source.getLocation();

		this.speed = speed;
	}

	public void turn() {
		if (hasArrived()) {
			throw new IllegalStateException("The fleet " + id + " already arrived!");
		}

		this.previousLocation = this.location;
		this.previousJustLeaving = this.previousLocation.equals(this.source.getLocation());
		this.location = this.location.moveAlong(this.destination.getLocation(), this.speed);
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isJustLeaving() {
		return this.location.equals(this.source.getLocation());
	}

	public boolean hasArrived() {
		return this.location.equals(this.destination.getLocation());
	}

	public HorizontalDirection getHorizontalDirection() {
		return getSource().getLocation().x() < getDestination().getLocation().x() ? HorizontalDirection.RIGHT
				: HorizontalDirection.LEFT;
	}

	@Override
	public double getDestinationDistance() {
		return this.destination.getLocation().distance(this.location);
	}

	public SystemOrb getSource() {
		return this.source;
	}

	public SystemOrb getDestination() {
		return this.destination;
	}

	public Location getPreviousLocation() {
		return this.previousLocation;
	}

	public boolean isPreviousJustLeaving() {
		return this.previousJustLeaving;
	}

	@Override
	public Location getLocation() {
		return this.location;
	}

	public int getSpeed() {
		return this.speed;
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(this.id, other.id));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("id", this.id)
			.add("source", this.source.getName())
			.add("destination", this.destination.getName())
			.add("previousLocation", this.previousLocation)
			.add("previousJustLeaving", this.previousJustLeaving)
			.add("location", this.location)
			.add("horizontalDirection", getHorizontalDirection())
			.add("speed", this.speed)
			.add("ships", this.ships)
			.toString();
	}

}
