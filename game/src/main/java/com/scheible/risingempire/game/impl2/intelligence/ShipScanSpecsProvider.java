package com.scheible.risingempire.game.impl2.intelligence;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;

/**
 * @author sj
 */
public interface ShipScanSpecsProvider {

	Parsec scanRange(Player player, ShipClassId shipClassId);

	default Parsec effectiveScanRange(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> scanRange(player, id)).max(Parsec::compareTo).orElseThrow();
	}

}
