package com.scheible.risingempire.webapp.adapter.frontend.dto;

import java.util.Locale;

import com.scheible.risingempire.game.api.universe.Player;

/**
 * @author sj
 */
public class TurnFinishedStatusPlayerDto {

	final String id;

	final String name;

	final PlayerDto playerColor;

	final boolean finished;

	private TurnFinishedStatusPlayerDto(String id, String name, PlayerDto player, boolean finished) {
		this.id = id;
		this.name = name;
		this.playerColor = player;
		this.finished = finished;
	}

	public static TurnFinishedStatusPlayerDto fromPlayer(Player player, boolean finished) {
		return new TurnFinishedStatusPlayerDto(player.name().toLowerCase(Locale.ROOT) + "-player",
				player.name().substring(0, 1).toUpperCase(Locale.ROOT)
						+ player.name().substring(1).toLowerCase(Locale.ROOT),
				PlayerDto.fromPlayer(player), finished);
	}

}
