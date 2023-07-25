package com.scheible.risingempire.game.api;

import java.util.Set;

import com.scheible.risingempire.game.api.view.universe.Player;

/**
 * 
 * @author sj
 */
public interface Game {

	PlayerGame forPlayer(Player player);

	Set<Player> getPlayers();

	void registerAi(Player player);

	boolean isAiControlled(Player player);

	void unregisterAi(Player player);
}
