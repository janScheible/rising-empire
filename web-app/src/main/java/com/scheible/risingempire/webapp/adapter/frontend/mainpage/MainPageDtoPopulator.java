package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusView;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.dto.AllocationCategoryDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.AllocationsDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.TurnFinishedStatusPlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.AnnexationDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.BuildQueueDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ColonizationDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ColonyDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ExplorationDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.FleetDeploymentDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.FleetViewDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ForeignColonyOwner;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.HabitabilityDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ProductionDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ShipsDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.SpaceCombatDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.SpaceCombatSystem;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.SystemDetailsDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.TransferColonistsDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.UnexploredDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPageDto.ButtonBarDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPageDto.TurnStatusDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.FleetDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.FleetSelectionDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.ItineraryDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.RangesDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.RangesDto.FleetRangeDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.RangesDto.ScannerRangeDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.StarDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.StarNotificationDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.StarSelectionDto;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import org.springframework.http.HttpMethod;

/**
 * @author sj
 */
public class MainPageDtoPopulator {

	static EntityModel<MainPageDto> populate(FrontendContext context, MainPageState state) {
		GameView gameView = context.getGameView();

		MainPageDto mainPage = new MainPageDto(gameView.getRound(),
				new TurnStatusDto(gameView.getTurnFinishedStatus().get(gameView.getPlayer()),
						gameView.getTurnFinishedStatus()
							.entrySet()
							.stream()
							.map(tfs -> TurnFinishedStatusPlayerDto.fromPlayer(tfs.getKey(), tfs.getValue()))
							.collect(Collectors.toList())),
				new StarMapDto(gameView.getGalaxyWidth(), gameView.getGalaxyHeight()),
				new MainPageDto.StateDescriptionDto(state.getClass().getSimpleName()));

		mainPage.starMap.getContent().ranges = new RangesDto(gameView.getGalaxyWidth(), gameView.getGalaxyHeight(),
				gameView.getPlayer());

		mainPage.starMap.getContent().ranges.fleetRanges.addAll(gameView.getSystems()
			.stream()
			.filter(s -> s.getFleetRange().isPresent() && s.getExtendedFleetRange().isPresent())
			.map(s -> new FleetRangeDto("fleetRange@" + s.getId().getValue(), s.getLocation().getX(),
					s.getLocation().getY(), s.getFleetRange().orElseThrow(), s.getExtendedFleetRange().orElseThrow()))
			.collect(Collectors.toList()));

		mainPage.starMap.getContent().ranges.colonyScannerRanges.addAll(gameView.getSystems()
			.stream()
			.filter(s -> s.getScannerRange().isPresent())
			.map(s -> new ScannerRangeDto("scannerRange@" + s.getId().getValue(), s.getLocation().getX(),
					s.getLocation().getY(), s.getScannerRange().get()))
			.collect(Collectors.toList()));

		mainPage.starMap.getContent().ranges.fleetScannerRanges.addAll(gameView.getFleets()
			.stream()
			.filter(f -> f.getScannerRange().isPresent())
			.map(f -> new ScannerRangeDto("scannerRange@" + f.getId().getValue(), f.getLocation().getX(),
					f.getLocation().getY(), f.getScannerRange().orElseThrow()))
			.collect(Collectors.toList()));

		SystemId turnSelectedStarId = null;
		if (state.getSelectedSystemId().isPresent()) {
			turnSelectedStarId = fromSelectedStar(gameView, state, mainPage, context);
		}

		Optional<Map<ShipTypeView, Integer>> stateShips = state.isFleetDeploymentState()
				? state.asFleetDeploymentState().getShips() : state.isFleetInspectionState()
						? state.asFleetInspectionState().getShips() : Optional.of(Map.<ShipTypeView, Integer>of());
		Optional<Map<ShipTypeView, Integer>> maybeShips = stateShips
			.or(() -> state.getSelectedFleetId().map(id -> gameView.getFleet(id).getShips()));

		if (state.getSelectedFleetId().isPresent()) {
			turnSelectedStarId = fromSelectedFleet(gameView, state, maybeShips, mainPage, context);
		}

		{
			SystemId finalTurnSelectedStarId = turnSelectedStarId;
			mainPage.buttonBar = new EntityModel<>(new ButtonBarDto())
				.with(Action.jsonPost("finish-turn", context.toFrontendUri("main-page", "button-bar", "finished-turns"))
					.with("selectedStarId", finalTurnSelectedStarId.getValue())
					.with("round", gameView.getRound()))
				.with(Action.get("show-tech-page", context.toFrontendUri("tech-page"))
					.with("selectedStarId", finalTurnSelectedStarId.getValue()));
		}

		mainPage.starMap.getContent().stars = gameView.getSystems()
			.stream()
			.map(s -> new EntityModel<>(new StarDto(s.getId().getValue(), s.getStarName(), s.getStarType(), s.isSmall(),
					s.getColonyView().map(ColonyView::getPlayer),
					s.getColonyView()
						.flatMap(ColonyView::getAnnexationStatus)
						.flatMap(AnnexationStatusView::siegingPlayer),
					s.getColonyView()
						.flatMap(ColonyView::getAnnexationStatus)
						.flatMap(AnnexationStatusView::getProgress),
					s.getLocation().getX(), s.getLocation().getY()))
				.with(state.isSystemSelectable(s.getId()), () -> Action
					.get("select", context.toFrontendUri("main-page"))
					.with("selectedStarId",
							state.isTransferColonistsState() ? state.getSelectedSystemId().get().getValue()
									: s.getId().getValue())
					.with(state.isTransferColonistsState(), "transferStarId", () -> s.getId().getValue())
					.with(state.getSelectedFleetId().isPresent()
							&& gameView.getFleet(state.getSelectedFleetId().orElseThrow()).isDeployable(),
							"selectedFleetId", () -> state.getSelectedFleetId().map(FleetId::getValue).orElseThrow())
					.with(maybeShips.isPresent(),
							() -> maybeShips.map(ships -> toDtoShipList(ships))
								.orElseThrow()
								.stream()
								.map(sh -> new ActionField(sh.id, sh.count)))))
			.collect(Collectors.toList());

		Predicate<FleetId> isSpaceCombatAttacker = fleetId -> gameView.getSpaceCombats()
			.stream()
			.anyMatch(sc -> sc.getAttackerFleets().stream().anyMatch(af -> af.getId().equals(fleetId)));

		mainPage.starMap.getContent().fleets = gameView.getFleets()
			.stream()
			.map(fleet -> new EntityModel<>(new FleetDto(fleet.getId().getValue(), fleet.getPlayer(),
					fleet.getPreviousLocation().map(Location::getX), fleet.getPreviousLocation().map(Location::getY),
					fleet.isPreviousJustLeaving(), fleet.getLocation().getX(), fleet.getLocation().getY(),
					fleet.getType() == FleetViewType.ORBITING, fleet.isJustLeaving().orElse(Boolean.FALSE),
					fleet.getSpeed(), fleet.getHorizontalDirection(),
					fleet.getFleetsBeforeArrival()
						.stream()
						.map(fba -> new EntityModel<>(new FleetDto(fba.getId().getValue(), fleet.getPlayer(),
								Optional.of(fba.getLocation().getX()), Optional.of(fba.getLocation().getY()),
								Optional.of(fba.isJustLeaving()), fleet.getLocation().getX(),
								fleet.getLocation().getY(), !isSpaceCombatAttacker.test(fba.getId()),
								fba.isJustLeaving(), Optional.of(fba.getSpeed()),
								Optional.of(fba.getHorizontalDirection()), List.of())))
						.toList()))
				.with(state.isFleetSelectable(fleet.getId()),
						() -> Action.get("select", context.toFrontendUri("main-page"))
							.with(state.isFleetSelectable(fleet.getId()), "selectedFleetId",
									() -> fleet.getId().getValue())
							.with(fleet.getDestination().isPresent(), "selectedStarId",
									() -> fleet.getDestination().get().getValue())))
			.collect(Collectors.toList());

		mainPage.starMap.getContent().starNotifications = gameView.getSystemNotifications()
			.stream()
			.flatMap(sn -> sn.getMessages().stream().map(message -> {
				SystemView notificationSystem = gameView.getSystem(sn.getSystemId());
				return new StarNotificationDto(notificationSystem.getId().getValue(),
						notificationSystem.getLocation().getX(), notificationSystem.getLocation().getY(), message);
			}))
			.toList();

		final SystemId finalTurnSelectedStarId = turnSelectedStarId;
		Function<SystemId, Action> spotlightAction = (systemId) -> Action
			.get("spotlight", context.toFrontendUri("main-page"))
			.with(finalTurnSelectedStarId != null, "selectedStarId", () -> finalTurnSelectedStarId.getValue())
			.with("spotlightedStarId", systemId.getValue());

		mainPage.spaceCombats = gameView.getSpaceCombats()
			.stream()
			.map(sc -> new EntityModel<>(toSpaceCombat(sc, gameView.getSystem(sc.getSystemId())))
				.with(spotlightAction.apply(sc.getSystemId())))
			.toList();

		mainPage.explorations = gameView.getJustExploredSystemIds()
			.stream()
			.map(jesi -> new EntityModel<>(new MainPageDto.ExplorationDto(jesi.getValue()))
				.with(spotlightAction.apply(jesi)))
			.toList();

		mainPage.colonizations = gameView.getColonizableSystemIds()
			.stream()
			.map(csi -> new EntityModel<>(new MainPageDto.ColonizationDto(csi.getValue()))
				.with(spotlightAction.apply(csi)))
			.toList();

		mainPage.annexations = gameView.getAnnexableSystemIds()
			.stream()
			.map(asi -> new EntityModel<>(new MainPageDto.AnnexationDto(asi.getValue()))
				.with(spotlightAction.apply(asi)))
			.toList();

		return new EntityModel<>(mainPage).with(!context.getGameView().getSelectTechs().isEmpty(),
				() -> Action.get("select-tech", context.toFrontendUri("select-tech-page"))
					.with("selectedStarId", finalTurnSelectedStarId.getValue()));
	}

