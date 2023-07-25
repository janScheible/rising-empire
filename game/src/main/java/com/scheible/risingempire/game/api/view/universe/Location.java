package com.scheible.risingempire.game.api.view.universe;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class Location {

	private final int x;
	private final int y;

	public Location(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Location moveAlong(final Location destination, final int speed) {
		final double distance = getDistance(destination);

		final int newX = x + (int) (((destination.x - x) / distance) * speed);
		final int newY = y + (int) (((destination.y - y) / distance) * speed);

		final Location newLocation = new Location(newX, newY);
		final double newDistance = getDistance(newLocation);

		return newDistance > distance ? destination : newLocation;
	}

	public double getDistance(final Location other) {
		return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && getClass().equals(obj.getClass())) {
			final Location other = (Location) obj;
			return Objects.equals(x, other.x) && Objects.equals(y, other.y);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
