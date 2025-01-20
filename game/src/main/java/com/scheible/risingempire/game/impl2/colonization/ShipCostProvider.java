package com.scheible.risingempire.game.impl2.colonization;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public interface ShipCostProvider {

	Credit cost(Player player, ShipClassId shipClassId);

}
