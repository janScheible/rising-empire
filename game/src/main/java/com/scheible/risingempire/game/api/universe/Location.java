package com.scheible.risingempire.game.api.universe;

/**
 * @author sj
 */
public record Location(int x, int y) {

	public static Location ORIGIN = new Location(0, 0);

	public Location moveAlong(Location destination, int speed) {
		double distance = distance(destination);

		int newX = this.x + (int) (((destination.x - this.x) / distance) * speed);
		int newY = this.y + (int) (((destination.y - this.y) / distance) * speed);

		Location newLocation = new Location(newX, newY);
		double newDistance = distance(newLocation);

		return newDistance > distance ? destination : newLocation;
	}

	public double distance(Location other) {
		return Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2));
	}

}
