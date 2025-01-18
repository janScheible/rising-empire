package com.scheible.risingempire.game.impl2.ship;

import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public record ShipDesign(ShipClassId id, int index, String name, ShipSize size, int look) {

}
