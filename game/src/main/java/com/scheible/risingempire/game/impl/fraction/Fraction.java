package com.scheible.risingempire.game.impl.fraction;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.system.SystemSnapshot;
import com.scheible.risingempire.util.jdk.Objects2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class Fraction {

	private final Player player;
	private final Race race;
	private final Map<DesignSlot, ShipDesign> shipDesigns;
	private final Technology technology;
	private final Map<SystemId, SystemSnapshot> systemSnapshots = new HashMap<>();

	public Fraction(final Player player, final Race race, final Map<DesignSlot, ShipDesign> shipDesigns,
			final Technology technology) {
		this.player = player;
		this.race = race;
		this.shipDesigns = new EnumMap<>(shipDesigns);
		this.technology = technology;
	}

	public Player getPlayer() {
		return player;
	}

	public Race getRace() {
		return race;
	}

	public Map<DesignSlot, ShipDesign> getShipDesigns() {
		return Collections.unmodifiableMap(shipDesigns);
	}

	public Technology getTechnology() {
		return technology;
	}

	public Optional<SystemSnapshot> getSnapshot(final SystemId systemId) {
		return Optional.ofNullable(systemSnapshots.get(systemId));
	}

	public void updateSnapshot(final SystemId systemId, final SystemSnapshot snapshot) {
		final int firstSeenTurn = Optional.ofNullable(systemSnapshots.get(systemId))
				.flatMap(SystemSnapshot::getFirstSeenTurn).orElseGet(() -> snapshot.getLastSeenTurn());
		systemSnapshots.put(systemId,
				snapshot.getFirstSeenTurn().filter(fst -> fst == firstSeenTurn).isPresent() ? snapshot
						: SystemSnapshot.withFirstSeenTurn(snapshot, firstSeenTurn));
	}

	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	@Override
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(player, other.player));
	}

	@Override
	public int hashCode() {
		return Objects.hash(player);
	}

	@Override
	public String toString() {
		return Objects2.toStringBuilder(getClass()).add("player", player).add("race", race).toString();
	}
}
