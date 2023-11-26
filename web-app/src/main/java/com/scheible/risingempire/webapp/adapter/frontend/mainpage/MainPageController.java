package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.game.GameHolder;
import com.scheible.risingempire.webapp.game.GameManager;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.ActionHttpMethod;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sj
 */
@FrontendController
class MainPageController {

	private static final Logger logger = LoggerFactory.getLogger(MainPageController.class);

	private final GameHolder gameHolder;

	private final GameManager gameManager;

	MainPageController(GameHolder gameHolder, GameManager gameManager) {
		this.gameHolder = gameHolder;
		this.gameManager = gameManager;
	}

	@PostMapping(path = "/main-page/button-bar/finished-turns", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> finishTurn(@ModelAttribute FrontendContext context, @RequestBody FinishTurnBodyDto body) {
		if (context.getGameView().getRound() != body.round) {
			return ResponseEntity.status(HttpStatus.GONE).build();
		}

		TurnStatus turnStatus = context.getPlayerGame().finishTurn();
		this.gameManager.turnFinished(context.getGameId(), context.getPlayer(), turnStatus);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with("turnFinishedRound", body.round)
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/colonizations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> colonizeSystem(@ModelAttribute FrontendContext context,
			@RequestBody ColonizeSystemBodyDto body) {
		FleetView fleet = context.getGameView().getFleet(body.fleetId);
		SystemView systemToColonize = context.getGameView().getSystem(fleet.getOrbiting().get());
		context.getPlayerGame()
			.colonizeSystem(systemToColonize.getId(), fleet.getId(), body.skip.orElse(Boolean.FALSE));

		if (!body.skip.isPresent()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, context.withSelectedStar(body.selectedStarId)
					.toAction(HttpMethod.GET, "main-page")
					.with(body.colonizableSystemId.orElseGet(() -> List.of())
						.stream()
						.map(csId -> new ActionField("colonizableSystemId", csId)))
					.with(context.getGameView()
						.getAnnexableSystemIds()
						.stream()
						.map(asId -> new ActionField("annexableSystemId", asId.getValue())))
					.with(context.getGameView()
						.getSystemNotifications()
						.stream() // CPD-OFF
						.map(nsId -> new ActionField("notificationSystemId", nsId.getSystemId().getValue())))
					.with(body.colonizableSystemId.isEmpty() && context.getGameView().getAnnexableSystemIds().isEmpty()
							&& context.getGameView().getSystemNotifications().isEmpty(), "newTurn", () -> Boolean.TRUE)
					.toGetUri())
				.build();
		}
		else {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(body.selectedStarId)
							.toAction(HttpMethod.GET, "main-page")
							.with(body.fields.isPresent(), "fields", () -> body.fields.get())
							.toGetUri())
				.build();
		} // CPD-ON
	}

