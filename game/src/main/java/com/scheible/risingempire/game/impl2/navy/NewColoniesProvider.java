package com.scheible.risingempire.game.impl2.navy;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface NewColoniesProvider {

	Set<NewColony> newColonies();

	record NewColony(Player player, Position system) {

	}

}
