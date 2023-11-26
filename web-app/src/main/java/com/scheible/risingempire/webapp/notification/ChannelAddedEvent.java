package com.scheible.risingempire.webapp.notification;

import com.scheible.risingempire.game.api.view.universe.Player;
import org.springframework.context.ApplicationEvent;

/**
 *
 */
public class ChannelAddedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final Player player;

	private final String gameId;

	public ChannelAddedEvent(Object source, Player player, String gameId) {
		super(source);

		this.player = player;
		this.gameId = gameId;
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getGameId() {
		return this.gameId;
	}

}
