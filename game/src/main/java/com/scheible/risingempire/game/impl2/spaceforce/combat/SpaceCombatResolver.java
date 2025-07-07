package com.scheible.risingempire.game.impl2.spaceforce.combat;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public interface SpaceCombatResolver {

	ResolvedSpaceCombat resolve(Player attacker, Ships attackerShips, Player defender, Ships defenderShips);

}
