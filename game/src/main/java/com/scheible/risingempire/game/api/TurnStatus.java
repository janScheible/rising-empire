package com.scheible.risingempire.game.api;

import java.util.Collections;
import java.util.Map;

import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public class TurnStatus {

	private final Map<Player, Boolean> playerStatus;
	private final boolean turnFinished;

	public TurnStatus(final Map<Player, Boolean> playerStatus, final boolean turnFinished) {
		this.playerStatus = Collections.unmodifiableMap(playerStatus);
		this.turnFinished = turnFinished;
	}

	public Map<Player, Boolean> getPlayerStatus() {
		return playerStatus;
	}

	public boolean wasTurnFinished() {
		return turnFinished;
	}
}
