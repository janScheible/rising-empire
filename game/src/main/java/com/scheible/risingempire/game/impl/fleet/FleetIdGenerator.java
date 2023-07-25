package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Set;

import com.scheible.risingempire.game.api.view.fleet.FleetId;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class FleetIdGenerator {

	private final Set<FleetId> used;

	public FleetIdGenerator(final Set<FleetId> used) {
		this.used = Collections.unmodifiableSet(used);
	}

	@SuppressFBWarnings(value = "SUI_CONTAINS_BEFORE_ADD", justification = "Might be in the Set already because of random generation.")
	public FleetId createRandom() {
		FleetId id = null;

		do {
			id = FleetId.createRandom();
		} while (Character.isDigit(id.getValue().charAt(0)) || used.contains(id));

		return id;
	}
}
