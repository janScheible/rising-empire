package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import static java.util.Collections.emptyList;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.ActionHttpMethod;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author sj
 */
@FrontendController
class MainPageController {

	private static final Logger logger = LoggerFactory.getLogger(MainPageController.class);

	private final GameHolder gameHolder;
	private final GameManager gameManager;

	MainPageController(final GameHolder gameHolder, final GameManager gameManager) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
	}

	static class FinishTurnBodyDto {

		SystemId selectedStarId;
		int round;
	}

	@PostMapping(path = "/main-page/button-bar/finished-turns", consumes = APPLICATION_JSON_VALUE)
	@SuppressFBWarnings(value = "SPRING_FILE_DISCLOSURE", justification = "Controlled redirect... ;-)")
	ResponseEntity<Void> finishTurn(@ModelAttribute final FrontendContext context,
			@RequestBody final FinishTurnBodyDto body) {
		if (context.getGameView().getRound() != body.round) {
			return ResponseEntity.status(HttpStatus.GONE).build();
		}

		final TurnStatus turnStatus = context.getPlayerGame().finishTurn();
		gameManager.turnFinished(context.getGameId(), context.getPlayer(), turnStatus);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page").with("turnFinishedRound", body.round).toGetUri())
				.build();
	}

	static class ColonizeSystemBodyDto {

		SystemId selectedStarId;
		FleetId fleetId;
		Optional<List<String>> notificationSystemId = Optional.empty();
	}

	@PostMapping(path = "/main-page/inspector/colonizations", consumes = APPLICATION_JSON_VALUE)
	@SuppressFBWarnings(value = "SPRING_FILE_DISCLOSURE", justification = "Controlled redirect... ;-)")
	ResponseEntity<Void> colonizeSystem(@ModelAttribute final FrontendContext context,
			@RequestBody final ColonizeSystemBodyDto body) {
		final FleetView fleet = context.getGameView().getFleet(body.fleetId);

		context.getPlayerGame().colonizeSystem(fleet.getId());

		return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION,
				context.withSelectedStar(body.selectedStarId).toAction(HttpMethod.GET, "main-page")
						.with(body.notificationSystemId.orElseGet(() -> emptyList()).stream()
								.map(nsId -> new ActionField("notificationSystemId", nsId)))
						.with(context.getGameView().getColonizableSystemIds().stream()
								.filter(cdId -> !fleet.getOrbiting().get().equals(cdId))
								.map(csId -> new ActionField("colonizableSystemId", csId.getValue())))
						.toGetUri())
				.build();
	}

	@PostMapping(path = "/main-page/inspector/deployments", consumes = APPLICATION_JSON_VALUE)
	@SuppressFBWarnings(value = "SPRING_FILE_DISCLOSURE", justification = "Controlled redirect... ;-)")
	ResponseEntity<Void> deployFleet(@ModelAttribute final FrontendContext context,
			@RequestBody final LinkedMultiValueMap<String, String> body) {
		final FleetView fleet = context.getGameView().getFleet(new FleetId(body.getFirst("selectedFleetId")));

		final Map<ShipTypeId, Integer> shipTypeIdsAndCounts = toShipTypesAndCounts(fleet.getShips(),
				body.toSingleValueMap()).entrySet().stream()
						.collect(Collectors.toMap(e -> e.getKey().getId(), Entry::getValue));

		context.getPlayerGame().deployFleet(fleet.getId(), new SystemId(body.getFirst("selectedStarId")),
				shipTypeIdsAndCounts);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(body.getFirst("selectedStarId")).toAction(HttpMethod.GET, "main-page")
								.with(body.containsKey("fields"), "fields", () -> body.getFirst("fields")).toGetUri())
				.build();
	}

	static class SystemSpendingsBodyDto {

		SystemId selectedStarId;
		Optional<String> fields = Optional.empty();

		Optional<String> locked = Optional.empty();

		Optional<Integer> ship = Optional.empty();
		Optional<Integer> defence = Optional.empty();
		Optional<Integer> industry = Optional.empty();
		Optional<Integer> ecology = Optional.empty();
		Optional<Integer> technology = Optional.empty();
	}

	@PostMapping(path = "/main-page/inspector/spendings", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> allocateSystemSpendings(@ModelAttribute final FrontendContext context,
			@RequestBody final SystemSpendingsBodyDto body) {
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(body.selectedStarId).toAction(HttpMethod.GET, "main-page")
								.with(body.fields.isPresent(), "fields", () -> body.fields.get())
								.with(body.locked.isPresent(), "lockedCategory", () -> body.locked.get()).toGetUri())
				.build();
	}

	static class NextShipTypeBodyDto {
		SystemId selectedStarId;
		Optional<String> fields = Optional.empty();

		ColonyId colonyId;

		Optional<String> locked = Optional.empty();
	}

	@PostMapping(path = "/main-page/inspector/ship-types", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> nextShipType(@ModelAttribute final FrontendContext context,
			@RequestBody final NextShipTypeBodyDto body) {
		context.getPlayerGame().nextShipType(body.colonyId);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(body.selectedStarId).toAction(HttpMethod.GET, "main-page")
								.with(body.fields.isPresent(), "fields", () -> body.fields.get())
								.with(body.locked.isPresent(), "lockedCategory", () -> body.locked.get()).toGetUri())
				.build();
	}

	@GetMapping("/main-page")
	@SuppressFBWarnings(value = "SPRING_FILE_DISCLOSURE", justification = "Controlled redirect... ;-)")
	ResponseEntity<EntityModel<MainPageDto>> mainPage(final HttpServletRequest request,
			@ModelAttribute final FrontendContext context, @RequestParam final Optional<String> selectedStarId,
			@RequestParam final Optional<String> selectedFleetId,
			@RequestParam(name = "spaceCombatSystemId") final Optional<List<String>> spaceCombatSystemIds,
			@RequestParam(name = "exploredSystemId") final Optional<List<String>> exploredSystemIds,
			@RequestParam(name = "colonizableSystemId") final Optional<List<String>> colonizableSystemIds,
			@RequestParam(name = "notificationSystemId") final Optional<List<String>> notificationSystemIds,
			@RequestParam final Optional<Integer> turnFinishedRound, @RequestParam final Optional<Boolean> newTurn,
			@RequestParam final Map<String, String> shipTypeIdsAndCounts,
			@RequestParam final Optional<String> lockedCategory, @RequestParam final Optional<String> fields) {
		if (context.isEmpty()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
					.header(HttpHeaders.LOCATION, context.toAction(HttpMethod.GET, "new-game-page").toGetUri()).build();
		}

		final Game game = gameHolder.get(context.getGameId()).orElseThrow();
		game.unregisterAi(context.getPlayer());

		final MainPageState state = MainPageState.fromParameters(selectedStarId, selectedFleetId, shipTypeIdsAndCounts,
				spaceCombatSystemIds, exploredSystemIds, colonizableSystemIds, notificationSystemIds, lockedCategory,
				turnFinishedRound.map(tfr -> tfr == context.getGameView().getRound()), newTurn.orElse(Boolean.FALSE),
				(fleetId, parameters) -> toShipTypesAndCounts(context.getGameView().getFleet(fleetId).getShips(),
						parameters),
				(fleetId, orbitingSystemId) -> context.getGameView().getFleet(fleetId).getOrbiting()
						.map(d -> d.equals(orbitingSystemId)).orElse(Boolean.FALSE),
				fleetId -> context.getGameView().getFleet(fleetId).isDeployable());

		if (logger.isDebugEnabled()) {
			logger.debug("player: {}, state: {}", context.getPlayer(), state.getClass().getSimpleName());
		}

		if (state.isInitState()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, context
					.withSelectedStar(context.getGameView().getHomeSystem().getId())
					.toAction(HttpMethod.GET, "main-page")
					.with(!context.getGameView().isOwnTurnFinished() ? getSteps(context.getGameView()) : Stream.empty())
					.with(context.getGameView().isOwnTurnFinished(), "turnFinishedRound",
							() -> context.getGameView().getRound())
					.toGetUri()).build();
		}

		final boolean selectTech = !context.getGameView().getSelectTechs().isEmpty();
		if (selectTech && !state.isTurnFinishedState() && !state.isFleetMovementState()
				&& spaceCombatSystemIds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
					.header(HttpHeaders.LOCATION,
							context.withSelectedStar(context.getSelectedStarId().orElseThrow().getValue())
									.toAction(HttpMethod.GET, "select-tech-page")
									.with(exploredSystemIds.orElseGet(() -> emptyList()).stream()
											.map(esId -> new ActionField("exploredSystemId", esId)))
									.with(colonizableSystemIds.orElseGet(() -> emptyList()).stream()
											.map(csId -> new ActionField("colonizableSystemId", csId)))
									.with(notificationSystemIds.orElseGet(() -> emptyList()).stream()
											.map(nsId -> new ActionField("notificationSystemId", nsId)))
									.toGetUri())
					.build();
		}

		return ResponseEntity.ok(MainPageDto
				.filterFields(MainPageDtoPopulator.populate(context, state, getSteps(context.getGameView())), fields)
				.with(new Action("_self", request.getRequestURI(), ActionHttpMethod.GET)
						.with(request.getParameterMap().entrySet().stream().filter(pv -> !"fields".equals(pv.getKey()))
								.flatMap(pv -> Stream.of(pv.getValue()).map(v -> new ActionField(pv.getKey(), v))))));
	}

	private static Stream<ActionField> getSteps(final GameView gameView) {
		final Set<ActionField> fields = Stream.concat(Stream.concat(Stream.concat(//
				gameView.getSpaceCombats().stream()
						.map(scv -> new ActionField("spaceCombatSystemId",
								scv.getSystemId().getValue() + "@" + scv.getOrder())),

				gameView.getJustExploredSystemIds().stream()
						.map(esId -> new ActionField("exploredSystemId", esId.getValue()))),

				gameView.getColonizableSystemIds().stream()
						.map(csId -> new ActionField("colonizableSystemId", csId.getValue()))),

				gameView.getSystemNotifications().stream().map(SystemNotificationView::getSystemId)
						.map(nsId -> new ActionField("notificationSystemId", nsId.getValue())))
				.collect(Collectors.toSet());

		if (!fields.isEmpty()) {
			return fields.stream();
		} else {
			return Stream.of(new ActionField("newTurn", Boolean.TRUE));
		}
	}

	private static Map<ShipTypeView, Integer> toShipTypesAndCounts(final Map<ShipTypeView, Integer> fleetShips,
			final Map<String, String> parameters) {
		return fleetShips.keySet().stream().filter(st -> parameters.containsKey(st.getId().getValue())).collect(
				Collectors.toMap(Function.identity(), st -> Integer.valueOf(parameters.get(st.getId().getValue()))));
	}
}
