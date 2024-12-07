package com.scheible.risingempire.game.impl2.technology;

import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;

public class Technology {

	private static final Map<ShipClassId, Speed> SPEEDS = Map.of( //
			new ShipClassId("enterprise"), new Speed(1.0), //
			new ShipClassId("scout"), new Speed(1.5), //
			ShipClassId.COLONISTS_TRANSPORTER, new Speed(1.0));

	private static final Map<ShipClassId, Parsec> RANGES = Map.of(//
			new ShipClassId("enterprise"), new Parsec(3.0), //
			new ShipClassId("scout"), new Parsec(4.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Parsec(3.0));

	public Speed speed(Player player, ShipClassId shipClassId) {
		return SPEEDS.get(shipClassId);
	}

	public Parsec range(Player player, ShipClassId shipClassId) {
		return RANGES.get(shipClassId);
	}

	public Parsec range(Player player) {
		return RANGES.values().stream().min(Parsec::compareTo).get();
	}

	public Parsec extendedRange(Player player) {
		return RANGES.values().stream().max(Parsec::compareTo).get();
	}

	public Parsec scanRange(Player player, ShipClassId shipClassId) {
		return new Parsec(0.5);
	}

	public Parsec effectiveScanRange(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> scanRange(player, id)).max(Parsec::compareTo).orElseThrow();
	}

	public Parsec colonyScanRange(Player player) {
		return new Parsec(1.5);
	}

}