	@PostMapping(path = "/main-page/inspector/annexations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> annexSystem(@ModelAttribute FrontendContext context, @RequestBody AnnexSystemBodyDto body) {
		FleetView fleet = context.getGameView().getFleet(body.fleetId);
		ColonyView colonyToAnnex = context.getGameView().getSystem(fleet.getOrbiting().get()).getColonyView().get();
		context.getPlayerGame().annexSystem(colonyToAnnex.getId(), fleet.getId(), body.skip.orElse(Boolean.FALSE));

		if (!body.skip.isPresent()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, context.withSelectedStar(body.selectedStarId)
					.toAction(HttpMethod.GET, "main-page")
					.with(body.annexableSystemId.orElseGet(() -> List.of())
						.stream()
						.map(asId -> new ActionField("annexableSystemId", asId)))
					.with(context.getGameView()
						.getSystemNotifications()
						.stream()
						.map(nsId -> new ActionField("notificationSystemId", nsId.getSystemId().getValue())))
					.with(body.annexableSystemId.isEmpty() && context.getGameView().getSystemNotifications().isEmpty(),
							"newTurn", () -> Boolean.TRUE)
					.toGetUri())
				.build();
		}
		else {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(body.selectedStarId)
							.toAction(HttpMethod.GET, "main-page")
							.with(body.fields.isPresent(), "fields", () -> body.fields.get())
							.toGetUri())
				.build();
		}
	}

	@PostMapping(path = "/main-page/inspector/deployments", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> deployFleet(@ModelAttribute FrontendContext context,
			@RequestBody LinkedMultiValueMap<String, String> body) {
		FleetView fleet = context.getGameView().getFleet(new FleetId(body.getFirst("selectedFleetId")));

		Map<ShipTypeId, Integer> shipTypeIdsAndCounts = toShipTypesAndCounts(fleet.getShips(), body.toSingleValueMap())
			.entrySet()
			.stream()
			.collect(Collectors.toMap(e -> e.getKey().getId(), Entry::getValue));

		context.getPlayerGame()
			.deployFleet(fleet.getId(), new SystemId(body.getFirst("selectedStarId")), shipTypeIdsAndCounts);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.getFirst("selectedStarId"))
						.toAction(HttpMethod.GET, "main-page")
						.with(body.containsKey("fields"), "fields", () -> body.getFirst("fields"))
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/spendings", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> allocateSystemSpendings(@ModelAttribute FrontendContext context,
			@RequestBody SystemSpendingsBodyDto body) {
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with(body.fields.isPresent(), "fields", () -> body.fields.get())
						.with(body.locked.isPresent(), "lockedCategory", () -> body.locked.get())
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/ship-types", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> nextShipType(@ModelAttribute FrontendContext context, @RequestBody NextShipTypeBodyDto body) {
		context.getPlayerGame().nextShipType(body.colonyId);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with(body.fields.isPresent(), "fields", () -> body.fields.get())
						.with(body.locked.isPresent(), "lockedCategory", () -> body.locked.get())
						.toGetUri())
			.build();
	}

	@GetMapping("/main-page")
	ResponseEntity<EntityModel<MainPageDto>> mainPage(HttpServletRequest request,
			@ModelAttribute FrontendContext context, @RequestParam Optional<String> selectedStarId,
			@RequestParam Optional<String> selectedFleetId,
			@RequestParam(name = "spaceCombatSystemId") Optional<List<String>> spaceCombatSystemIds,
			@RequestParam(name = "exploredSystemId") Optional<List<String>> exploredSystemIds,
			@RequestParam(name = "colonizableSystemId") Optional<List<String>> colonizableSystemIds,
			@RequestParam(name = "annexableSystemId") Optional<List<String>> annexableSystemIds,
			@RequestParam(name = "notificationSystemId") Optional<List<String>> notificationSystemIds,
			@RequestParam Optional<Integer> turnFinishedRound, @RequestParam Optional<Boolean> newTurn,
			@RequestParam Map<String, String> shipTypeIdsAndCounts, @RequestParam Optional<String> lockedCategory,
			@RequestParam Optional<String> fields) {
		if (context.isEmpty()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, context.toAction(HttpMethod.GET, "new-game-page").toGetUri())
				.build();
		}

		Game game = this.gameHolder.get(context.getGameId()).orElseThrow();
		game.unregisterAi(context.getPlayer());

		MainPageState state = MainPageState.fromParameters(selectedStarId, selectedFleetId, shipTypeIdsAndCounts,
				spaceCombatSystemIds, exploredSystemIds, colonizableSystemIds, annexableSystemIds,
				notificationSystemIds, lockedCategory,
				turnFinishedRound.map(tfr -> tfr == context.getGameView().getRound()), newTurn.orElse(Boolean.FALSE),
				(fleetId, parameters) -> toShipTypesAndCounts(context.getGameView().getFleet(fleetId).getShips(),
						parameters),
				(fleetId, orbitingSystemId) -> context.getGameView()
					.getFleet(fleetId)
					.getOrbiting()
					.map(d -> d.equals(orbitingSystemId))
					.orElse(Boolean.FALSE),
				fleetId -> context.getGameView().getFleet(fleetId).isDeployable());

		if (logger.isDebugEnabled()) {
			logger.debug("player: {}, state: {}", context.getPlayer(), state.getClass().getSimpleName());
		}

		if (state.isInitState()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, context.withSelectedStar(context.getGameView().getHomeSystem().getId())
					.toAction(HttpMethod.GET, "main-page")
					.with(!context.getGameView().isOwnTurnFinished() ? getSteps(context.getGameView()) : Stream.empty())
					.with(context.getGameView().isOwnTurnFinished(), "turnFinishedRound",
							() -> context.getGameView().getRound())
					.toGetUri())
				.build();
		}

		boolean selectTech = !context.getGameView().getSelectTechs().isEmpty();
		if (selectTech && !state.isTurnFinishedState() && !state.isFleetMovementState()
				&& spaceCombatSystemIds.isEmpty()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(context.getSelectedStarId().orElseThrow().getValue())
							.toAction(HttpMethod.GET, "select-tech-page")
							.toGetUri())
				.build();
		}

		return ResponseEntity.ok(MainPageDto
			.filterFields(MainPageDtoPopulator.populate(context, state, getSteps(context.getGameView())), fields)
			.with(new Action("_self", request.getRequestURI(), ActionHttpMethod.GET).with(request.getParameterMap()
				.entrySet()
				.stream()
				.filter(pv -> !"fields".equals(pv.getKey()))
				.flatMap(pv -> Stream.of(pv.getValue()).map(v -> new ActionField(pv.getKey(), v))))));
	}

	private static Stream<ActionField> getSteps(GameView gameView) {
		Set<ActionField> fields = Stream.concat(Stream.concat(Stream.concat(Stream.concat(//
				gameView.getSpaceCombats()
					.stream()
					.map(scv -> new ActionField("spaceCombatSystemId",
							scv.getSystemId().getValue() + "@" + scv.getOrder())),

				gameView.getJustExploredSystemIds()
					.stream()
					.map(esId -> new ActionField("exploredSystemId", esId.getValue()))),

				gameView.getColonizableSystemIds()
					.stream()
					.map(csId -> new ActionField("colonizableSystemId", csId.getValue()))),

				gameView.getAnnexableSystemIds()
					.stream()
					.map(asId -> new ActionField("annexableSystemId", asId.getValue()))),

				gameView.getSystemNotifications()
					.stream()
					.map(SystemNotificationView::getSystemId)
					.map(nsId -> new ActionField("notificationSystemId", nsId.getValue())))
			.collect(Collectors.toSet());

		if (!fields.isEmpty()) {
			return fields.stream();
		}
		else {
			return Stream.of(new ActionField("newTurn", Boolean.TRUE));
		}
	}

	private static Map<ShipTypeView, Integer> toShipTypesAndCounts(Map<ShipTypeView, Integer> fleetShips,
			Map<String, String> parameters) {
		return fleetShips.keySet()
			.stream()
			.filter(st -> parameters.containsKey(st.getId().getValue()))
			.collect(Collectors.toMap(Function.identity(),
					st -> Integer.valueOf(parameters.get(st.getId().getValue()))));
	}

	static class ColonizeSystemBodyDto {

		SystemId selectedStarId;

		FleetId fleetId;

		Optional<List<String>> colonizableSystemId = Optional.empty();

		// The presence of a skip value is a terrible way to indicate that the action was
		// invoked in the command phase
		// and not as part of the flow at the begin of a player's turn.
		Optional<Boolean> skip = Optional.empty();

		Optional<String> fields = Optional.empty();

	}

	static class AnnexSystemBodyDto {

		SystemId selectedStarId;

		FleetId fleetId;

		Optional<List<String>> annexableSystemId = Optional.empty();

		// The presence of a skip value is a terrible way to indicate that the action was
		// invoked in the command phase
		// and not as part of the flow at the begin of a player's turn.
		Optional<Boolean> skip = Optional.empty();

		Optional<String> fields = Optional.empty();

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

	static class FinishTurnBodyDto {

		SystemId selectedStarId;

		int round;

	}

	static class NextShipTypeBodyDto {

		SystemId selectedStarId;

		Optional<String> fields = Optional.empty();

		ColonyId colonyId;

		Optional<String> locked = Optional.empty();

	}

}
