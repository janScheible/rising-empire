package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;

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

	private final Set<FleetBeforeArrival> attackerFleets;

	private final List<CombatantShipSpecs> attackerShipSpecs;

	private final Race defender;

	private final Player defenderPlayer;

	private final Optional<FleetId> defenderFleet;

	private final Set<FleetBeforeArrival> defenderFleetsBeforeArrival;

	private final List<CombatantShipSpecs> defenderShipSpecs;

	private final Outcome outcome;

	public SpaceCombatView(SystemId systemId, int order, int fireExchangeCount, Race attacker, Player attackerPlayer,
			Set<FleetBeforeArrival> attackerFleets, List<CombatantShipSpecs> attackerShipSpecs, Race defender,
			Player defenderPlayer, Optional<FleetId> defenderFleet, Set<FleetBeforeArrival> defenderFleetsBeforeArrival,
			List<CombatantShipSpecs> defenderShipSpecs, Outcome outcome) {
		this.systemId = systemId;

		this.order = order;

		this.fireExchangeCount = fireExchangeCount;

		this.attacker = attacker;
		this.attackerPlayer = attackerPlayer;
		this.attackerFleets = attackerFleets;
		this.attackerShipSpecs = unmodifiableList(attackerShipSpecs);

		this.defender = defender;
		this.defenderPlayer = defenderPlayer;
		this.defenderFleet = defenderFleet;
		this.defenderFleetsBeforeArrival = defenderFleetsBeforeArrival;
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

	public Set<FleetBeforeArrival> getAttackerFleets() {
		return this.attackerFleets;
	}

	public List<CombatantShipSpecs> getAttackerShipSpecs() {
		return this.attackerShipSpecs;
	}

	public Race getDefender() {
		return this.defender;
	}

	public Player getDefenderPlayer() {
		return this.defenderPlayer;
	}

	public Optional<FleetId> getDefenderFleet() {
		return this.defenderFleet;
	}

	public Set<FleetBeforeArrival> getDefenderFleetsBeforeArrival() {
		return this.defenderFleetsBeforeArrival;
	}

	public List<CombatantShipSpecs> getDefenderShipSpecs() {
		return this.defenderShipSpecs;
	}

	public Outcome getOutcome() {
		return this.outcome;
	}

}
