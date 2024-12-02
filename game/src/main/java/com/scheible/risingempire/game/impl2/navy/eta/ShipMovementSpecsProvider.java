package com.scheible.risingempire.game.impl2.navy.eta;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.navy.ShipSpeedSpecsProvider;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;

public interface ShipMovementSpecsProvider extends ShipSpeedSpecsProvider {

	Parsec range(Player player, ShipClassId ShipClassId);

	Parsec range(Player player);

	Parsec extendedRange(Player player);

	default Parsec effectiveRange(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> range(player, id)).min(Parsec::compareTo).orElseThrow();
	}

}
