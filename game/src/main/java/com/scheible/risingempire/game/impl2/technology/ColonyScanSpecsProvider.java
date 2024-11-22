package com.scheible.risingempire.game.impl2.technology;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;

/**
 * @author sj
 */
public interface ColonyScanSpecsProvider {

	Parsec colonyScanRange(Player player);

}
