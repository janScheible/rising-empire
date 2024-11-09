package com.scheible.risingempire.game.impl2.technology;

import java.util.Map;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.intelligence.ShipScanSpecsProvider;
import com.scheible.risingempire.game.impl2.navy.ShipSpecsProvider;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;

public class Technology implements ShipSpecsProvider, ShipScanSpecsProvider {

	private static final Map<ShipClassId, Speed> SPEEDS = Map.of( //
			new ShipClassId("enterprise"), new Speed(new Parsec(1.0)), //
			new ShipClassId("scout"), new Speed(new Parsec(1.5)), //
			ShipClassId.COLONISTS_TRANSPORTER, new Speed(new Parsec(1.0)));

	private static final Map<ShipClassId, Parsec> RANGES = Map.of(//
			new ShipClassId("enterprise"), new Parsec(3.0), //
			new ShipClassId("scout"), new Parsec(4.0), //
			ShipClassId.COLONISTS_TRANSPORTER, new Parsec(3.0));

	@Override
	public Speed speed(Player player, ShipClassId shipClassId) {
		return SPEEDS.get(shipClassId);
	}

	@Override
	public Parsec range(Player player, ShipClassId shipClassId) {
		return RANGES.get(shipClassId);
	}

	@Override
	public Parsec range(Player player) {
		return RANGES.values().stream().min(Parsec::compareTo).get();
	}

	@Override
	public Parsec extendedRange(Player player) {
		return RANGES.values().stream().max(Parsec::compareTo).get();
	}

	@Override
	public Parsec scanRange(Player player, ShipClassId shipClassId) {
		return new Parsec(2.0);
	}

}