package com.scheible.risingempire.game.impl2.navy;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;

/**
 * @author sj
 */
public interface ShipSpeedSpecsProvider {

	Speed speed(Player player, ShipClassId shipClassId);

	default Speed effectiveSpeed(Player player, Set<ShipClassId> shipClassIds) {
		return shipClassIds.stream().map(id -> speed(player, id)).min(Speed::compareTo).orElseThrow();
	}

}
