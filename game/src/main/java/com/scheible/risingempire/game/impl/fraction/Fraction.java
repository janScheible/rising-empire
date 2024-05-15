package com.scheible.risingempire.game.impl.fraction;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.system.SystemSnapshot;
import com.scheible.risingempire.util.jdk.Objects2;

/**
 * @author sj
 */
public class Fraction {

	private final Player player;

	private final Race race;

	private final Map<DesignSlot, ShipDesign> shipDesigns;

	private final Technology technology;

	private final Map<SystemId, SystemSnapshot> systemSnapshots = new HashMap<>();

	public Fraction(Player player, Race race, Map<DesignSlot, ShipDesign> shipDesigns, Technology technology) {
		this.player = player;
		this.race = race;
		this.shipDesigns = new EnumMap<>(shipDesigns);
		this.technology = technology;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Race getRace() {
		return this.race;
	}

	public Map<DesignSlot, ShipDesign> getShipDesigns() {
		return Collections.unmodifiableMap(this.shipDesigns);
	}

	public Technology getTechnology() {
		return this.technology;
	}

	public Optional<SystemSnapshot> getSnapshot(SystemId systemId) {
		return Optional.ofNullable(this.systemSnapshots.get(systemId));
	}

	public void updateSnapshot(SystemId systemId, SystemSnapshot snapshot) {
		int firstSeenTurn = Optional.ofNullable(this.systemSnapshots.get(systemId))
			.flatMap(SystemSnapshot::getFirstSeenTurn)
			.orElseGet(snapshot::getLastSeenTurn);
		this.systemSnapshots.put(systemId, snapshot.getFirstSeenTurn().filter(fst -> fst == firstSeenTurn).isPresent()
				? snapshot : SystemSnapshot.withFirstSeenTurn(snapshot, firstSeenTurn));
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(this.player, other.player));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.player);
	}

	@Override
	public String toString() {
		return Objects2.toStringBuilder(getClass()).add("player", this.player).add("race", this.race).toString();
	}

}
