package com.scheible.risingempire.game.impl2.navy.eta;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface ColoniesProvider {

	Set<Position> colonies(Player player);

}
