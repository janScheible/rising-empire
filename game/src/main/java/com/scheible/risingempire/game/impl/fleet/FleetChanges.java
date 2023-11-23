package com.scheible.risingempire.game.impl.fleet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class FleetChanges {

	private final Set<Fleet> added;

	private final Set<Fleet> removed;

	private final List<SpaceCombat> combats;

	private final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping;

	public FleetChanges(Set<Fleet> added, Set<Fleet> removed) {
		this.added = unmodifiableSet(added);
		this.removed = unmodifiableSet(removed);
		this.combats = List.of();
		this.orbitingArrivingMapping = Map.of();
	}

	public FleetChanges(Set<Fleet> added, Set<Fleet> removed, List<SpaceCombat> combats,
			Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping) {
		this.added = unmodifiableSet(added);
		this.removed = unmodifiableSet(removed);
		this.combats = unmodifiableList(combats);
		this.orbitingArrivingMapping = unmodifiableMap(orbitingArrivingMapping);
	}

	public void consume(Consumer<Fleet> addedConsumer, Consumer<Fleet> removedConsumer) {
		this.added.forEach(addedConsumer::accept);
		this.removed.forEach(removedConsumer::accept);
	}

	public Set<Fleet> getAdded() {
		return this.added;
	}

	public Set<Fleet> getRemoved() {
		return this.removed;
	}

	public List<SpaceCombat> getCombats() {
		return this.combats;
	}

	public Map<FleetId, Set<FleetBeforeArrival>> getOrbitingArrivingMapping() {
		return this.orbitingArrivingMapping;
	}

}
