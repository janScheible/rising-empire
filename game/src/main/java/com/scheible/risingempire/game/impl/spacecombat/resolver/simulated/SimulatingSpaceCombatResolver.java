package com.scheible.risingempire.game.impl.spacecombat.resolver.simulated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.fleet.DeployedFleet;
import com.scheible.risingempire.game.impl.fleet.OrbitingFleet;
import com.scheible.risingempire.game.impl.fleet.SpaceCombatResolver;
import com.scheible.risingempire.game.impl.ship.AbstractWeapon;
import com.scheible.risingempire.game.impl.ship.BeamWeapon;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.Missile;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.ship.WeaponSlot;
import com.scheible.risingempire.game.impl.spacecombat.FireExchange;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;
import com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.CombatStack.Side;
import com.scheible.risingempire.util.jdk.Lists2;

/**
 * @author sj
 */
public class SimulatingSpaceCombatResolver implements SpaceCombatResolver {

	@Override
	public SpaceCombat resolve(SystemId systemId, OrbitingFleet defending, DeployedFleet attacking,
			ShipDesignProvider shipDesignProvider) {
		BiFunction<Map<DesignSlot, Integer>, Player, Map<ShipDesign, Integer>> toShipDesignCounts = (ships,
				player) -> ships.entrySet()
					.stream()
					.collect(Collectors.toMap(e -> shipDesignProvider.get(player, e.getKey()), Map.Entry::getValue));

		SpaceCombatSummary summary = simulate(toShipDesignCounts.apply(attacking.getShips(), attacking.getPlayer()),
				toShipDesignCounts.apply(defending.getShips(), defending.getPlayer()));

		BiFunction<ShipDesign, Player, DesignSlot> toDesginSlot = (design, player) -> {
			for (DesignSlot slot : DesignSlot.values()) {
				if (shipDesignProvider.get(player, slot) == design) {
					return slot;
				}
			}
			throw new IllegalStateException("Unkown ship desgin!");
		};

		BiFunction<Map<ShipDesign, List<FireExchange>>, Player, Map<DesignSlot, List<FireExchange>>> toDesignSlotsCombatantFireExchanges = (
				designWithFireExchanges, player) -> designWithFireExchanges.entrySet()
					.stream()
					.collect(Collectors.toMap(e -> toDesginSlot.apply(e.getKey(), player), Entry::getValue));

		return new SpaceCombat(systemId, summary.fireExchangeCount, attacking.getPlayer(),
				new FleetBeforeArrival(attacking.getId(), attacking.getHorizontalDirection(), attacking.getSpeed()),
				new HashMap<>(attacking.getShips()),
				toDesignSlotsCombatantFireExchanges.apply(summary.attackerFireExchanges, attacking.getPlayer()),
				defending.getPlayer(), defending.getId(), new HashMap<>(defending.getShips()),
				toDesignSlotsCombatantFireExchanges.apply(summary.defenderFireExchanges, defending.getPlayer()),
				summary.outcome);
	}

	SpaceCombatSummary simulate(Map<ShipDesign, Integer> attacker, Map<ShipDesign, Integer> defender) {
		Map<ShipDesign, List<FireExchange>> attackerFireExchanges = new HashMap<>();
		Map<ShipDesign, List<FireExchange>> defenderFireExchanges = new HashMap<>();
		Outcome outcome = Outcome.ATTACKER_RETREATED;

		Set<CombatStack> attackingStacks = toStack(attacker, Side.ATTACKER);
		Set<CombatStack> defendingStacks = toStack(defender, Side.DEFENDER);

		List<CombatStack> allStacksList = Stream.concat(attackingStacks.stream(), defendingStacks.stream())
			.sorted(Comparator.<CombatStack>comparingInt(s -> s.design.getInitiative() + s.design.getCombatSpeed())
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
					.collect(Collectors.toList());

				if (otherSideStacks.isEmpty()) {
					outcome = attackingStack.side == Side.ATTACKER ? Outcome.ATTACKER_WON : Outcome.DEFENDER_WON;
				}
				else {
					CombatStack defendingStack = Lists2.getRandomElement(otherSideStacks);
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

		return new SpaceCombatSummary(outcome, fireExchangeCount, attackerFireExchanges, defenderFireExchanges);
	}

	private int fireWeapons(CombatStack attackingStack, CombatStack defendingStack) {
		int totalLostHitPoints = 0;

		for (WeaponSlot weaponSlot : attackingStack.design.getWeaponSlots()) {
			AbstractWeapon weapon = weaponSlot.getWeapon();
			for (int weaponIndex = 0; weaponIndex < weaponSlot.getCount(); weaponIndex++) {

				boolean missilesLeft = false;
				if (weapon instanceof Missile) {
					Missile missile = (Missile) weapon;
					int shots = attackingStack.missileLaunches.getOrDefault(missile, 0);
					if (shots < missile.getRackSize().getSize()) {
						attackingStack.missileLaunches.put(missile, shots + 1);
						missilesLeft = true;
					}
				}

				for (int attackerShipIndex = 0; attackerShipIndex < attackingStack.count; attackerShipIndex++) {
					int lostHitPoints = 0;

					if (weapon instanceof BeamWeapon || weapon instanceof Missile && missilesLeft) {
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

	private static Set<CombatStack> toStack(Map<ShipDesign, Integer> fleet, Side side) {
		return fleet.entrySet()
			.stream()
			.map(e -> new CombatStack(e.getKey(), e.getValue(), side))
			.collect(Collectors.toSet());
	}

	/**
	 * Fire exchanges for the same round have to be merged.
	 */
	private static void addMerged(List<FireExchange> exchanges, FireExchange current) {
		FireExchange previous = exchanges.isEmpty() ? null : exchanges.get(exchanges.size() - 1);

		if (previous != null && previous.getRound() == current.getRound()) {
			exchanges.set(exchanges.size() - 1,
					new FireExchange(current.getRound(), previous.getLostHitPoints() + current.getLostHitPoints(),
							current.getDamage(), current.getShipCount()));
		}
		else {
			exchanges.add(current);
		}
	}

	static class SpaceCombatSummary {

		final Outcome outcome;

		final int fireExchangeCount;

		final Map<ShipDesign, List<FireExchange>> attackerFireExchanges;

		final Map<ShipDesign, List<FireExchange>> defenderFireExchanges;

		private SpaceCombatSummary(Outcome outcome, int fireExchangeCount,
				Map<ShipDesign, List<FireExchange>> attackerFireExchanges,
				Map<ShipDesign, List<FireExchange>> defenderFireExchanges) {
			this.outcome = outcome;
			this.fireExchangeCount = fireExchangeCount;
			this.attackerFireExchanges = attackerFireExchanges;
			this.defenderFireExchanges = defenderFireExchanges;
		}

	}

}
