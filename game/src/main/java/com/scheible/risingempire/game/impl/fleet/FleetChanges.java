package com.scheible.risingempire.game.impl.fleet;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class FleetChanges {

	private final Set<Fleet> added;
	private final Set<Fleet> removed;
	private final List<SpaceCombat> combats;
	private final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping;

	public FleetChanges(final Set<Fleet> added, final Set<Fleet> removed) {
		this.added = unmodifiableSet(added);
		this.removed = unmodifiableSet(removed);
		this.combats = emptyList();
		this.orbitingArrivingMapping = emptyMap();
	}

	public FleetChanges(final Set<Fleet> added, final Set<Fleet> removed, final List<SpaceCombat> combats,
			final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping) {
		this.added = unmodifiableSet(added);
		this.removed = unmodifiableSet(removed);
		this.combats = unmodifiableList(combats);
		this.orbitingArrivingMapping = unmodifiableMap(orbitingArrivingMapping);
	}

	public void consume(final Consumer<Fleet> addedConsumer, final Consumer<Fleet> removedConsumer) {
		added.forEach(addedConsumer::accept);
		removed.forEach(removedConsumer::accept);
	}

	public Set<Fleet> getAdded() {
		return added;
	}

	public Set<Fleet> getRemoved() {
		return removed;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP")
	public List<SpaceCombat> getCombats() {
		return combats;
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP")
	public Map<FleetId, Set<FleetBeforeArrival>> getOrbitingArrivingMapping() {
		return orbitingArrivingMapping;
	}
}
