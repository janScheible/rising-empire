package com.scheible.risingempire.game.impl2.colonization;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface ColonyFleetProvider {

	Set<Position> colonizableSystems(Player player);

}
