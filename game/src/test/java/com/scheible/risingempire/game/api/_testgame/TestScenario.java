package com.scheible.risingempire.game.api._testgame;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;

/**
 * @author sj
 */
public interface TestScenario {

	static TestScenario start(Consumer<TestScenario> testMethod) {
		TestScenario testScenario = new TestScenarioImpl(new ArrayList<>());
		testMethod.accept(testScenario);
		return testScenario;
	}

	GameTurn customize(Consumer<GameOptions> optionsCustomizer);

	Game getGame();

	default GameView getWhiteView() {
		return getGame().forPlayer(Player.WHITE).getView();
	}

	void applyTurnLogic(Game game);

}
