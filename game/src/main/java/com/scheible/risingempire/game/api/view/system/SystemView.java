package com.scheible.risingempire.game.api.view.system;

import java.util.Optional;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;

/**
 * @author sj
 */
@StagedRecordBuilder
public record SystemView(SystemId id, boolean justExplored, Location location, StarType starType, boolean small,
		boolean homeSystem, Optional<Integer> range, Optional<PlanetType> planetType,
		Optional<PlanetSpecial> planetSpecial, Optional<Integer> seenInTurn, Optional<String> starName,
		Optional<Integer> planetMaxPopulation, Optional<ColonyView> colony, Optional<Integer> fleetRange,
		Optional<Integer> extendedFleetRange, Optional<Integer> scannerRange, Optional<Boolean> colonizable,
		Optional<Boolean> colonizeCommand) {

	public SystemView {
		if (Boolean.TRUE.equals(colonizable.orElse(Boolean.FALSE)) && colonizeCommand.isEmpty()) {
			throw new IllegalArgumentException("colonizationCommand can't be absent when canColonize = true!");
		}
	}

	public Optional<ColonyView> colony(Player player) {
		return this.colony.filter(c -> c.player().equals(player));
	}

}
