package com.scheible.risingempire.webapp.adapter.frontend.newgamepage;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.GalaxySize;

/**
 * @author sj
 */
class NewGamePageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final List<GalaxySize> galaxySizes;

	NewGamePageDto(List<GalaxySize> galaxySizes) {
		this.galaxySizes = galaxySizes;
	}

}
