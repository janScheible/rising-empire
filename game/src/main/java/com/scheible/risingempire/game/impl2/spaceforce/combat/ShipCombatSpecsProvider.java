package com.scheible.risingempire.game.impl2.spaceforce.combat;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public interface ShipCombatSpecsProvider {

	ShipCombatSpecs shipCombatSpecs(Player player, ShipClassId shipClassId);

}
