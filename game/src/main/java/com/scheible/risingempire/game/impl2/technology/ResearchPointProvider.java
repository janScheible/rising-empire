package com.scheible.risingempire.game.impl2.technology;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;

/**
 * @author sj
 */
public interface ResearchPointProvider {

	ResearchPoint researchPoints(Player player);

}
