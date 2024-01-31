package com.scheible.risingempire.game.api.universe;

import java.util.Objects;

/**
 * @author sj
 */
public class Location {

	private final int x;

	private final int y;

	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Location moveAlong(Location destination, int speed) {
		double distance = getDistance(destination);

		int newX = this.x + (int) (((destination.x - this.x) / distance) * speed);
		int newY = this.y + (int) (((destination.y - this.y) / distance) * speed);

		Location newLocation = new Location(newX, newY);
		double newDistance = getDistance(newLocation);

		return newDistance > distance ? destination : newLocation;
	}

	public double getDistance(Location other) {
		return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			Location other = (Location) obj;
			return Objects.equals(this.x, other.x) && Objects.equals(this.y, other.y);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.y);
	}

	@Override
	public String toString() {
		return "(" + this.x + "," + this.y + ")";
	}

}
