package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface ArrivedFleetsProvider {

	Map<Player, Set<Position>> arrivedFleets();

}
