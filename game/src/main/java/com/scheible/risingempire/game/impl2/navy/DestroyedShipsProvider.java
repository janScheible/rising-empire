package com.scheible.risingempire.game.impl2.navy;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
@FunctionalInterface
public interface DestroyedShipsProvider {

	Set<DestroyedShips> destroyedShips();

	record DestroyedShips(Player player, Position system, Ships ships) {

	}

}
