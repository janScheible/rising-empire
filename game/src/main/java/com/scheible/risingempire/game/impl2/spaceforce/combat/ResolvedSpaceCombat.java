package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public record ResolvedSpaceCombat(Outcome outcome, int fireExchangeCount,
		Map<ShipCombatSpecs, List<FireExchange>> attackerFireExchanges, boolean attackerShipSpecsAvailable,
		Map<ShipCombatSpecs, List<FireExchange>> defenderFireExchanges, boolean defenderShipSpecsAvailable) {

	public Ships attackerShips(Ships previousAttackerShips) {
		return new Ships(previousAttackerShips.stream()
			.collect(Collectors.toMap(Entry::getKey,
					e -> countAfterCombat(e.getKey(), previousAttackerShips, this.attackerFireExchanges))));
	}

	public Ships defenderShips(Ships previousDefenderShips) {
		return new Ships(previousDefenderShips.stream()
			.collect(Collectors.toMap(Entry::getKey,
					e -> countAfterCombat(e.getKey(), previousDefenderShips, this.defenderFireExchanges))));
	}

	private int countAfterCombat(ShipClassId shipClassId, Ships ships,
			Map<ShipCombatSpecs, List<FireExchange>> fireExchanges) {
		Optional<Integer> lastFireExchangeCount = fireExchanges.entrySet()
			.stream()
			.filter(e -> e.getKey().shipClassId().equals(shipClassId))
			.findFirst()
			.map(e -> e.getValue().getLast().shipCount());

		return lastFireExchangeCount.orElse(ships.count(shipClassId));
	}

}
