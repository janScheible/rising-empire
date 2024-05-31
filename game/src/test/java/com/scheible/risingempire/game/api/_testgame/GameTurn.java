package com.scheible.risingempire.game.api._testgame;

import java.util.function.BiConsumer;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.view.GameView;

/**
 * @author sj
 */
public interface GameTurn {

	GameTurn turn(BiConsumer<PlayerGame, GameView> turnLogic);

}
