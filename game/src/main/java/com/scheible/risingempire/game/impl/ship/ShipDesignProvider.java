package com.scheible.risingempire.game.impl.ship;

import com.scheible.risingempire.game.api.universe.Player;

/**
 * @author sj
 */
@FunctionalInterface
public interface ShipDesignProvider {

	ShipDesign get(Player player, DesignSlot slot);

}
