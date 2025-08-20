package com.scheible.risingempire.game.impl2.ai;

import com.scheible.risingempire.game.api.ai.Ai;
import com.scheible.risingempire.game.api.ai.AiFactory;

/**
 * @author sj
 */
public class AiFactoryImpl implements AiFactory {

	@Override
	public Ai create() {
		return new SimpleAi();
	}

}
