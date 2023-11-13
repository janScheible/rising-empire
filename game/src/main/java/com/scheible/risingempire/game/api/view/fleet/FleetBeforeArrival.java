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

	public FleetBeforeArrival(final FleetId id, final HorizontalDirection horizontalDirection, final int speed) {
		this.id = id;
		this.horizontalDirection = horizontalDirection;
		this.speed = speed;
	}

	public FleetId getId() {
		return id;
	}

	public HorizontalDirection getHorizontalDirection() {
		return horizontalDirection;
	}

	public int getSpeed() {
		return speed;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			final FleetBeforeArrival other = (FleetBeforeArrival) obj;
			return Objects.equals(id, other.id) && Objects.equals(horizontalDirection, other.horizontalDirection)
					&& Objects.equals(speed, other.speed);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, horizontalDirection, speed);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", "FleetBeforeArrival[", "]").add("id=" + id)
			.add("horizontalDirection=" + horizontalDirection)
			.add("speed=" + speed)
			.toString();
	}

}
