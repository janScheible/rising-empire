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

	final Optional<List<ScenarioDto>> gameScenarios;

	NewGamePageDto(List<GalaxySize> galaxySizes, Optional<List<ScenarioDto>> gameScenarios) {
		this.galaxySizes = galaxySizes;
		this.gameScenarios = gameScenarios;
	}

	record ScenarioDto(String id, String name) {

	}

}
