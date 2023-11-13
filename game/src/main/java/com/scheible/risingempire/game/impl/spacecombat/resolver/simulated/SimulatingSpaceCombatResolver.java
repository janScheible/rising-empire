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

import static com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.CombatStack.Side.ATTACKER;
import static com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.CombatStack.Side.DEFENDER;

/**
 * @author sj
 */
public class SimulatingSpaceCombatResolver implements SpaceCombatResolver {

	static class SpaceCombatSummary {

		final Outcome outcome;

		final int fireExchangeCount;

		final Map<ShipDesign, List<FireExchange>> attackerFireExchanges;

		final Map<ShipDesign, List<FireExchange>> defenderFireExchanges;

		private SpaceCombatSummary(final Outcome outcome, final int fireExchangeCount,
				final Map<ShipDesign, List<FireExchange>> attackerFireExchanges,
				final Map<ShipDesign, List<FireExchange>> defenderFireExchanges) {
			this.outcome = outcome;
			this.fireExchangeCount = fireExchangeCount;
			this.attackerFireExchanges = attackerFireExchanges;
			this.defenderFireExchanges = defenderFireExchanges;
		}

	}

	@Override
	public SpaceCombat resolve(final SystemId systemId, final OrbitingFleet defending, final DeployedFleet attacking,
			final ShipDesignProvider shipDesignProvider) {
		final BiFunction<Map<DesignSlot, Integer>, Player, Map<ShipDesign, Integer>> toShipDesignCounts = (ships,
				player) -> ships.entrySet()
					.stream()
					.collect(Collectors.toMap(e -> shipDesignProvider.get(player, e.getKey()), Map.Entry::getValue));

		final SpaceCombatSummary summary = simulate(
				toShipDesignCounts.apply(attacking.getShips(), attacking.getPlayer()),
				toShipDesignCounts.apply(defending.getShips(), defending.getPlayer()));

		final BiFunction<ShipDesign, Player, DesignSlot> toDesginSlot = (design, player) -> {
			for (final DesignSlot slot : DesignSlot.values()) {
				if (shipDesignProvider.get(player, slot) == design) {
					return slot;
				}
			}
			throw new IllegalStateException("Unkown ship desgin!");
		};

		final BiFunction<Map<ShipDesign, List<FireExchange>>, Player, Map<DesignSlot, List<FireExchange>>> toDesignSlotsCombatantFireExchanges = (
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

	SpaceCombatSummary simulate(final Map<ShipDesign, Integer> attacker, final Map<ShipDesign, Integer> defender) {
		final Map<ShipDesign, List<FireExchange>> attackerFireExchanges = new HashMap<>();
		final Map<ShipDesign, List<FireExchange>> defenderFireExchanges = new HashMap<>();
		Outcome outcome = Outcome.ATTACKER_RETREATED;

		final Set<CombatStack> attackingStacks = toStack(attacker, ATTACKER);
		final Set<CombatStack> defendingStacks = toStack(defender, DEFENDER);

		final List<CombatStack> allStacksList = Stream.concat(attackingStacks.stream(), defendingStacks.stream())
			.sorted(Comparator.<CombatStack>comparingInt(s -> s.design.getInitiative() + s.design.getCombatSpeed())
				.reversed())
			.collect(Collectors.toList());

		int fireExchangeCount = 0;
		for (int round = 0; round < 50; round++) {
			for (final CombatStack attackingStack : allStacksList) {
				if (attackingStack.isDestroyed()) {
					continue;
				}

				final List<CombatStack> otherSideStacks = (attackingStack.side == ATTACKER ? defendingStacks
						: attackingStacks)
					.stream()
					.filter(s -> !s.isDestroyed())
					.collect(Collectors.toList());

				if (otherSideStacks.isEmpty()) {
					outcome = attackingStack.side == ATTACKER ? Outcome.ATTACKER_WON : Outcome.DEFENDER_WON;
				}
				else {
					final CombatStack defendingStack = Lists2.getRandomElement(otherSideStacks);
					final int totalLostHitPoints = fireWeapons(attackingStack, defendingStack);

					if (totalLostHitPoints > 0) {
						/*
						 * System.out.println(round + ": " + totalLostHitPoints +
						 * " damage on " + defendingStack.side + " " +
						 * defendingStack.design.getName() + " (" + defendingStack.count +
						 * "/" + defendingStack.previousCount + " ships)");
						 */

						addMerged(
								(defendingStack.side == ATTACKER ? attackerFireExchanges : defenderFireExchanges)
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

	private int fireWeapons(final CombatStack attackingStack, final CombatStack defendingStack) {
		int totalLostHitPoints = 0;

		for (final WeaponSlot weaponSlot : attackingStack.design.getWeaponSlots()) {
			final AbstractWeapon weapon = weaponSlot.getWeapon();
			for (int weaponIndex = 0; weaponIndex < weaponSlot.getCount(); weaponIndex++) {

				boolean missilesLeft = false;
				if (weapon instanceof Missile) {
					final Missile missile = (Missile) weapon;
					final int shots = attackingStack.missileLaunches.getOrDefault(missile, 0);
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

	private static Set<CombatStack> toStack(final Map<ShipDesign, Integer> fleet, final Side side) {
		return fleet.entrySet()
			.stream()
			.map(e -> new CombatStack(e.getKey(), e.getValue(), side))
			.collect(Collectors.toSet());
	}

	/**
	 * Fire exchanges for the same round have to be merged.
	 */
	private static void addMerged(final List<FireExchange> exchanges, final FireExchange current) {
		final FireExchange previous = exchanges.isEmpty() ? null : exchanges.get(exchanges.size() - 1);

		if (previous != null && previous.getRound() == current.getRound()) {
			exchanges.set(exchanges.size() - 1,
					new FireExchange(current.getRound(), previous.getLostHitPoints() + current.getLostHitPoints(),
							current.getDamage(), current.getShipCount()));
		}
		else {
			exchanges.add(current);
		}
	}

}
