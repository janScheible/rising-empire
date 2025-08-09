package com.scheible.risingempire.game.impl2.technology;

/**
 * @author sj
 */
public enum TechCategory {

	FACTORY("More factories per population."), RESEARCH("More research points per population."),
	SHIP("Makes ships faster, extends their range and stats.");

	private final String description;

	TechCategory(String description) {
		this.description = description;
	}

	public String description() {
		return this.description;
	}

}
