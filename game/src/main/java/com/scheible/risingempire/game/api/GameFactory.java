package com.scheible.risingempire.game.api;

import java.util.ServiceLoader;

/**
 * Factory for obtaining game instances.
 *
 * @author sj
 */
public interface GameFactory {

	static GameFactory get() {
		return LazyInstanceHolder.INSTANCE;
	}

	Game create(GameOptions gameOptions);

	Game load(Savegame savegame);

	/*
	 * See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	class LazyInstanceHolder {

		private static final GameFactory INSTANCE = new GameFactory() {

			private static final ServiceLoader<GameFactory> GAME_FACTORIES = ServiceLoader.load(GameFactory.class);

			@Override
			public Game create(GameOptions gameOptions) {
				return gameFactory().create(gameOptions);
			}

			@Override
			public Game load(Savegame savegame) {
				return gameFactory().load(savegame);
			}

			private static GameFactory gameFactory() {
				return GAME_FACTORIES.stream().findFirst().orElseThrow().get();
			}

		};

	}

	interface Savegame {

		GameOptions gameOptions();

	}

}
