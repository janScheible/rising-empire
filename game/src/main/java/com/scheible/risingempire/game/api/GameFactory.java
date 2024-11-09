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

	Game load(Object whatEver);

	/*
	 * See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	class LazyInstanceHolder {

		private static final GameFactory INSTANCE = new GameFactory() {

			private static final ServiceLoader<GameFactory> GAME_FACTORIES = ServiceLoader.load(GameFactory.class);

			@Override
			public Game create(GameOptions gameOptions) {
				return GAME_FACTORIES.stream()
					.filter(service -> service.type().getPackageName().contains(".impl2.") == gameOptions.game2())
					.findFirst()
					.orElseThrow()
					.get()
					.create(gameOptions);
			}

			@Override
			public Game load(Object whatEver) {
				// the passed input has to be inspected if game2 or not
				throw new UnsupportedOperationException("Not supported yet.");
			}

		};

	}

}
