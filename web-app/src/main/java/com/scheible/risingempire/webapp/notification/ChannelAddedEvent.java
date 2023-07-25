package com.scheible.risingempire.webapp.notification;

import org.springframework.context.ApplicationEvent;

import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 */
public class ChannelAddedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final Player player;
	private final String gameId;

	public ChannelAddedEvent(final Object source, final Player player, final String gameId) {
		super(source);

		this.player = player;
		this.gameId = gameId;
	}

	public Player getPlayer() {
		return player;
	}

	public String getGameId() {
		return gameId;
	}
}
