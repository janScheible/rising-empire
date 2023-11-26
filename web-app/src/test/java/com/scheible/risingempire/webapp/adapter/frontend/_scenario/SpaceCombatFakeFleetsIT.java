package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp._hypermedia.HypermediaClient;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.FleetCondition.fleet;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.mainPageState;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SpaceCombatFakeFleetsIT extends AbstractMainPageIT {

	@Test
	void testFakeFleetForLostOne() throws Exception {
		startGameForBlue(GameFactory.get()
			.create(GameOptions.forTestGameScenario()
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
				Map.of(blueHomeFleet.getShipType("Colony Ship").getId(), 1));
		blueGame.deployFleet(blueHomeFleet.getId(), YELLOW_HOME_SYSTEM_ID,
				Map.of(blueHomeFleet.getShipType("Colony Ship").getId(), 1));

		HypermediaClient blueClient = createHypermediaClient(Player.BLUE);
		assertThat(blueClient).is(mainPageState("NewTurnState"));
		assertThat(extractFleets(blueClient)).hasSize(3).areExactly(3, fleet("blue", 60, 60));

		finishTurn(blueClient);
		assertThat(blueClient).is(mainPageState("FleetMovementState"));
		assertThat(extractFleets(blueClient)).hasSize(5)
			.areExactly(1, fleet("blue", 60, 60))
			.areExactly(1, fleet("blue", 220, 100))
			.areExactly(1, fleet("white", 220, 100))
			.areExactly(1, fleet("blue", 140, 340))
			.areExactly(1, fleet("yellow", 140, 340));

		beginNewTurn(blueClient);
		assertThat(blueClient).is(mainPageState("SpaceCombatSystemState"));
		assertThat(extractFleets(blueClient)).hasSize(5)
			.areExactly(1, fleet("blue", 60, 60))
			.areExactly(1, fleet("blue", 220, 100))
			.areExactly(1, fleet("white", 220, 100))
			.areExactly(1, fleet("blue", 140, 340))
			.areExactly(1, fleet("yellow", 140, 340));

		blueClient.submit("$.inspector.spaceCombat._actions[?(@.name=='continue')]");

		blueClient.submit("$._actions[?(@.name=='continue')]");
		assertThat(blueClient).is(mainPageState("SpaceCombatSystemState"));
		assertThat(extractFleets(blueClient)).hasSize(4)
			.areExactly(1, fleet("blue", 60, 60))
			.areExactly(1, fleet("blue", 220, 100))
			.areExactly(1, fleet("blue", 140, 340))
			.areExactly(1, anyOf(fleet("white", 220, 100), fleet("yellow", 140, 340)));

		blueClient.submit("$.inspector.spaceCombat._actions[?(@.name=='continue')]");

		blueClient.submit("$._actions[?(@.name=='continue')]");
		assertThat(blueClient).is(mainPageState("NewTurnState"));
		assertThat(extractFleets(blueClient)).hasSize(3)
			.areExactly(1, fleet("blue", 60, 60))
			.areExactly(1, fleet("blue", 220, 100))
			.areExactly(1, fleet("blue", 140, 340));
	}

}
