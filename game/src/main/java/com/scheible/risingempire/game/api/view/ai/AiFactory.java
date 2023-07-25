package com.scheible.risingempire.game.api.view.ai;

import java.util.ServiceLoader;

/**
 *
 * @author sj
 */
public interface AiFactory {

	/*
	 * See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	class LazyInstanceHolder {

		private static final AiFactory INSTANCE = ServiceLoader.load(AiFactory.class).findFirst().get();
	}

	static AiFactory get() {
		return LazyInstanceHolder.INSTANCE;
	}

	Ai create();
}
