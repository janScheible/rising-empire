package com.scheible.risingempire.game.impl2.colonization;

import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface MaxPopulationProvider {

	Population max(Position system);

}
