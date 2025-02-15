package com.scheible.risingempire.game.impl2.army;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public record SiegedSystem(Position position, Player colonyOwner, Player fleetOwner) {

}
