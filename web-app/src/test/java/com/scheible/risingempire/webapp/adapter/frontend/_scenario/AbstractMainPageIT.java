package com.scheible.risingempire.webapp.adapter.frontend._scenario;

import static java.util.Objects.requireNonNull;

import static com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.NotificationEventCondition.notification;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;
import org.assertj.core.description.TextDescription;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp._hypermedia.HypermediaClient;
import com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT.GameHolderConfiguration;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.notification.NotificationChannel;
import com.scheible.risingempire.webapp.notification.NotificationService;

import net.minidev.json.JSONObject;

/**
 * Base class for main page tests with MockMvc. <code>SwappableGame</code> is used to control the game bean.
 * It can be either swap with a Mockito mock or a real game.
 * 
 * @author sj
 */
@WebMvcTest(properties = { "spring.main.banner-mode=off", "spring.test.mockmvc.print=none" })
@Import({ NotificationService.class, GameHolderConfiguration.class, GameManager.class })
@TestPropertySource(properties = { "logging.level.com.scheible.risingempire.webapp._hypermedia.HypermediaClient=TRACE",
		"com.scheible.risingempire.webapp.adapter.frontend._scenario.AbstractMainPageIT=INFO" })
abstract class AbstractMainPageIT {

	record NotificationEvent(Player player, String type) {

	}

	static class NotificationEventCondition extends Condition<NotificationEvent> {

		private final Player player;
		private final String type;

		NotificationEventCondition(final Player player, final String type) {
			this.player = requireNonNull(player);
			this.type = requireNonNull(type);
		}

		static NotificationEventCondition notification(final Player player, final String type) {
			return new NotificationEventCondition(player, type);
		}

		@Override
		public boolean matches(final NotificationEvent event) {
			return event.player() == player && event.type().equals(type);
		}

		@Override
		public Description description() {
			return new TextDescription("notification for %s of type '%s'", player, type);
		}

		String getType() {
			return type;
		}

		Player getPlayer() {
			return player;
		}
	}

	static class MainPageAssert extends AbstractAssert<MainPageAssert, HypermediaClient> {

		MainPageAssert(HypermediaClient actual) {
			super(actual, MainPageAssert.class);
		}

		static MainPageAssert assertThat(HypermediaClient actual) {
			return new MainPageAssert(actual);
		}

		MainPageAssert isNewTurn() {
			isNotNull();
			if (!internalIsNewTurn()) {
				failWithMessage("Expected a page with a query parameter newTurn=true");
			}
			return this;
		}

		MainPageAssert isNotNewTurn() {
			isNotNull();
			if (internalIsNewTurn()) {
				failWithMessage("Expected a page without a query parameter newTurn=true");
			}
			return this;
		}

		private boolean internalIsNewTurn() {
			return Boolean.parseBoolean(actual.getPage().getRequest().getParameter("newTurn"));
		}

		MainPageAssert is2xxSuccessful() {
			isNotNull();
			if (actual.getPage().getResponse().getStatus() < 200 || actual.getPage().getResponse().getStatus() > 299) {
				failWithMessage("Expected the response status to be 2xx but was %d",
						actual.getPage().getResponse().getStatus());
			}
			return this;
		}
	}

	static class JsonAssertCondition extends Condition<HypermediaClient> {

		private final String json;
		private final Optional<String> logJsonPath;

		public JsonAssertCondition(final String json, final Optional<String> logJsonPath) {
			this.json = json;
			this.logJsonPath = logJsonPath;
		}

		@Override
		public boolean matches(final HypermediaClient client) {
			try {
				assertEquals(json, client.getPageContentAsString(), false);
				if (logJsonPath.isPresent()) {
					logger.info(JsonPath.parse(client.getPageContentAsString()).read(logJsonPath.get()).toString());
				}
			} catch (UnsupportedEncodingException | JSONException | AssertionError ex) {
				return false;
			}

			return true;
		}

		@Override
		public Description description() {
			return new TextDescription("matching JSON of '%s'", json);
		}

		static JsonAssertCondition miniMap(final boolean miniMap) {
			return new JsonAssertCondition("{ starMap: { miniMap: " + miniMap + " } }", Optional.empty());
		}

		static JsonAssertCondition mainPageState(final String stateName) {
			return new JsonAssertCondition("{ stateDescription: { stateName: \"" + stateName + "\" } }",
					Optional.of("$.stateDescription"));
		}

		static JsonAssertCondition round(final int round) {
			return new JsonAssertCondition("{ round: " + round + " }", Optional.empty());
		}
	}

	static class MainPageFleet {

		final String id;
		final String player;
		final int x;
		final int y;

