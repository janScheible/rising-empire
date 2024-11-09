package com.scheible.risingempire.game.impl2.intelligence;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;

/**
 * @author sj
 */
public interface ColonyScanSpecsProvider {

	Parsec scanRange(Player player);

}
