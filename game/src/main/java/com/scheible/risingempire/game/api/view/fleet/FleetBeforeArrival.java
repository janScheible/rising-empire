package com.scheible.risingempire.game.api.view.fleet;

import java.util.Objects;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;

/**
 * @author sj
 */
public class FleetBeforeArrival {

	private final FleetId id;

	private final HorizontalDirection horizontalDirection;

	private final int speed;

	private final Location location;

	private final boolean justLeaving;

	public FleetBeforeArrival(FleetId id, HorizontalDirection horizontalDirection, int speed, Location location,
			boolean justLeaving) {
		this.id = id;
		this.horizontalDirection = horizontalDirection;
		this.speed = speed;
		this.location = location;
		this.justLeaving = justLeaving;
	}

	public FleetId getId() {
		return this.id;
	}

	public HorizontalDirection getHorizontalDirection() {
		return this.horizontalDirection;
	}

	public int getSpeed() {
		return this.speed;
	}

	public Location getLocation() {
		return this.location;
	}

	public boolean isJustLeaving() {
		return this.justLeaving;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			FleetBeforeArrival other = (FleetBeforeArrival) obj;
			return Objects.equals(this.id, other.id)
					&& Objects.equals(this.horizontalDirection, other.horizontalDirection)
					&& Objects.equals(this.speed, other.speed) && Objects.equals(this.location, other.location)
					&& Objects.equals(this.justLeaving, other.justLeaving);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.horizontalDirection, this.speed, this.location, this.justLeaving);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "FleetBeforeArrival[", "]").add("id=" + this.id)
			.add("horizontalDirection=" + this.horizontalDirection)
			.add("speed=" + this.speed)
			.add("location=" + this.location)
			.add("justLeaving=" + this.justLeaving)
			.toString();
	}

}
