package com.scheible.risingempire.game.impl2.ai;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.ai.Ai;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;

/**
 * @author sj
 */
public class SimpleAi implements Ai {

	@Override
	public void finishTurn(PlayerGame game) {
		GameView view = game.view();

		if (!view.selectTechGroups().isEmpty()) {
			for (TechGroupView techGroup : view.selectTechGroups()) {
				game.selectTech(techGroup.iterator().next().id());
			}
		}
	}

}
