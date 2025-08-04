package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public interface ResolvedSpaceCombat {

	Outcome outcome();

	int fireExchangeCount();

	Map<ShipCombatSpecs, List<FireExchange>> attackerFireExchanges();

	boolean attackerShipSpecsAvailable();

	Map<ShipCombatSpecs, List<FireExchange>> defenderFireExchanges();

	boolean defenderShipSpecsAvailable();

	Ships attackerShips(Ships previousAttackerShips);

	Ships defenderShips(Ships previousDefenderShips);

}
