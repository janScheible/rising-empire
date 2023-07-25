package com.scheible.risingempire.webapp.adapter.frontend.dto;

import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public class TurnFinishedStatusPlayerDto {

	final String id;
	final String name;
	final PlayerDto playerColor;
	final boolean finished;

	TurnFinishedStatusPlayerDto(final String id, final String name, final PlayerDto player, final boolean finished) {
		this.id = id;
		this.name = name;
		this.playerColor = player;
		this.finished = finished;
	}

	public static TurnFinishedStatusPlayerDto fromPlayer(final Player player, final boolean finished) {
		return new TurnFinishedStatusPlayerDto(player.name().toLowerCase() + "-player",
				player.name().substring(0, 1).toUpperCase() + player.name().substring(1).toLowerCase(),
				PlayerDto.fromPlayer(player), finished);
	}
}
