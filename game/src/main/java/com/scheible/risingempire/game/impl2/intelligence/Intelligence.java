package com.scheible.risingempire.game.impl2.intelligence;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;

/**
 * @author sj
 */
public class Intelligence implements ColonyScanSpecsProvider, ShipScanSpecsProvider {

	@Override
	public Parsec scanRange(Player player) {
		return new Parsec(0.2);
	}

	@Override
	public Parsec scanRange(Player player, ShipClassId shipClassId) {
		return new Parsec(0.2);
	}

}
