package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
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
						.with(body.partial, "partial", () -> true)
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/colonizations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> colonizeSystem(@ModelAttribute FrontendContext context,
			@RequestBody ColonizeSystemBodyDto body) {
		FleetView fleet = context.getGameView().getFleet(body.fleetId);
		SystemView systemToColonize = context.getGameView().getSystem(fleet.getOrbiting().get());
		context.getPlayerGame().colonizeSystem(systemToColonize.getId(), fleet.getId(), body.skip);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with(body.partial, "partial", () -> true)
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/annexations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> annexSystem(@ModelAttribute FrontendContext context, @RequestBody AnnexSystemBodyDto body) {
		FleetView fleet = context.getGameView().getFleet(body.fleetId);
		ColonyView colonyToAnnex = context.getGameView().getSystem(fleet.getOrbiting().get()).getColonyView().get();
		context.getPlayerGame().annexSystem(colonyToAnnex.getId(), fleet.getId(), body.skip);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with(body.partial, "partial", () -> true)
						.toGetUri())
			.build();
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
						.with(Boolean.parseBoolean(body.getFirst("partial")), "partial",
								() -> "updatedParentFleetId=" + fleet.getParentId().orElse(fleet.getId()).getValue())
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
						.with(body.partial, "partial", () -> true)
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
						.with(body.partial, "partial", () -> true)
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/colonist-transfers", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> transferColonists(@ModelAttribute FrontendContext context,
			@RequestBody TransferColonistsBodyDto body) {
		context.getPlayerGame()
			.transferColonists(body.selectedStarId.toColonyId(), body.transferColonyId, body.colonists);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with(body.partial, "partial", () -> true)
						.toGetUri())
			.build();
	}

	@PostMapping(path = "/main-page/inspector/ship-relocations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> relocateShips(@ModelAttribute FrontendContext context,
			@RequestBody RelocateShipsBodyDto body) {
		context.getPlayerGame().relocateShips(body.selectedStarId.toColonyId(), body.relocateColonyId);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId)
						.toAction(HttpMethod.GET, "main-page")
						.with(body.partial, "partial", () -> true)
						.toGetUri())
			.build();
	}

	@GetMapping("/main-page")
	ResponseEntity<EntityModel<MainPageDto>> mainPage(HttpServletRequest request,
			@ModelAttribute FrontendContext context, @RequestParam Optional<String> selectedStarId,
			@RequestParam Optional<String> selectedFleetId, Optional<String> spotlightedStarId,
			@RequestParam Optional<String> transferStarId, @RequestParam Optional<String> relocateStarId,
			@RequestParam Map<String, String> shipTypeIdsAndCounts, @RequestParam Optional<String> partial) {
		if (context.isEmpty()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION, context.toAction(HttpMethod.GET, "new-game-page").toGetUri())
				.build();
		}

		Game game = this.gameHolder.get(context.getGameId()).orElseThrow();
		game.unregisterAi(context.getPlayer());

		MainPageState state = MainPageState.fromParameters(selectedStarId, spotlightedStarId, transferStarId,
				relocateStarId, selectedFleetId, shipTypeIdsAndCounts,
				(fleetId, parameters) -> toShipTypesAndCounts(context.getGameView().getFleet(fleetId).getShips(),
						parameters),
				(fleetId, orbitingSystemId) -> context.getGameView()
					.getFleet(fleetId)
					.getOrbiting()
					.map(d -> d.equals(orbitingSystemId))
					.orElse(Boolean.FALSE),
				fleetId -> context.getGameView().getFleet(fleetId).isDeployable(),
				systemId -> context.getGameView()
					.getSystem(systemId)
					.getColonyView(context.getGameView().getPlayer())
					.isPresent());

		if (logger.isDebugEnabled()) {
			logger.debug("player: {}, state: {}", context.getPlayer(), state.getClass().getSimpleName());
		}

		if (state.isInitState()) {
			return ResponseEntity.status(HttpStatus.SEE_OTHER)
				.header(HttpHeaders.LOCATION,
						context.withSelectedStar(context.getGameView().getHomeSystem().getId())
							.toAction(HttpMethod.GET, "main-page")
							.toGetUri())
				.build();
		}

		return ResponseEntity
			.ok(MainPageDto.filterFields(state, MainPageDtoPopulator.populate(context, state), partial.orElse("false"))
				.with(new Action("_self", request.getRequestURI(), ActionHttpMethod.GET).with(request.getParameterMap()
					.entrySet()
					.stream()
					.flatMap(pv -> Stream.of(pv.getValue()).map(v -> new ActionField(pv.getKey(), v))))));
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

		boolean skip;

		boolean partial;

	}

	static class AnnexSystemBodyDto {

		SystemId selectedStarId;

		FleetId fleetId;

		boolean skip;

		boolean partial;

	}

	static class SystemSpendingsBodyDto {

		SystemId selectedStarId;

		Optional<String> locked = Optional.empty();

		Optional<Integer> ship = Optional.empty();

		Optional<Integer> defence = Optional.empty();

		Optional<Integer> industry = Optional.empty();

		Optional<Integer> ecology = Optional.empty();

		Optional<Integer> technology = Optional.empty();

		boolean partial;

	}

	static class FinishTurnBodyDto {

		SystemId selectedStarId;

		int round;

		boolean partial;

	}

	static class NextShipTypeBodyDto {

		SystemId selectedStarId;

		ColonyId colonyId;

		boolean partial;

	}

	static class TransferColonistsBodyDto {

		SystemId selectedStarId;

		int colonists;

		ColonyId transferColonyId;

		boolean partial;

	}

	static class RelocateShipsBodyDto {

		SystemId selectedStarId;

		ColonyId relocateColonyId;

		boolean partial;

	}

}
