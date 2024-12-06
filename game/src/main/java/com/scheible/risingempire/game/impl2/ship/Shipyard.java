package com.scheible.risingempire.game.impl2.ship;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public class Shipyard {

	public ShipDesign design(Player player, ShipClassId shipClassId) {
		if (shipClassId.value().equals("enterprise")) {
			return new ShipDesign(0, "Enterprise", ShipSize.HUGE, 0);
		}
		else {
			return new ShipDesign(0, "Scout", ShipSize.SMALL, 0);
		}
	}

}