		MainPageFleet(final String id, final String player, final int x, final int y) {
			this.id = id;
			this.player = player;
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, player, x, y);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof MainPageFleet) {
				MainPageFleet other = (MainPageFleet) obj;
				return Objects.equals(other.id, id) && Objects.equals(other.player, player)
						&& Objects.equals(other.x, x) && Objects.equals(other.y, y);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return id + "@" + player + " at (" + x + "," + y + ")";
		}
	}

	static class FleetCondition extends Condition<MainPageFleet> {

		final String player;
		final int x;
		final int y;

		FleetCondition(final String player, final int x, final int y) {
			this.player = player;
			this.x = x;
			this.y = y;
		}

		static FleetCondition fleet(final String player, final int x, final int y) {
			return new FleetCondition(player, x, y);
		}

		@Override
		public boolean matches(MainPageFleet fleet) {
			return fleet.player.equals(player) && fleet.x == x && fleet.y == y;
		}

		@Override
		public Description description() {
			return new TextDescription("being %s at (%d, %d)", player, x, y);
		}
	}

	@SuppressWarnings("overloads")
	static interface SwappableGame {

		void swap(Game game);
	}

	@TestConfiguration
	static class GameHolderConfiguration {

		@Bean
		GameHolder gameHolder() {
			return new GameHolder();
		}
	}

	private final String TEST_GAME_ID = "main-page-integration-test-game";

	final SystemId BLUE_HOME_SYSTEM_ID = new SystemId("s60x60");
	final SystemId WHITE_HOME_SYSTEM_ID = new SystemId("s220x100");
	final SystemId YELLOW_HOME_SYSTEM_ID = new SystemId("s140x340");

	private static final Logger logger = LoggerFactory.getLogger(AbstractMainPageIT.class);

	protected final Queue<NotificationEvent> notifications = new ConcurrentLinkedQueue<>();

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected NotificationService notificationService;

	@Autowired
	private GameHolder gameHolder;

	@Autowired
	private GameManager gameManager;

	protected void startGameForBlue(final Game game) {
		gameManager.startGame(TEST_GAME_ID, Player.BLUE, game);
	}

	protected Game getGame() {
		return gameHolder.get(TEST_GAME_ID).orElseThrow();
	}

	protected NotificationChannel registerChannel(final Player player) throws IOException {
		final NotificationChannel mockChannel = (String type, Map<String, Object> payload) -> {
			notifications.add(new NotificationEvent(player, type));
		};
		notificationService.registerChannel(TEST_GAME_ID, player, UUID.randomUUID().toString(), mockChannel);
		return mockChannel;
	}

	protected void unregisterChannel(final Player player) {
		notificationService.unregisterChannel(TEST_GAME_ID, player);
		notificationService.removePlayerSession(TEST_GAME_ID, player);
	}

	protected HypermediaClient createHypermediaClient(final Player player) throws Exception {
		return HypermediaClient.create("/game/games/" + TEST_GAME_ID + "/" + player + "/main-page",
				MediaType.APPLICATION_JSON, mockMvc);
	}

	protected HypermediaClient finishTurn(final SystemId selectedStar, final int round) throws Exception {
		return HypermediaClient.create(
				post("/game/games/" + TEST_GAME_ID + "/BLUE/main-page/button-bar/finished-turns")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JSONObject.toJSONString(
								Map.of("selectedStarId", Arrays.asList(selectedStar.getValue()), "round", round))),
				mockMvc);
	}

	protected MvcResult selectStar(final HypermediaClient client, final SystemId systemId) throws Exception {
		return selectStar(client, systemId.getValue());
	}

	protected MvcResult selectStar(final HypermediaClient client, final String starId) throws Exception {
		return client.submit("$.starMap.stars[?(@.id=='" + starId + "')]._actions[?(@.name=='select')]");
	}

	protected MvcResult finishTurn(final HypermediaClient client) throws Exception {
		return client.submit("$.buttonBar._actions[?(@.name=='finish-turn')]");
	}

	protected MvcResult fleetMovements(final HypermediaClient client) throws Exception {
		return client.submit("$._actions[?(@.name=='fleet-movements')]");
	}

	protected MvcResult beginNewTurn(final HypermediaClient client) throws Exception {
		return client.submit("$._actions[?(@.name=='begin-new-turn')]");
	}

	protected void assertNotifications(final FinishTurnIT.NotificationEventCondition... notifications) {
		for (final Player player : Player.values()) {
			for (final String type : new String[] { "next-turn", "turn-finish-status" }) {
				FinishTurnIT.NotificationEventCondition notification = null;

				for (final FinishTurnIT.NotificationEventCondition currentNotification : notifications) {
					if (currentNotification.getType().equals(type) && currentNotification.getPlayer() == player) {
						notification = currentNotification;
						break;
					}
				}

				if (notification != null) {
					assertThat(this.notifications).areExactly(1, notification);
				} else {
					assertThat(this.notifications).areExactly(0, notification(player, type));
				}
			}
		}

		this.notifications.clear();
	}

	@SuppressWarnings("unchecked")
	Set<MainPageFleet> extractFleets(final HypermediaClient blueClient) throws UnsupportedEncodingException {
		return ((List<Map<String, Object>>) JsonPath.parse(blueClient.getPageContentAsString()).read("$.starMap.fleets",
				List.class)).stream()
						.map(e -> new MainPageFleet(e.get("id").toString(), e.get("playerColor").toString(),
								(int) e.get("x"), (int) e.get("y")))
						.collect(Collectors.toSet());
	}
}
