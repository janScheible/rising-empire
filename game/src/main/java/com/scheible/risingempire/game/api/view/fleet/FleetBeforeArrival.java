package com.scheible.risingempire.game.api.view.fleet;

import java.util.Objects;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;

/**
 * @author sj
 */
public class FleetBeforeArrival {

	private final FleetId id;

	private final HorizontalDirection horizontalDirection;

	private final int speed;

	public FleetBeforeArrival(FleetId id, HorizontalDirection horizontalDirection, int speed) {
		this.id = id;
		this.horizontalDirection = horizontalDirection;
		this.speed = speed;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			FleetBeforeArrival other = (FleetBeforeArrival) obj;
			return Objects.equals(this.id, other.id)
					&& Objects.equals(this.horizontalDirection, other.horizontalDirection)
					&& Objects.equals(this.speed, other.speed);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.horizontalDirection, this.speed);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "FleetBeforeArrival[", "]").add("id=" + this.id)
			.add("horizontalDirection=" + this.horizontalDirection)
			.add("speed=" + this.speed)
			.toString();
	}

}
