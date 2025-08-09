package com.scheible.risingempire.game.impl2.technology;

/**
 * @author sj
 */
public enum TechCategory {

	SHIP("Makes ships faster, extends their range and stats."), FACTORY("More factories per population."),
	RESEARCH("More research points per population.");

	private final String description;

	TechCategory(String description) {
		this.description = description;
	}

	public String description() {
		return this.description;
	}

}
