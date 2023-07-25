package com.scheible.risingempire.webapp.game;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.scheible.risingempire.game.api.Game;

/**
 *
 * @author sj
 */
public class GameHolder {

	private final Map<String, Game> games = new ConcurrentHashMap<>();

	void set(final String gameId, final Game game) {
		games.put(gameId, SynchronizedGameProxyFactory.getProxy(game));
	}

	public Optional<Game> get(final String gameId) {
		return Optional.ofNullable(games.get(gameId));
	}

	public Set<String> getGameIds() {
		return games.keySet();
	}

	void removeGame(final String gameId) {
		games.remove(gameId);
	}
}
