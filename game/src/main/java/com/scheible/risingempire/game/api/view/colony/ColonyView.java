package com.scheible.risingempire.game.api.view.colony;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.colony.ColonyViewBuilder.IdStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ColonyView(ColonyId id, Player player, Race race, int population, boolean outdated,
		Optional<SpaceDockView> spaceDock, Optional<Map<ProductionArea, AllocationView>> allocations,
		Optional<AnnexationStatusView> annexationStatus, int maxTransferPopulation,
		Optional<ColonistTransferView> colonistTransfer, Optional<ColonyId> relocationTarget) {

	public ColonyView {
		allocations = allocations.map(ap -> ap.isEmpty() ? ap : new EnumMap<>(ap)).map(Collections::unmodifiableMap);
	}

	public static IdStage builder() {
		return ColonyViewBuilder.builder();
	}

}