	private static SystemId fromSelectedStar(GameView gameView, MainPageState state, MainPageDto mainPage,
			FrontendContext context) {
		SystemView selectedSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());

		mainPage.starMap.getContent().starSelection = new StarSelectionDto(selectedSystem.getLocation().getX(),
				selectedSystem.getLocation().getY());

		boolean colonization = (state.isStarInspectionState() && selectedSystem.isColonizable())
				|| (state.isStarSpotlightState()
						&& gameView.getColonizableSystemIds().contains(state.getSelectedSystemId().get()));
		boolean annexation = (state.isStarInspectionState() && selectedSystem.getColonyView()
			.flatMap(ColonyView::getAnnexationStatus)
			.flatMap(AnnexationStatusView::annexable)
			.orElse(Boolean.FALSE))
				|| (state.isStarSpotlightState()
						&& gameView.getAnnexableSystemIds().contains(state.getSelectedSystemId().get()));

		Supplier<HabitabilityDto> habitabilityDtoSupplier = () -> new HabitabilityDto(
				selectedSystem.getPlanetType().get(), selectedSystem.getPlanetSpecial().get(),
				selectedSystem.getPlanetMaxPopulation().get());

		if ((state.isStarInspectionState() || state.isTransferColonistsState()) && !(colonization || annexation)) {
			if (selectedSystem.getStarName().isPresent()) {
				Optional<Integer> transferEta = Optional.empty();
				if (state.isTransferColonistsState() && !state.getSelectedSystemId()
					.equals(state.asTransferColonistsState().getTransferSystemId())) {
					SystemView transferDestinationSystem = gameView
						.getSystem(state.asTransferColonistsState().getTransferSystemId());

					transferEta = context.getPlayerGame()
						.calcTranportColonistsEta(selectedSystem.getId(), transferDestinationSystem.getId())
						.filter(eta -> transferDestinationSystem.getColonyView(gameView.getPlayer()).isPresent());

					mainPage.starMap.getContent().starSelection.itinerary = Optional
						.of(new ItineraryDto(transferDestinationSystem.getLocation().getX(),
								transferDestinationSystem.getLocation().getY(), selectedSystem.getLocation().getX(),
								selectedSystem.getLocation().getY(), false, false, transferEta.isPresent()));
				}

				mainPage.inspector.systemDetails = new SystemDetailsDto(selectedSystem.getStarName().get(),
						habitabilityDtoSupplier.get(),
						selectedSystem.getColonyView()
							.map(c -> new ColonyDto(
									Optional.ofNullable(c.getPlayer() != gameView.getPlayer()
											? new ForeignColonyOwner(c.getRace(), c.getPlayer()) : null),
									c.getPopulation(), c.getRatios().map(r -> new ProductionDto(42, 78)),
									c.getAnnexationStatus().flatMap(AnnexationStatusView::roundsUntilAnnexable),
									c.getAnnexationStatus()
										.filter(as -> context.getPlayer() == c.getPlayer())
										.flatMap(AnnexationStatusView::siegingPlayer),
									c.getAnnexationStatus()
										.filter(as -> context.getPlayer() == c.getPlayer())
										.flatMap(AnnexationStatusView::siegingRace))),
						selectedSystem.getColonyView()
							.filter(c -> c.getPlayer() == gameView.getPlayer() && !state.isTransferColonistsState())
							.map(c -> new EntityModel<>(new AllocationsDto(Map.of( //
									"ship", new AllocationCategoryDto(10, "None"), //
									"defence", new AllocationCategoryDto(15, "None"), //
									"industry", new AllocationCategoryDto(20, "2.7/y"), //
									"ecology", new AllocationCategoryDto(25, "Clean"), //
									"technology", new AllocationCategoryDto(30, "0RP")),
									state.asStarInspectionState().getLockedCategory()))
								.with(Action
									.jsonPost("allocate-spending",
											context.toFrontendUri("main-page", "inspector", "spendings"))
									.with("selectedStarId", selectedSystem.getId().getValue()))),
						selectedSystem.getColonyView()
							.filter(c -> c.getPlayer() == gameView.getPlayer() && !state.isTransferColonistsState())
							.map(c -> new EntityModel<>(new BuildQueueDto(c.getSpaceDock().get().getName(),
									c.getSpaceDock().get().getSize(), gameView.getPlayer(), 1))
								.with(Action
									.jsonPost("next-ship-type",
											context.toFrontendUri("main-page", "inspector", "ship-types"))
									.with("selectedStarId", selectedSystem.getId().getValue())
									.with(state.asStarInspectionState().getLockedCategory().isPresent(), "locked",
											() -> state.asStarInspectionState().getLockedCategory().get())
									.with("colonyId", c.getId().getValue()))
								.with(Action.get("transfer-colonists", context.toFrontendUri("main-page"))
									.with("selectedStarId", selectedSystem.getId().getValue())
									.with("transferStarId", selectedSystem.getId().getValue()))),
						state.isTransferColonistsState() ? Optional
							.of(new EntityModel<>(new TransferColonistsDto(selectedSystem.getColonyView()
								.get()
								.getColonistTransfers()
								.getOrDefault(state.asTransferColonistsState().getTransferSystemId().toColonyId(), 0),
									selectedSystem.getColonyView()
										.map(ColonyView::getPopulation)
										.map(p -> p / 2)
										.orElseThrow(),
									transferEta))
								.with(Action.get("cancel", context.toFrontendUri("main-page"))
									.with("selectedStarId", selectedSystem.getId().getValue()))
								.with(transferEta.isPresent() && transferEta.get() > 0,
										() -> Action
											.jsonPost("transfer",
													context.toFrontendUri("main-page", "inspector",
															"colonist-transfers"))
											.with("selectedStarId", selectedSystem.getId().getValue())
											.with("colonists", 0)
											.with("transferColonyId",
													state.asTransferColonistsState()
														.getTransferSystemId()
														.toColonyId()
														.getValue())))
								: Optional.empty(),
						selectedSystem.getRange());

			}
			else {
				mainPage.inspector.unexplored = new UnexploredDto(selectedSystem.getStarType(),
						selectedSystem.getRange().orElseThrow());
			}
		}
		else if (colonization || annexation) {
			FleetId orbiting = gameView.getOrbiting(selectedSystem.getId()).get().getId();

			if (colonization) {
				mainPage.inspector.colonization = new EntityModel<>(
						new ColonizationDto(selectedSystem.getStarName().get(), habitabilityDtoSupplier.get(),
								Optional.ofNullable(state.isStarSpotlightState() ? null
										: selectedSystem.hasColonizeCommand().orElse(Boolean.FALSE))))
					.with(Action.jsonPost("colonize", context.toFrontendUri("main-page", "inspector", "colonizations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().getValue()
										: selectedSystem.getId().getValue())
						.with("fleetId", orbiting.getValue())
						.with("skip", Boolean.FALSE))
					.with(Action.jsonPost("cancel", context.toFrontendUri("main-page", "inspector", "colonizations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().getValue()
										: selectedSystem.getId().getValue())
						.with("fleetId", orbiting.getValue())
						.with("skip", Boolean.TRUE));
			}
			else if (annexation) {
				mainPage.inspector.annexation = new EntityModel<>(
						new AnnexationDto(selectedSystem.getStarName().get(), habitabilityDtoSupplier.get(),
								Optional.ofNullable(state.isStarSpotlightState() ? null
										: selectedSystem.getColonyView()
											.get()
											.getAnnexationStatus()
											.get()
											.annexCommand()
											.orElse(Boolean.FALSE))))
					.with(Action.jsonPost("annex", context.toFrontendUri("main-page", "inspector", "annexations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().getValue()
										: selectedSystem.getId().getValue())
						.with("fleetId", orbiting.getValue())
						.with("skip", Boolean.FALSE))
					.with(Action.jsonPost("cancel", context.toFrontendUri("main-page", "inspector", "annexations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().getValue()
										: selectedSystem.getId().getValue())
						.with("fleetId", orbiting.getValue())
						.with("skip", Boolean.TRUE));
			}
		}
		else if (state.isStarSpotlightState()) {
			if (gameView.getSpaceCombats()
				.stream()
				.map(SpaceCombatView::getSystemId)
				.anyMatch(s -> s.equals(state.getSelectedSystemId().get()))) {
				SpaceCombatView spaceCombat = gameView.getSpaceCombats()
					.stream()
					.filter(sc -> sc.getSystemId().equals(selectedSystem.getId()))
					.findAny()
					.orElseThrow();

				mainPage.inspector.spaceCombat = new EntityModel<>(new SpaceCombatDto(selectedSystem.getStarName()
					.map(name -> new SpaceCombatSystem(name, new HabitabilityDto(selectedSystem.getPlanetType().get(),
							selectedSystem.getPlanetSpecial().get(), selectedSystem.getPlanetMaxPopulation().get()))),
						spaceCombat.getAttacker(), spaceCombat.getAttackerPlayer(), spaceCombat.getDefender(),
						spaceCombat.getDefenderPlayer()))
					.with(Action
						.get("continue",
								context.toFrontendUri("space-combat-page",
										state.getSelectedSystemId().get().getValue()))
						.with("selectedStarId", state.asStarSpotlightState().getActualSelectedSystemId().getValue()));
			}
			else if (gameView.getJustExploredSystemIds().contains(state.getSelectedSystemId().get())) {
				mainPage.inspector.exploration = new EntityModel<>(
						new ExplorationDto(selectedSystem.getStarName().orElseThrow(),
								new HabitabilityDto(selectedSystem.getPlanetType().get(),
										selectedSystem.getPlanetSpecial().get(),
										selectedSystem.getPlanetMaxPopulation().get())))
					.with(Action.get("continue", context.toFrontendUri("main-page"))
						.with("selectedStarId", state.asStarSpotlightState().getActualSelectedSystemId().getValue()));
			}
		}

		return selectedSystem.getId();
	}

	private static SystemId fromSelectedFleet(GameView gameView, MainPageState state,
			Optional<Map<ShipTypeView, Integer>> maybeShips, MainPageDto mainPage, FrontendContext context) {
		SystemId turnSelectedStarId;
		FleetView selectedFleet = gameView.getFleet(state.getSelectedFleetId().orElseThrow());
		boolean justLeaving = selectedFleet.isJustLeaving().orElse(Boolean.FALSE);
		Map<ShipTypeView, Integer> ships = maybeShips.orElseThrow();
		mainPage.starMap.getContent().fleetSelection = new FleetSelectionDto(selectedFleet.getLocation().getX(),
				selectedFleet.getLocation().getY(), selectedFleet.getOrbiting().isPresent(), justLeaving);
		turnSelectedStarId = selectedFleet.getOrbiting().or(selectedFleet::getClosest).orElseThrow();
		Map<ShipTypeView, Integer> totalShips = gameView.getFleet(selectedFleet.getId()).getShips();

		if (state.isFleetInspectionState()) {
			if (selectedFleet.isDeployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(
						new FleetDeploymentDto(selectedFleet.getId().getValue(), gameView.getRound(),
								selectedFleet.getPlayer(), null, null, false, toDtoShipList(ships, totalShips)));
			}
			else {
				mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.getPlayer(),
						selectedFleet.getRace(), null, toDtoShipList(ships)));
			}
		}
		else if (state.isFleetDeploymentState()) {
			SystemView selectedSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());
			Optional<Integer> eta = context.getPlayerGame()
				.calcEta(selectedFleet.getId(), selectedSystem.getId(),
						ships.entrySet()
							.stream()
							.collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue)));

