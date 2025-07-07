package com.scheible.risingempire.game.impl2.spaceforce;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public record SpaceCombatFleet(Player player, Position position, Ships ships) {

}
