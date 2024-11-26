package com.scheible.risingempire.game.api.view.system;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.system.SystemViewBuilder.IdStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SystemView(SystemId id, boolean justExplored, Location location, StarType starType, boolean small,
		boolean homeSystem, Optional<Integer> closestColony, Optional<PlanetType> planetType,
		Optional<PlanetSpecial> planetSpecial, Optional<Integer> seenInTurn, Optional<String> starName,
		Optional<Integer> planetMaxPopulation, Optional<ColonyView> colony, Optional<Integer> fleetRange,
		Optional<Integer> extendedFleetRange, Optional<Integer> scannerRange, boolean colonizable,
		boolean colonizeCommand, Set<SystemNotificationView> notifications) {

	public static IdStage builder() {
		return SystemViewBuilder.builder();
	}

	public Optional<ColonyView> colony(Player player) {
		return this.colony.filter(c -> c.player().equals(player));
	}

}
