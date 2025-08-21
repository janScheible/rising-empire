package com.scheible.risingempire.webapp.adapter.frontend.newgamepage;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.GalaxySize;

/**
 * @author sj
 */
class NewGamePageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final List<GalaxySize> galaxySizes;

	final int maxPlayerCount;

	final boolean testGame;

	final Optional<List<ScenarioDto>> gameScenarios;

	NewGamePageDto(List<GalaxySize> galaxySizes, int maxPlayerCount, boolean testGame,
			Optional<List<ScenarioDto>> gameScenarios) {
		this.galaxySizes = galaxySizes;
		this.maxPlayerCount = maxPlayerCount;
		this.testGame = testGame;
		this.gameScenarios = gameScenarios;
	}

	record ScenarioDto(String id, String name) {

	}

}
