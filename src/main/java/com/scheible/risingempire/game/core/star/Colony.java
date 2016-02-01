package com.scheible.risingempire.game.core.star;

import com.scheible.risingempire.game.core.Leader;

/**
 *
 * @author sj
 */
public class Colony {
	
	private final Leader leader;
	private int population;

	public Colony(Leader leader, int population) {
		this.leader = leader;
		this.population = population;
	}

	public Leader getLeader() {
		return leader;
	}

	public int getPopulation() {
		return population;
	}
}
