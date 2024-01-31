package com.scheible.risingempire.game.api.ai;

import java.util.ServiceLoader;

/**
 * @author sj
 */
public interface AiFactory {

	static AiFactory get() {
		return LazyInstanceHolder.INSTANCE;
	}

	Ai create();

	/*
	 * See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	class LazyInstanceHolder {

		private static final AiFactory INSTANCE = ServiceLoader.load(AiFactory.class).findFirst().get();

	}

}
