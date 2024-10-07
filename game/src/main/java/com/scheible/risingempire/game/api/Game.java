package com.scheible.risingempire.game.api;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;

/**
 * @author sj
 */
public interface Game {

	PlayerGame forPlayer(Player player);

	Set<Player> players();

	void registerAi(Player player);

	boolean aiControlled(Player player);

	void unregisterAi(Player player);

	/**
	 * The round of the game. It is incremented every time all players finished theirs
	 * turns.
	 */
	int round();

}
