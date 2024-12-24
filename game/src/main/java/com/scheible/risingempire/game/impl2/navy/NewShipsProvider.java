package com.scheible.risingempire.game.impl2.navy;

import java.util.Map;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public interface NewShipsProvider {

	Map<Position, Map<ShipClassId, Integer>> newShips(Player player);

}
