package com.scheible.risingempire.game.impl2.colonization;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface AnnexedSystemsProvider {

	Set<AnnexedSystem> annexedSystems();

	record AnnexedSystem(Player player, Position system) {

	}

}
