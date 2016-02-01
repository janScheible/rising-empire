package com.scheible.risingempire.game.core;

import java.util.Objects;

/**
 *
 * @author sj
 */
public class Leader {

	private final String name;
	private final String nation;
	private final String homeStar;

	public Leader(String name, String nation, String homeStar) {
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

	@Override
	public int hashCode() {
		return Objects.hash(name, nation);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Leader) {
			Leader other = (Leader) obj;
			return Objects.equals(name, other.name) && Objects.equals(nation, other.nation);
		} else {
			return false;
		}
	}
}