			if (selectedFleet.isDeployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(
						new FleetDeploymentDto(selectedFleet.getId().getValue(), gameView.getRound(),
								selectedFleet.getPlayer(), eta.orElse(null),
								eta.isPresent() ? null : selectedSystem.getRange().orElse(null), true, toDtoShipList(
										ships, totalShips)))
					.with(selectedFleet.isDeployable() && eta.isPresent(),
							() -> Action
								.jsonPost("deploy", context.toFrontendUri("main-page", "inspector", "deployments"))
								.with("selectedFleetId", selectedFleet.getId().getValue())
								.with("selectedStarId", selectedSystem.getId().getValue())
								.with(toDtoShipList(ships).stream().map(s -> new ActionField(s.id, s.count))));
			}
			else {
				mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.getPlayer(),
						selectedFleet.getRace(), eta.orElse(null), toDtoShipList(ships)));
			}

			mainPage.starMap.getContent().fleetSelection.itinerary = Optional
				.of(new ItineraryDto(selectedFleet.getLocation().getX(), selectedFleet.getLocation().getY(),
						selectedSystem.getLocation().getX(), selectedSystem.getLocation().getY(),
						selectedFleet.getOrbiting().isPresent(), justLeaving, eta.isPresent()));
		}

		if (mainPage.inspector.fleetDeployment != null) {
			mainPage.inspector.fleetDeployment
				.with(Action.get("cancel", context.toFrontendUri("main-page"))
					.with("selectedStarId",
							selectedFleet.getOrbiting()
								.orElseGet(() -> selectedFleet.getSource().orElseThrow())
								.getValue()))
				.with(context.toNamedAction("assign-ships", HttpMethod.GET, true, true, "main-page")
					.with(maybeShips.isPresent(),
							() -> maybeShips.map(s -> toDtoShipList(s))
								.orElseThrow()
								.stream()
								.map(sh -> new ActionField(sh.id, sh.count))));
		}

		return turnSelectedStarId;
	}

	static List<ShipsDto> toDtoShipList(Map<ShipTypeView, Integer> ships) {
		return toDtoShipList(ships, Map.of());
	}

	static List<ShipsDto> toDtoShipList(Map<ShipTypeView, Integer> ships, Map<ShipTypeView, Integer> totalShips) {
		return ships.entrySet()
			.stream()
			.map(e -> new ShipsDto(e.getKey().getId().getValue(), e.getKey().getName(), e.getKey().getSize(),
					e.getValue(), totalShips.get(e.getKey())))
			.sorted((first, second) -> first.name.compareTo(second.name))
			.collect(Collectors.toList());
	}

	static MainPageDto.SpaceCombatDto toSpaceCombat(SpaceCombatView sc, SystemView combatSystem) {
		Player player = null;
		Set<FleetBeforeArrival> fleets = Set.of();
		boolean orbiting = false;

		if (sc.getOutcome() == Outcome.ATTACKER_WON) {
			player = sc.getDefenderPlayer();
			fleets = sc.getDefenderFleetsBeforeArrival();
			orbiting = true;
		}
		else if (sc.getOutcome() == Outcome.DEFENDER_WON) {
			player = sc.getAttackerPlayer();
			fleets = sc.getAttackerFleets();
			orbiting = false;
		}

		final Player finalPlayer = player;
		final boolean finalOrbiting = orbiting;
		List<FleetDto> destroyedFleetDtos = new ArrayList<>();

		if (player != null) {
			destroyedFleetDtos.addAll(fleets.stream()
				.map(fba -> new FleetDto(fba.getId().getValue(), finalPlayer, Optional.of(fba.getLocation().getX()),
						Optional.of(fba.getLocation().getY()), Optional.of(fba.isJustLeaving()),
						combatSystem.getLocation().getX(), combatSystem.getLocation().getY(), finalOrbiting, false,
						Optional.of(fba.getSpeed()), Optional.of(fba.getHorizontalDirection()), List.of()))
				.toList());

			if (sc.getOutcome() == Outcome.ATTACKER_WON && sc.getDefenderFleet().isPresent()) {
				destroyedFleetDtos.add(new FleetDto(sc.getDefenderFleet().get().getValue(), finalPlayer,
						Optional.empty(), Optional.empty(), Optional.empty(), combatSystem.getLocation().getX(),
						combatSystem.getLocation().getY(), true, false, Optional.empty(), Optional.empty(), List.of()));
			}
		}

		return new MainPageDto.SpaceCombatDto(destroyedFleetDtos.stream().map(f -> new EntityModel<>(f)).toList());
	}

}
