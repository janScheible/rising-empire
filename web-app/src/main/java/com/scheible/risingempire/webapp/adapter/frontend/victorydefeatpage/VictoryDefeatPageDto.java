package com.scheible.risingempire.webapp.adapter.frontend.victorydefeatpage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author sj
 */
class VictoryDefeatPageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final boolean victory;

	VictoryDefeatPageDto(boolean victory) {
		this.victory = victory;
	}

}
