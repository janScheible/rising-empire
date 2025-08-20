package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatViewBuilder.SystemIdStage;
import com.scheible.risingempire.game.api.view.system.SystemId;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SpaceCombatView(SystemId systemId, String systemName, int fireExchangeCount, Race attacker,
		Player attackerPlayer, Set<FleetBeforeArrivalView> attackerFleets,
		List<CombatantShipSpecsView> attackerShipSpecs, Race defender, Player defenderPlayer,
		Optional<FleetId> destroyedDefenderFleet, Set<FleetBeforeArrivalView> defenderFleetsBeforeArrival,
		List<CombatantShipSpecsView> defenderShipSpecs, Outcome outcome) {

	public enum Outcome {

		ATTACKER_WON, ATTACKER_RETREATED, DEFENDER_WON;

	}

	public SpaceCombatView {
		attackerShipSpecs = unmodifiableList(attackerShipSpecs);
		defenderShipSpecs = unmodifiableList(defenderShipSpecs);
	}

	public static SystemIdStage builder() {
		return SpaceCombatViewBuilder.builder();
	}

}
