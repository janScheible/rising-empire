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

		private static final GameFactory INSTANCE = ServiceLoader.load(GameFactory.class).findFirst().get();

	}

}
