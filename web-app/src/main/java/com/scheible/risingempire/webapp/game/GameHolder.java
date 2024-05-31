package com.scheible.risingempire.webapp.game;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api._testgame.TestScenario;

/**
 * @author sj
 */
public class GameHolder {

	private final Map<String, GameWithTestScenario> games = new ConcurrentHashMap<>();

	void set(String gameId, Game game, Optional<TestScenario> testScenario) {
		this.games.put(gameId, new GameWithTestScenario(SynchronizedGameProxyFactory.getProxy(game), testScenario));
	}

	public Optional<Game> get(String gameId) {
		return Optional.ofNullable(this.games.get(gameId)).map(GameWithTestScenario::game);
	}

	public Optional<TestScenario> getTestScenario(String gameId) {
		return Optional.ofNullable(this.games.get(gameId)).flatMap(GameWithTestScenario::testScenario);
	}

	public Set<String> getGameIds() {
		return this.games.keySet();
	}

	void removeGame(String gameId) {
		this.games.remove(gameId);
	}

	private record GameWithTestScenario(Game game, Optional<TestScenario> testScenario) {

	}

}
