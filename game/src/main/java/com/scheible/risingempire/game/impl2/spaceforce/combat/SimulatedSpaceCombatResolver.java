package com.scheible.risingempire.game.impl2.spaceforce.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.spaceforce.combat.CombatStack.Side;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatBeamWeapon;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatMissile;
import com.scheible.risingempire.game.impl2.spaceforce.combat.weapon.CombatWeapon;
import com.scheible.risingempire.util.SeededRandom;
import com.scheible.risingempire.util.jdk.Lists2;

/**
 * @author sj
 */
public class SimulatedSpaceCombatResolver implements SpaceCombatResolver {

	private final ShipCombatSpecsProvider shipCombatSpecsProvider;

	private final SeededRandom random;

	public SimulatedSpaceCombatResolver(ShipCombatSpecsProvider shipCombatSpecsProvider, SeededRandom random) {
		this.shipCombatSpecsProvider = shipCombatSpecsProvider;
		this.random = random;
	}

	@Override
	public ResolvedSpaceCombatRecord resolve(Player attacker, Ships attackerShips, Player defender,
			Ships defenderShips) {
		return resolve(toCombatSpecs(attacker, attackerShips), toCombatSpecs(defender, defenderShips));
	}

	ResolvedSpaceCombatRecord resolve(Map<ShipCombatSpecs, Integer> attacker, Map<ShipCombatSpecs, Integer> defender) {
		Map<ShipCombatSpecs, List<FireExchange>> attackerFireExchanges = new HashMap<>();
		Map<ShipCombatSpecs, List<FireExchange>> defenderFireExchanges = new HashMap<>();
		Outcome outcome = Outcome.ATTACKER_RETREATED;

		Set<CombatStack> attackingStacks = toStack(attacker, Side.ATTACKER, this.random);
		Set<CombatStack> defendingStacks = toStack(defender, Side.DEFENDER, this.random);

		List<CombatStack> allStacksList = Stream.concat(attackingStacks.stream(), defendingStacks.stream())
			.sorted(Comparator.<CombatStack>comparingInt(s -> s.design.initiative() + s.design.combatSpeed())
				.reversed())
			.collect(Collectors.toList());

		int fireExchangeCount = 0;
		for (int round = 0; round < 50; round++) {
			for (CombatStack attackingStack : allStacksList) {
				if (attackingStack.isDestroyed()) {
					continue;
				}

				List<CombatStack> otherSideStacks = (attackingStack.side == Side.ATTACKER ? defendingStacks
						: attackingStacks)
					.stream()
					.filter(s -> !s.isDestroyed())
					.sorted()
					.collect(Collectors.toList());

				if (otherSideStacks.isEmpty()) {
					outcome = attackingStack.side == Side.ATTACKER ? Outcome.ATTACKER_WON : Outcome.DEFENDER_WON;
				}
				else {
					CombatStack defendingStack = Lists2.getRandomElement(otherSideStacks, this.random);
					int totalLostHitPoints = fireWeapons(attackingStack, defendingStack);

					if (totalLostHitPoints > 0) {
						/*
						 * System.out.println(round + ": " + totalLostHitPoints +
						 * " damage on " + defendingStack.side + " " +
						 * defendingStack.design.getName() + " (" + defendingStack.count +
						 * "/" + defendingStack.previousCount + " ships)");
						 */

						addMerged(
								(defendingStack.side == Side.ATTACKER ? attackerFireExchanges : defenderFireExchanges)
									.computeIfAbsent(defendingStack.design, key -> new ArrayList<>()),
								new FireExchange(round, totalLostHitPoints, defendingStack.getDamage(),
										defendingStack.count));

						fireExchangeCount = round + 1;
					}
				}
			}
		}

		return new ResolvedSpaceCombatRecord(outcome, fireExchangeCount, attackerFireExchanges,
				battleScanner(defender.keySet()), defenderFireExchanges, battleScanner(attacker.keySet()));
	}

	private static boolean battleScanner(Set<ShipCombatSpecs> combatSpecs) {
		return combatSpecs.stream().anyMatch(ShipCombatSpecs::battleScanner);
	}

	private int fireWeapons(CombatStack attackingStack, CombatStack defendingStack) {
		int totalLostHitPoints = 0;

		for (Entry<CombatWeapon, Integer> weaponSlot : attackingStack.design.weapons().entrySet()) {
			CombatWeapon weapon = weaponSlot.getKey();
			for (int weaponIndex = 0; weaponIndex < weaponSlot.getValue(); weaponIndex++) {

				boolean missilesLeft = false;
				if (weapon instanceof CombatMissile) {
					CombatMissile missile = (CombatMissile) weapon;
					int shots = attackingStack.missileLaunches.getOrDefault(missile, 0);
					if (shots < missile.rackSize()) {
						attackingStack.missileLaunches.put(missile, shots + 1);
						missilesLeft = true;
					}
				}

				for (int attackerShipIndex = 0; attackerShipIndex < attackingStack.count; attackerShipIndex++) {
					int lostHitPoints = 0;

					if (weapon instanceof CombatBeamWeapon || weapon instanceof CombatMissile && missilesLeft) {
						lostHitPoints = defendingStack.hitWith(weapon);
						totalLostHitPoints += lostHitPoints;
					}

					if (defendingStack.isDestroyed()) {
						return totalLostHitPoints;
					}
				}
			}
		}

		return totalLostHitPoints;
	}

	private Map<ShipCombatSpecs, Integer> toCombatSpecs(Player player, Ships ships) {
		return ships.stream()
			.map(e -> Map.entry(this.shipCombatSpecsProvider.shipCombatSpecs(player, e.getKey()), e.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	private static Set<CombatStack> toStack(Map<ShipCombatSpecs, Integer> fleet, Side side, SeededRandom random) {
		return fleet.entrySet()
			.stream()
			.map(e -> new CombatStack(e.getKey(), e.getValue(), side, random))
			.collect(Collectors.toSet());
	}

	/**
	 * Fire exchanges for the same round have to be merged.
	 */
	private static void addMerged(List<FireExchange> exchanges, FireExchange current) {
		FireExchange previous = exchanges.isEmpty() ? null : exchanges.get(exchanges.size() - 1);

		if (previous != null && previous.round() == current.round()) {
			exchanges.set(exchanges.size() - 1, new FireExchange(current.round(),
					previous.lostHitPoints() + current.lostHitPoints(), current.damage(), current.shipCount()));
		}
		else {
			exchanges.add(current);
		}
	}

	private record ResolvedSpaceCombatRecord(Outcome outcome, int fireExchangeCount,
			Map<ShipCombatSpecs, List<FireExchange>> attackerFireExchanges, boolean attackerShipSpecsAvailable,
			Map<ShipCombatSpecs, List<FireExchange>> defenderFireExchanges,
			boolean defenderShipSpecsAvailable) implements ResolvedSpaceCombat {

		@Override
		public Ships attackerShips(Ships previousAttackerShips) {
			return new Ships(previousAttackerShips.stream()
				.collect(Collectors.toMap(Entry::getKey,
						e -> countAfterCombat(e.getKey(), previousAttackerShips, this.attackerFireExchanges))));
		}

		@Override
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

}
