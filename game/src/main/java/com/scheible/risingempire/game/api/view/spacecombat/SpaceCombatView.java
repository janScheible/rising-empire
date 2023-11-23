package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.List;

import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
public class SpaceCombatView {

	public enum Outcome {

		ATTACKER_WON, ATTACKER_RETREATED, DEFENDER_WON;

	}

	private final SystemId systemId;

	private final int order;

	private final int fireExchangeCount;

	private final Race attacker;

	private final Player attackerPlayer;

	private final FleetBeforeArrival attackerFleet;

	private final List<CombatantShipSpecsView> attackerShipSpecs;

	private final Race defender;

	private final Player defenderPlayer;

	private final FleetId defenderFleet;

	private final List<CombatantShipSpecsView> defenderShipSpecs;

	private final Outcome outcome;

	public SpaceCombatView(SystemId systemId, int order, int fireExchangeCount, Race attacker, Player attackerPlayer,
			FleetBeforeArrival attackerFleet, List<CombatantShipSpecsView> attackerShipSpecs, Race defender,
			Player defenderPlayer, FleetId defenderFleet, List<CombatantShipSpecsView> defenderShipSpecs,
			Outcome outcome) {
		this.systemId = systemId;

		this.order = order;

		this.fireExchangeCount = fireExchangeCount;

		this.attacker = attacker;
		this.attackerPlayer = attackerPlayer;
		this.attackerFleet = attackerFleet;
		this.attackerShipSpecs = unmodifiableList(attackerShipSpecs);

		this.defender = defender;
		this.defenderPlayer = defenderPlayer;
		this.defenderFleet = defenderFleet;
		this.defenderShipSpecs = unmodifiableList(defenderShipSpecs);

		this.outcome = outcome;
	}

	public SystemId getSystemId() {
		return this.systemId;
	}

	public int getOrder() {
		return this.order;
	}

	public int getFireExchangeCount() {
		return this.fireExchangeCount;
	}

	public Race getAttacker() {
		return this.attacker;
	}

	public Player getAttackerPlayer() {
		return this.attackerPlayer;
	}

	public FleetBeforeArrival getAttackerFleet() {
		return this.attackerFleet;
	}

	public List<CombatantShipSpecsView> getAttackerShipSpecs() {
		return this.attackerShipSpecs;
	}

	public Race getDefender() {
		return this.defender;
	}

	public Player getDefenderPlayer() {
		return this.defenderPlayer;
	}

	public FleetId getDefenderFleet() {
		return this.defenderFleet;
	}

	public List<CombatantShipSpecsView> getDefenderShipSpecs() {
		return this.defenderShipSpecs;
	}

	public Outcome getOutcome() {
		return this.outcome;
	}

}
