package com.scheible.risingempire.game.impl.system;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * @author sj
 */
@FunctionalInterface
public interface SystemSnapshotter {

	void put(Player player, SystemId systemId, SystemSnapshot snapshot);

}
