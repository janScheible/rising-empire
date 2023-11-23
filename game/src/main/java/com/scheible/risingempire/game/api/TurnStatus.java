package com.scheible.risingempire.game.api;

import java.util.Map;

import com.scheible.risingempire.game.api.view.universe.Player;

import static java.util.Collections.unmodifiableMap;

/**
 * @author sj
 */
public record TurnStatus(Map<Player, Boolean> playerStatus, boolean roundFinished) {

	public TurnStatus {
		playerStatus = unmodifiableMap(playerStatus);
	}
}
