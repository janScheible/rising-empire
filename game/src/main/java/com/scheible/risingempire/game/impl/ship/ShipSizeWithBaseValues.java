package com.scheible.risingempire.game.impl.ship;

import com.scheible.risingempire.game.api.view.ship.ShipSize;

/**
 * @author sj
 */
enum ShipSizeWithBaseValues {

	SMALL(ShipSize.SMALL, 2, 3), MEDIUM(ShipSize.MEDIUM, 1, 18), LARGE(ShipSize.LARGE, 0, 100),
	HUGE(ShipSize.HUGE, -1, 600);

	final ShipSize shipSize;

	final int baseDefense;

	final int baseHits;

	ShipSizeWithBaseValues(final ShipSize shipSize, final int baseDefense, final int baseHits) {
		this.shipSize = shipSize;
		this.baseDefense = baseDefense;
		this.baseHits = baseHits;
	}

	static ShipSizeWithBaseValues of(final ShipSize shipSize) {
		return ShipSizeWithBaseValues.valueOf(shipSize.name());
	}

}
