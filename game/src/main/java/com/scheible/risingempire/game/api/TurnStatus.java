package com.scheible.risingempire.game.api;

import java.util.Collections;
import java.util.Map;

import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * @author sj
 */
public record TurnStatus(Map<Player, Boolean> playerStatus, boolean roundFinished) {

	public TurnStatus {
		playerStatus = Collections.unmodifiableMap(playerStatus);
	}
}
