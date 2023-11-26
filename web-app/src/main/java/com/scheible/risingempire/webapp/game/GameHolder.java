package com.scheible.risingempire.webapp.game;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.scheible.risingempire.game.api.Game;

/**
 * @author sj
 */
public class GameHolder {

	private final Map<String, Game> games = new ConcurrentHashMap<>();

	void set(String gameId, Game game) {
		this.games.put(gameId, SynchronizedGameProxyFactory.getProxy(game));
	}

	public Optional<Game> get(String gameId) {
		return Optional.ofNullable(this.games.get(gameId));
	}

	public Set<String> getGameIds() {
		return this.games.keySet();
	}

	void removeGame(String gameId) {
		this.games.remove(gameId);
	}

}
