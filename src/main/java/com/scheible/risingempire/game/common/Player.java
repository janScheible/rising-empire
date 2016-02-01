package com.scheible.risingempire.game.common;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class Player {
	
	public static class Prototype {

		private final String name;
		private final String nation;
		private final String homeStar;

		public Prototype(String name, String nation, String homeStar) {
			this.name = name;
			this.nation = nation;
			this.homeStar = homeStar;
		}

		public String getName() {
			return name;
		}

		public String getNation() {
			return nation;
		}

		public String getHomeStar() {
			return homeStar;
		}
	}	
	
	private final String name;
	private final String nation;

	public Player(String name, String nation) {
		this.name = name;
		this.nation = nation;
	}

	public String getName() {
		return name;
	}

	public String getNation() {
		return nation;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, nation);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Player) {
			Player other = (Player) obj;
			return Objects.equals(name, other.name) && Objects.equals(nation, other.nation);
		} else {
			return false;
		}
	}
}
