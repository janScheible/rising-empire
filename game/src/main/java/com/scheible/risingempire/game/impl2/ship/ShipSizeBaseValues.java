package com.scheible.risingempire.game.impl2.ship;

import com.scheible.risingempire.game.api.view.ship.ShipSize;

/**
 * @author sj
 */
class ShipSizeBaseValues {

	static int baseDefense(ShipSize shipSize) {
		return switch (shipSize) {
			case ShipSize.SMALL -> 2;
			case ShipSize.MEDIUM -> 1;
			case ShipSize.LARGE -> 0;
			case ShipSize.HUGE -> -1;
		};
	}

	static int baseHits(ShipSize shipSize) {
		return switch (shipSize) {
			case ShipSize.SMALL -> 3;
			case ShipSize.MEDIUM -> 18;
			case ShipSize.LARGE -> 100;
			case ShipSize.HUGE -> 600;
		};
	}

}
