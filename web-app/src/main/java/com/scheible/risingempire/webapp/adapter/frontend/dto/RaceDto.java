package com.scheible.risingempire.webapp.adapter.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.view.universe.Race;

/**
 * @author sj
 */
public enum RaceDto {

	@JsonProperty("Human")
	HUMAN,

	@JsonProperty("Mrrshan")
	MRRSHAN,

	@JsonProperty("Psilon")
	PSILON;

	public static RaceDto fromRace(Race race) {
		return RaceDto.valueOf(race.name());
	}

}
