package com.scheible.risingempire.game.impl.system;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.system.SystemId;

/**
 * @author sj
 */
@FunctionalInterface
public interface SystemSnapshotter {

	void put(Player player, SystemId systemId, SystemSnapshot snapshot);

}
