package com.scheible.risingempire.game.impl.fleet;

import java.util.Set;

import com.scheible.risingempire.game.api.view.fleet.FleetId;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class FleetIdGenerator {

	private final Set<FleetId> used;

	public FleetIdGenerator(Set<FleetId> used) {
		this.used = unmodifiableSet(used);
	}

	public FleetId createRandom() {
		FleetId id = null;

		do {
			id = FleetId.createRandom();
		}
		while (Character.isDigit(id.value().charAt(0)) || this.used.contains(id));

		return id;
	}

}
