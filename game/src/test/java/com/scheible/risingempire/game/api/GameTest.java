package com.scheible.risingempire.game.api;

import java.util.ArrayList;
import java.util.List;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class GameTest {

	@Test
	void testSwitchToNextShipType() {
		Game game = GameFactory.get().create(GameOptions.forTestGame());
		PlayerGame blueGame = game.forPlayer(Player.BLUE);

		List<String> shipNames = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			GameView blueGameState = blueGame.getView();

			shipNames.add(blueGameState.getSystem("Sol").getColonyView().get().getSpaceDock().get().getName());
			blueGame.nextShipType(blueGameState.getSystem("Sol").getColonyView().get().getId());
		}

		assertThat(shipNames).containsExactly("Scout", "Colony Ship", "Fighter");
	}

}
