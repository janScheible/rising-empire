package com.scheible.risingempire.game.impl2.universe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public class Universe {

	private GalaxySize galaxySize;

	private List<Star> stars;

	public Universe(GalaxySize galaxySize, List<Star> stars) {
		this.galaxySize = galaxySize;
		this.stars = new ArrayList<>(stars);
	}

	public Parsec height() {
		return new Parsec(this.galaxySize.height() / 10.0);
	}

	public Parsec width() {
		return new Parsec(this.galaxySize.width() / 10.0);
	}

	public Star closest(Position position) {
		Star closest = null;
		Parsec distance = null;

		for (Star star : this.stars) {
			if (closest == null) {
				closest = star;
				distance = star.position().subtract(position).length();
			}
			else {
				Parsec currentDistance = star.position().subtract(position).length();

				if (currentDistance.compareTo(distance) < 0) {
					closest = star;
					distance = currentDistance;
				}
			}
		}

		return closest;
	}

	public Planet planet(Star star) {
		return new Planet(PlanetType.TERRAN, PlanetSpecial.NONE);
	}

	public List<Star> stars() {
		return Collections.unmodifiableList(this.stars);
	}

}
