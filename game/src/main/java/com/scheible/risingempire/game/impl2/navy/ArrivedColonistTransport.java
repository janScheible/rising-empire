package com.scheible.risingempire.game.impl2.navy;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public record ArrivedColonistTransport(Player player, Position destination, int transporters) {

}
