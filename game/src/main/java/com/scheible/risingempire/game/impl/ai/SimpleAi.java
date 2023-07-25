package com.scheible.risingempire.game.impl.ai;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.ai.Ai;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;

/**
 *
 * @author sj
 */
public class SimpleAi implements Ai {

	@Override
	public void finishTurn(final PlayerGame gameView) {
		final GameView gameState = gameView.getView();

		if (!gameState.getSelectTechs().isEmpty()) {
			for (final TechGroupView techGroup : gameState.getSelectTechs()) {
				gameView.selectTech(techGroup.iterator().next().getId());
			}
		}
	}
}