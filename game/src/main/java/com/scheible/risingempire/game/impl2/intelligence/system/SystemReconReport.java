package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.impl2.apiinternal.Population;

/**
 * @author sj
 */

public record SystemReconReport(boolean explored, Optional<ColonyReconReport> colonyReconReport) {

	public Optional<PlanetReconReport> planetReconReport(String starName, PlanetType planetType,
			PlanetSpecial planetSpecial, Population planetMapPopulation) {
		return this.explored
				? Optional.of(new PlanetReconReport(starName, planetType, planetSpecial, planetMapPopulation))
				: Optional.empty();
	}

	public record PlanetReconReport(String starName, PlanetType planetType, PlanetSpecial planetSpecial,
			Population planetMapPopulation) {

	}

	public record ColonyReconReport(Player player, Race race, Population population) {

	}

}