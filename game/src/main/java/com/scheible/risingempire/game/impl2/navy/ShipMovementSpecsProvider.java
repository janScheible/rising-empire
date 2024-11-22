package com.scheible.risingempire.game.impl2.navy;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;

public interface ShipMovementSpecsProvider {

	Speed speed(Player player, ShipClassId shipClassId);

	default Speed effectiveSpeed(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> speed(player, id)).min(Speed::compareTo).orElseThrow();
	}

	Parsec range(Player player, ShipClassId ShipClassId);

	Parsec range(Player player);

	Parsec extendedRange(Player player);

	default Parsec effectiveRange(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> range(player, id)).min(Parsec::compareTo).orElseThrow();
	}

}
