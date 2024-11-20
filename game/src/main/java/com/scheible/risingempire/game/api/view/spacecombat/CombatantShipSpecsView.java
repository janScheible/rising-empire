package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.List;
import java.util.Optional;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.spacecombat.CombatantShipSpecsViewBuilder.IdStage;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
@StagedRecordBuilder
public record CombatantShipSpecsView(ShipTypeId id, String name, int count, int previousCount, ShipSize size,
		Optional<Integer> shield, Optional<Integer> beamDefence, Optional<Integer> attackLevel, Optional<Integer> warp,
		Optional<Integer> missleDefence, Optional<Integer> hits, Optional<Integer> speed, List<String> equipment,
		List<FireExchangeView> fireExchanges) {

	public CombatantShipSpecsView {
		equipment = unmodifiableList(equipment);
		fireExchanges = unmodifiableList(fireExchanges);
	}

	public static IdStage builder() {
		return CombatantShipSpecsViewBuilder.builder();
	}

}
