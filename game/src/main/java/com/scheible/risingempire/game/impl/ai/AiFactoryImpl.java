package com.scheible.risingempire.game.impl.ai;

import com.scheible.risingempire.game.api.view.ai.Ai;
import com.scheible.risingempire.game.api.view.ai.AiFactory;

/**
 *
 * @author sj
 */
public class AiFactoryImpl implements AiFactory {

	@Override
	public Ai create() {
		return new SimpleAi();
	}
}
