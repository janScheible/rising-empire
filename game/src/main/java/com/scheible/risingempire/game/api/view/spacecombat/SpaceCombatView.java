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

	public SpaceCombatView(final SystemId systemId, final int order, final int fireExchangeCount, final Race attacker,
			final Player attackerPlayer, final FleetBeforeArrival attackerFleet,
			final List<CombatantShipSpecsView> attackerShipSpecs, final Race defender, final Player defenderPlayer,
			final FleetId defenderFleet, final List<CombatantShipSpecsView> defenderShipSpecs, final Outcome outcome) {
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
		return systemId;
	}

	public int getOrder() {
		return order;
	}

	public int getFireExchangeCount() {
		return fireExchangeCount;
	}

	public Race getAttacker() {
		return attacker;
	}

	public Player getAttackerPlayer() {
		return attackerPlayer;
	}

	public FleetBeforeArrival getAttackerFleet() {
		return attackerFleet;
	}

	public List<CombatantShipSpecsView> getAttackerShipSpecs() {
		return attackerShipSpecs;
	}

	public Race getDefender() {
		return defender;
	}

	public Player getDefenderPlayer() {
		return defenderPlayer;
	}

	public FleetId getDefenderFleet() {
		return defenderFleet;
	}

	public List<CombatantShipSpecsView> getDefenderShipSpecs() {
		return defenderShipSpecs;
	}

	public Outcome getOutcome() {
		return outcome;
	}

}
