package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.spaceforce.SpaceCombatBuilder.SystemStage;
import com.scheible.risingempire.game.impl2.spaceforce.combat.FireExchange;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SpaceCombat(Position system, Player attacker, Player defender, Outcome outcome, int fireExchangeCount,
		Ships previousAttackerShips, Ships attackerShips, Map<ShipClassId, List<FireExchange>> attackerFireExchanges,
		boolean attackerShipSpecsAvailable, Ships previousDefenderShips, Ships defenderShips,
		Map<ShipClassId, List<FireExchange>> defenderFireExchanges, boolean defenderShipSpecsAvailable) {

	public static SystemStage builder() {
		return SpaceCombatBuilder.builder();
	}

}
