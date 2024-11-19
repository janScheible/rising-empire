package com.scheible.risingempire.game.api.view.colony;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;

import static java.util.Collections.unmodifiableMap;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ColonyView(ColonyId id, Player player, Race race, int population, Optional<ShipTypeView> spaceDock,
		Optional<Map<ProductionArea, Integer>> ratios, Optional<AnnexationStatusView> annexationStatus,
		Map<ColonyId, Integer> colonistTransfers, Optional<ColonyId> relocationTarget) {

	public ColonyView {
		ratios = ratios.map(EnumMap::new).map(Collections::unmodifiableMap);
		colonistTransfers = unmodifiableMap(colonistTransfers);
	}

}
