package com.scheible.risingempire.webapp.adapter.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * @author sj
 */
public enum PlayerDto {

	@JsonProperty("blue")
	BLUE,

	@JsonProperty("green")
	GREEN,

	@JsonProperty("purple")
	PURPLE,

	@JsonProperty("red")
	RED,

	@JsonProperty("white")
	WHITE,

	@JsonProperty("yellow")
	YELLOW;

	public static PlayerDto fromPlayer(final Player player) {
		return PlayerDto.valueOf(player.name());
	}

}
