package com.scheible.risingempire.game.impl.spacecombat;

import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

/**
 *
 * @author sj
 */
public class SpaceCombat {

	private final SystemId systemId;

	private final int fireExchangeCount;

	private final Player attacker;
	private final FleetBeforeArrival attackerFleet;
	private final Map<DesignSlot, Integer> attackerShipCounts;
	private final Map<DesignSlot, List<FireExchange>> attackerFireExchanges;

	private final Player defender;
	private final FleetId defenderFleet;
	private final Map<DesignSlot, Integer> defenderShipCounts;
	private final Map<DesignSlot, List<FireExchange>> defenderFireExchanges;

	private final Outcome outcome;

	private final Integer order;

	private SpaceCombat(final SystemId systemId, final int fireExchangeCount, final Player attacker,
			final FleetBeforeArrival attackerFleet, final Map<DesignSlot, Integer> attackerShipCounts,
			final Map<DesignSlot, List<FireExchange>> attackerFireExchanges, final Player defender,
			final FleetId defenderFleet, final Map<DesignSlot, Integer> defenderShipCounts,
			final Map<DesignSlot, List<FireExchange>> defenderFireExchanges, final Outcome outcome,
			final Integer order) {
		this.systemId = systemId;

		this.fireExchangeCount = fireExchangeCount;

		this.attacker = attacker;
		this.attackerFleet = attackerFleet;
		this.attackerShipCounts = unmodifiableMap(attackerShipCounts);
		this.attackerFireExchanges = unmodifiableMap(attackerFireExchanges);

		this.defender = defender;
		this.defenderFleet = defenderFleet;
		this.defenderShipCounts = unmodifiableMap(defenderShipCounts);
		this.defenderFireExchanges = unmodifiableMap(defenderFireExchanges);

		this.outcome = outcome;

		this.order = order;
	}

	public SpaceCombat(final SystemId systemId, final int fireExchangeCount, final Player attacker,
			final FleetBeforeArrival attackerFleet, final Map<DesignSlot, Integer> attackerShipCounts,
			final Map<DesignSlot, List<FireExchange>> attackerFireExchanges, final Player defender,
			final FleetId defenderFleet, final Map<DesignSlot, Integer> defenderShipCounts,
			final Map<DesignSlot, List<FireExchange>> defenderFireExchanges, final Outcome outcome) {
		this(systemId, fireExchangeCount, attacker, attackerFleet, attackerShipCounts, attackerFireExchanges, defender,
				defenderFleet, defenderShipCounts, defenderFireExchanges, outcome, null);
	}

	public static SpaceCombat withOrder(final SpaceCombat spaceCombat, final int order) {
		return new SpaceCombat(spaceCombat.getSystemId(), spaceCombat.getFireExchangeCount(), spaceCombat.getAttacker(),
				spaceCombat.getAttackerFleet(), spaceCombat.getAttackerShipCounts(),
				spaceCombat.getAttackerFireExchanges(), spaceCombat.getDefender(), spaceCombat.getDefenderFleet(),
				spaceCombat.getDefenderShipCounts(), spaceCombat.getDefenderFireExchanges(), spaceCombat.getOutcome(),
				order);
	}

	public SystemId getSystemId() {
		return systemId;
	}

	public int getFireExchangeCount() {
		return fireExchangeCount;
	}

	public Player getAttacker() {
		return attacker;
	}

	public FleetBeforeArrival getAttackerFleet() {
		return attackerFleet;
	}

	public Map<DesignSlot, Integer> getAttackerShipCounts() {
		return attackerShipCounts;
	}

	public Map<DesignSlot, List<FireExchange>> getAttackerFireExchanges() {
		return attackerFireExchanges;
	}

	public Player getDefender() {
		return defender;
	}

	public FleetId getDefenderFleet() {
		return defenderFleet;
	}

	public Map<DesignSlot, Integer> getDefenderShipCounts() {
		return defenderShipCounts;
	}

	public Map<DesignSlot, List<FireExchange>> getDefenderFireExchanges() {
		return defenderFireExchanges;
	}

	public Outcome getOutcome() {
		return outcome;
	}

	public int getOrder() {
		return order;
	}
}
