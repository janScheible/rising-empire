package com.scheible.risingempire.game.impl.spacecombat;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

import static java.util.Collections.unmodifiableMap;

/**
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

	private SpaceCombat(SystemId systemId, int fireExchangeCount, Player attacker, FleetBeforeArrival attackerFleet,
			Map<DesignSlot, Integer> attackerShipCounts, Map<DesignSlot, List<FireExchange>> attackerFireExchanges,
			Player defender, FleetId defenderFleet, Map<DesignSlot, Integer> defenderShipCounts,
			Map<DesignSlot, List<FireExchange>> defenderFireExchanges, Outcome outcome, Integer order) {
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

	public SpaceCombat(SystemId systemId, int fireExchangeCount, Player attacker, FleetBeforeArrival attackerFleet,
			Map<DesignSlot, Integer> attackerShipCounts, Map<DesignSlot, List<FireExchange>> attackerFireExchanges,
			Player defender, FleetId defenderFleet, Map<DesignSlot, Integer> defenderShipCounts,
			Map<DesignSlot, List<FireExchange>> defenderFireExchanges, Outcome outcome) {
		this(systemId, fireExchangeCount, attacker, attackerFleet, attackerShipCounts, attackerFireExchanges, defender,
				defenderFleet, defenderShipCounts, defenderFireExchanges, outcome, null);
	}

	public static SpaceCombat withOrder(SpaceCombat spaceCombat, int order) {
		return new SpaceCombat(spaceCombat.getSystemId(), spaceCombat.getFireExchangeCount(), spaceCombat.getAttacker(),
				spaceCombat.getAttackerFleet(), spaceCombat.getAttackerShipCounts(),
				spaceCombat.getAttackerFireExchanges(), spaceCombat.getDefender(), spaceCombat.getDefenderFleet(),
				spaceCombat.getDefenderShipCounts(), spaceCombat.getDefenderFireExchanges(), spaceCombat.getOutcome(),
				order);
	}

	public SystemId getSystemId() {
		return this.systemId;
	}

	public int getFireExchangeCount() {
		return this.fireExchangeCount;
	}

	public Player getAttacker() {
		return this.attacker;
	}

	public FleetBeforeArrival getAttackerFleet() {
		return this.attackerFleet;
	}

	public Map<DesignSlot, Integer> getAttackerShipCounts() {
		return this.attackerShipCounts;
	}

	public Map<DesignSlot, List<FireExchange>> getAttackerFireExchanges() {
		return this.attackerFireExchanges;
	}

	public Player getDefender() {
		return this.defender;
	}

	public FleetId getDefenderFleet() {
		return this.defenderFleet;
	}

	public Map<DesignSlot, Integer> getDefenderShipCounts() {
		return this.defenderShipCounts;
	}

	public Map<DesignSlot, List<FireExchange>> getDefenderFireExchanges() {
		return this.defenderFireExchanges;
	}

	public Outcome getOutcome() {
		return this.outcome;
	}

	public int getOrder() {
		return this.order;
	}

}
