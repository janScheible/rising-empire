package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import java.util.Set;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.webapp._hypermedia.HypermediaClient;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.FleetCondition.fleet;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.mainPageState;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SpaceCombatFakeFleetsIT extends AbstractMainPageIT {

	@Test
	void testFakeFleetForLostOne() throws Exception {
		startGameForBlue(GameFactory.get()
			.create(GameOptions.forTestGame()
				// make the whole map reachable in a single turn for a simpler test setup
				.fleetRangeFactor(2000.0)
				.fleetSpeedFactor(2000.0)
				// make always blue win space combat
				.spaceCombatWinner(Outcome.ATTACKER_WON)
				// disable notifications
				.fakeSystemNotificationProvider((player, round) -> Set.of())));

		Game game = getGame();

		game.registerAi(Player.WHITE);
		game.registerAi(Player.YELLOW);

		PlayerGame blueGame = game.forPlayer(Player.BLUE);
		GameView blueGameView = blueGame.getView();

		SystemView blueHomeSystem = blueGameView.getHomeSystem();
		FleetView blueHomeFleet = blueGameView.getFleets()
			.stream()
			.filter(f -> blueHomeSystem.getId().equals(f.getOrbiting().orElse(null)))
			.findFirst()
			.orElseThrow();

		blueGame.deployFleet(blueHomeFleet.getId(), WHITE_HOME_SYSTEM_ID,
				blueHomeFleet.getShips().getPartByName("Colony Ship", 1));
		blueGame.deployFleet(blueHomeFleet.getId(), YELLOW_HOME_SYSTEM_ID,
				blueHomeFleet.getShips().getPartByName("Colony Ship", 1));

		HypermediaClient blueClient = createHypermediaClient(Player.BLUE);
		assertThat(blueClient).is(mainPageState("StarInspectionState"));
		assertThat(extractFleets(blueClient)).hasSize(3).areExactly(3, fleet("blue", 60, 60, false));

		finishTurn(blueClient);
		assertThat(blueClient).is(mainPageState("StarInspectionState"));
		// also the destroyed fleets of the (new) space combats have to be considered
		assertThat(extractFleets(blueClient)).hasSize(5)
			.areExactly(1, fleet("blue", 60, 60, false))
			.areExactly(1, fleet("blue", 220, 100, false))
			.areExactly(1, fleet("white", 220, 100, true))
			.areExactly(1, fleet("blue", 140, 340, false))
			.areExactly(1, fleet("yellow", 140, 340, true));
	}

}
