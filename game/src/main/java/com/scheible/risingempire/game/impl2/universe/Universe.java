package com.scheible.risingempire.game.impl2.universe;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public record Universe(Parsec width, Parsec height, List<Star> stars, Map<Position, Planet> planets,
		Map<Player, Position> homeSystems) {

	public Universe {
		stars = Collections.unmodifiableList(stars);
		homeSystems = Collections.unmodifiableMap(homeSystems);
	}

	public Star closest(Position position, Predicate<Star> starPredicate) {
		Star closest = null;
		Parsec distance = null;

		for (Star star : this.stars) {
			if (!starPredicate.test(star) || position.equals(star.position())) {
				continue;
			}

			if (closest == null) {
				closest = star;
				distance = star.position().subtract(position).length();
			}
			else {
				Parsec currentDistance = star.position().subtract(position).length();

				if (currentDistance.lessThan(distance)) {
					closest = star;
					distance = currentDistance;
				}
			}
		}

		return closest;
	}

	public Parsec distance(Star from, Star to) {
		return to.position().subtract(from.position()).length();
	}

	public Planet planet(Star star) {
		return planet(star.position());
	}

	public Planet planet(Position system) {
		return this.planets.get(system);
	}

	public Star star(Position system) {
		return this.stars.stream().filter(s -> s.position().equals(system)).findFirst().orElseThrow();
	}

	public List<Star> stars() {
		return Collections.unmodifiableList(this.stars);
	}

	public boolean homeSystem(Player player, Position position) {
		return this.homeSystems.get(player).equals(position);
	}

}
