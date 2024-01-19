package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import java.util.Set;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp._hypermedia.HypermediaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.mainPageState;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.JsonAssertCondition.round;
import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.NotificationEventCondition.notification;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class FinishTurnIT extends AbstractMainPageIT {

	@BeforeEach
	void beforeEach() {
		startGameForBlue(GameFactory.get()
			.create(GameOptions.forTestGameScenario()
				// disable notifications
				.fakeSystemNotificationProvider((player, round) -> Set.of())));
	}

	@Test
	void nextTurnSinglePlayer() throws Exception {
		Game game = getGame();

		registerChannel(Player.BLUE);

		HypermediaClient blueClient = createHypermediaClient(Player.BLUE);

		assertThat(blueClient).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications();

		finishTurn(blueClient);
		assertThat(blueClient).is(mainPageState("StarInspectionState")).is(round(2));
		assertNotifications();

		unregisterChannel(Player.BLUE);
	}

	@Test
	void nextTurnThreePlayers() throws Exception {
		registerChannel(Player.BLUE);
		registerChannel(Player.WHITE);
		registerChannel(Player.YELLOW);

		HypermediaClient blueClient = createHypermediaClient(Player.BLUE);
		assertThat(blueClient).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications();

		HypermediaClient whiteClient = createHypermediaClient(Player.WHITE);
		assertThat(whiteClient).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications();

		HypermediaClient yellowClient = createHypermediaClient(Player.YELLOW);
		assertThat(yellowClient).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications();

		finishTurn(blueClient);
		assertThat(blueClient).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications();

		finishTurn(whiteClient);
		assertThat(whiteClient).is(mainPageState("StarInspectionState")).is(round(1));
		assertNotifications(notification(Player.BLUE, "turn-finish-status"));

		finishTurn(yellowClient);
		assertThat(yellowClient).is(mainPageState("StarInspectionState")).is(round(2));
		assertNotifications(notification(Player.BLUE, "turn-finished"), notification(Player.WHITE, "turn-finished"));

		unregisterChannel(Player.BLUE);
		unregisterChannel(Player.WHITE);
		unregisterChannel(Player.YELLOW);
	}

}
