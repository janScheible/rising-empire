package com.scheible.risingempire.game.impl2.spaceforce;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public record SpaceCombat(Position system, Player attacker, Player defender, Outcome outcome) {

}
