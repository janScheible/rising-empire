package com.scheible.risingempire.game.impl2.colonization;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public record Colony(Player player, Position position, ShipClassId spaceDockShipClass) {

}
