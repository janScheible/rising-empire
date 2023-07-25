package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import static java.util.Collections.emptySet;

import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.mainPageState;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.miniMap;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.round;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.NotificationEventCondition.notification;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp._hypermedia.HypermediaClient;

/**
 *
 * @author sj
 */
class FinishTurnIT extends AbstractMainPageIT {

	@BeforeEach
	void beforeEach() {
		startGameForBlue(GameFactory.get().create(GameOptions.forTestGameScenario()
				// disable notifications
				.fakeSystemNotificationProvider((player, round) -> emptySet())));
	}

	@Test
	void nextTurnSinglePlayer() throws Exception {
		final Game game = getGame();

		registerChannel(Player.BLUE);

		final HypermediaClient blueClient = createHypermediaClient(Player.BLUE);

		assertThat(blueClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(1));
		assertNotifications();

		selectStar(blueClient, WHITE_HOME_SYSTEM_ID);
		assertThat(blueClient).has(miniMap(false)).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications();

		finishTurn(blueClient);
		assertThat(blueClient).has(miniMap(true)).is(mainPageState("FleetMovementState")).is(round(2));
		assertNotifications();

		beginNewTurn(blueClient);
		assertThat(blueClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(2));
		assertNotifications();

		unregisterChannel(Player.BLUE);
	}

	@Test
	void nextTurnThreePlayers() throws Exception {
		registerChannel(Player.BLUE);
		registerChannel(Player.WHITE);
		registerChannel(Player.YELLOW);

		final HypermediaClient blueClient = createHypermediaClient(Player.BLUE);
		assertThat(blueClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(1));
		assertNotifications();

		final HypermediaClient whiteClient = createHypermediaClient(Player.WHITE);
		assertThat(whiteClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(1));
		assertNotifications();

		final HypermediaClient yellowClient = createHypermediaClient(Player.YELLOW);
		assertThat(yellowClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(1));
		assertNotifications();

		finishTurn(blueClient);
		assertThat(blueClient).has(miniMap(true)).is(mainPageState("TurnFinishedState")).is(round(1));
		assertNotifications();

		finishTurn(whiteClient);
		assertThat(whiteClient).has(miniMap(true)).is(mainPageState("TurnFinishedState")).is(round(1));
		assertNotifications(notification(Player.BLUE, "turn-finish-status"));

		finishTurn(yellowClient);
		assertThat(yellowClient).has(miniMap(true)).is(mainPageState("FleetMovementState")).is(round(2));
		assertNotifications(notification(Player.BLUE, "turn-finished"), notification(Player.WHITE, "turn-finished"));

		fleetMovements(blueClient);
		assertThat(blueClient).has(miniMap(true)).is(mainPageState("FleetMovementState")).is(round(2));
		assertNotifications();
		beginNewTurn(blueClient);
		assertThat(blueClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(2));
		assertNotifications();

		fleetMovements(whiteClient);
		assertThat(whiteClient).has(miniMap(true)).is(mainPageState("FleetMovementState")).is(round(2));
		assertNotifications();
		beginNewTurn(whiteClient);
		assertThat(whiteClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(2));
		assertNotifications();

		beginNewTurn(yellowClient);
		assertThat(yellowClient).has(miniMap(false)).is(mainPageState("NewTurnState")).is(round(2));
		assertNotifications();

		unregisterChannel(Player.BLUE);
		unregisterChannel(Player.WHITE);
		unregisterChannel(Player.YELLOW);
	}
}
