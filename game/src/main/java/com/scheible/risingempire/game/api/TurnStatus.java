package com.scheible.risingempire.game.api;

import java.util.Map;

import com.scheible.risingempire.game.api.view.universe.Player;
import java.util.Collections;

/**
 *
 * @author sj
 */
public record TurnStatus(Map<Player, Boolean> playerStatus, boolean roundFinished) {
	
	public TurnStatus {
		playerStatus = Collections.unmodifiableMap(playerStatus);
	}	
}
