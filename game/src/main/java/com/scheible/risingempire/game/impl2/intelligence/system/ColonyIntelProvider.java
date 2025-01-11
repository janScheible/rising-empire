package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface ColonyIntelProvider {

	Optional<ColonyIntel> colony(Position system);

	record ColonyIntel(Player player, Population population) {

	}

}
