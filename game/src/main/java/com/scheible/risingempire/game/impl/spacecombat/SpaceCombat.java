package com.scheible.risingempire.game.impl.spacecombat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

import static java.util.Collections.unmodifiableMap;

/**
 * @author sj
 */
public class SpaceCombat {

	private final SystemId systemId;

	private final int fireExchangeCount;

	private final Player attacker;

	private final FleetBeforeArrivalView attackerFleet;

	private final Map<DesignSlot, Integer> previousAttackerShipCounts;

	private final Map<DesignSlot, List<FireExchange>> attackerFireExchanges;

	private final Player defender;

	private final FleetId defenderFleet;

	private final Map<DesignSlot, Integer> previousDefenderShipCounts;

	private final Map<DesignSlot, List<FireExchange>> defenderFireExchanges;

	private final Outcome outcome;

	public SpaceCombat(SystemId systemId, int fireExchangeCount, Player attacker, FleetBeforeArrivalView attackerFleet,
			Map<DesignSlot, Integer> previousAttackerShipCounts,
			Map<DesignSlot, List<FireExchange>> attackerFireExchanges, Player defender, FleetId defenderFleet,
			Map<DesignSlot, Integer> previousDefenderShipCounts,
			Map<DesignSlot, List<FireExchange>> defenderFireExchanges, Outcome outcome) {
		this.systemId = systemId;

		this.fireExchangeCount = fireExchangeCount;

		this.attacker = attacker;
		this.attackerFleet = attackerFleet;
		this.previousAttackerShipCounts = unmodifiableMap(previousAttackerShipCounts);
		this.attackerFireExchanges = unmodifiableMap(attackerFireExchanges);

		this.defender = defender;
		this.defenderFleet = defenderFleet;
		this.previousDefenderShipCounts = unmodifiableMap(previousDefenderShipCounts);
		this.defenderFireExchanges = unmodifiableMap(defenderFireExchanges);

		this.outcome = outcome;
	}

	private Map<DesignSlot, Integer> getShipCounts(Map<DesignSlot, Integer> shipCounts,
			Map<DesignSlot, List<FireExchange>> fireExchanges) {
		return shipCounts.entrySet()
			.stream()
			.collect(Collectors.toMap(Entry::getKey, e -> getEitherFromLastFireExchangeOrPrevious(e, fireExchanges)));
	}

	private int getEitherFromLastFireExchangeOrPrevious(Entry<DesignSlot, Integer> shipCount,
			Map<DesignSlot, List<FireExchange>> fireExchanges) {
		return Optional.ofNullable(fireExchanges.get(shipCount.getKey()))
			.flatMap(fe -> Optional.ofNullable(fe.isEmpty() ? null : fe.getLast().getShipCount()))
			.orElse(shipCount.getValue());
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

	public FleetBeforeArrivalView getAttackerFleet() {
		return this.attackerFleet;
	}

	public Map<DesignSlot, Integer> getPreviousAttackerShipCounts() {
		return this.previousAttackerShipCounts;
	}

	public Map<DesignSlot, Integer> getAttackerShipCounts() {
		return getShipCounts(this.previousAttackerShipCounts, this.attackerFireExchanges);
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

	public Map<DesignSlot, Integer> getPreviousDefenderShipCounts() {
		return this.previousDefenderShipCounts;
	}

	public Map<DesignSlot, Integer> getDefenderShipCounts() {
		return getShipCounts(this.previousDefenderShipCounts, this.defenderFireExchanges);
	}

	public Map<DesignSlot, List<FireExchange>> getDefenderFireExchanges() {
		return this.defenderFireExchanges;
	}

	public Outcome getOutcome() {
		return this.outcome;
	}

}
