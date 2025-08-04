package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public class PredefinedSpaceCombatResolver implements SpaceCombatResolver {

	private final Outcome predefinedOutcome;

	private final SpaceCombatResolver delegate;

	public PredefinedSpaceCombatResolver(Outcome predefinedOutcome, SpaceCombatResolver delegate) {
		this.predefinedOutcome = predefinedOutcome;
		this.delegate = delegate;
	}

	@Override
	public ResolvedSpaceCombat resolve(Player attacker, Ships attackerShips, Player defender, Ships defenderShips) {
		ResolvedSpaceCombat delegateResult = this.delegate.resolve(attacker, attackerShips, defender, defenderShips);

		return new ResolvedSpaceCombatRecord(this.predefinedOutcome, delegateResult.attackerShipSpecsAvailable(),
				delegateResult.defenderShipSpecsAvailable());
	}

	private record ResolvedSpaceCombatRecord(Outcome outcome, boolean attackerShipSpecsAvailable,
			boolean defenderShipSpecsAvailable) implements ResolvedSpaceCombat {

		@Override
		public int fireExchangeCount() {
			return 0;
		}

		@Override
		public Map<ShipCombatSpecs, List<FireExchange>> attackerFireExchanges() {
			return Map.of();
		}

		@Override
		public Map<ShipCombatSpecs, List<FireExchange>> defenderFireExchanges() {
			return Map.of();
		}

		@Override
		public Ships attackerShips(Ships previousAttackerShips) {
			if (this.outcome == Outcome.DEFENDER_WON) {
				return Ships.NONE;
			}
			else {
				return halve(previousAttackerShips);
			}
		}

		@Override
		public Ships defenderShips(Ships previousDefenderShips) {
			if (this.outcome == Outcome.ATTACKER_WON) {
				return Ships.NONE;
			}
			else {
				return halve(previousDefenderShips);
			}
		}

		private static Ships halve(Ships ships) {
			return new Ships(
					ships.stream().collect(Collectors.toMap(Entry::getKey, e -> Math.max(1, e.getValue() / 2))));
		}

	}

}
