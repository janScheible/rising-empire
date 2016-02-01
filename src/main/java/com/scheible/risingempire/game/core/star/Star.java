package com.scheible.risingempire.game.core.star;

import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author sj
 */
public class Star {
	
	public static class Prototype {

		private String name;
		private Vector2D location;

		public Prototype(String name, int x, int y) {
			this.name = name;
			this.location = new Vector2D(x, y);
		}	

		public String getName() {
			return name;
		}

		public Vector2D getLocation() {
			return location;
		}
	}
	
	private final String name;
	private Vector2D location;
	
	private Optional<Colony> colony;

	public Star(String name, Vector2D location, Optional<Colony> colony) {
		this.name = name;
		this.location = location;
		
		this.colony = colony;
	}

	public String getName() {
		return name;
	}

	public Vector2D getLocation() {
		return location;
	}

	public Optional<Colony> getColony() {
		return colony;
	}
}
