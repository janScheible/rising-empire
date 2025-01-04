package com.scheible.risingempire.game.impl2.universe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public class Universe {

	private Parsec galaxyWidth;

	private Parsec galaxyHeight;

	private List<Star> stars;

	public Universe(Parsec galaxyWidth, Parsec galaxyHeight, List<Star> stars) {
		this.galaxyWidth = galaxyWidth;
		this.galaxyHeight = galaxyHeight;
		this.stars = new ArrayList<>(stars);
	}

	public Parsec height() {
		return this.galaxyHeight;
	}

	public Parsec width() {
		return this.galaxyWidth;
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
		return new Planet(PlanetType.TERRAN, PlanetSpecial.NONE, new Population(100));
	}

	public Planet planet(Position system) {
		return new Planet(PlanetType.TERRAN, PlanetSpecial.NONE, new Population(100));
	}

	public List<Star> stars() {
		return Collections.unmodifiableList(this.stars);
	}

}
