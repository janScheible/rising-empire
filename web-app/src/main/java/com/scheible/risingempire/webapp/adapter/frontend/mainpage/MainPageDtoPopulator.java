package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatus;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.fleet.FleetView.FleetViewType;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
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
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.RelocateShipsDto;
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

		MainPageDto mainPage = new MainPageDto(gameView.getRound(), gameView.getPlayer(),
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

		SystemId turnSelectedSystemId = null;
		if (state.getSelectedSystemId().isPresent()) {
			turnSelectedSystemId = fromSelectedStar(gameView, state, mainPage, context);
		}

		Optional<ShipsView> stateShips = state.isFleetDeploymentState() ? state.asFleetDeploymentState().getShips()
				: state.isFleetInspectionState() ? state.asFleetInspectionState().getShips() : Optional.empty();
		Optional<ShipsView> maybeShips = stateShips
			.or(() -> state.getSelectedFleetId().map(id -> gameView.getFleet(id).getShips()));

		if (state.getSelectedFleetId().isPresent()) {
			// we always need a selected system --> derive it for example from the
			// currently selected fleet
			turnSelectedSystemId = fromSelectedFleet(gameView, state, maybeShips, mainPage, context);
		}
		SystemId selectedSystemId = turnSelectedSystemId;

		{
			mainPage.buttonBar = new EntityModel<>(new ButtonBarDto())
				.with(Action.jsonPost("finish-turn", context.toFrontendUri("main-page", "button-bar", "finished-turns"))
					.with("selectedStarId", selectedSystemId.getValue())
					.with("round", gameView.getRound()))
				.with(Action.get("show-tech-page", context.toFrontendUri("tech-page"))
					.with("selectedStarId", selectedSystemId.getValue()));
		}

		mainPage.starMap.getContent().stars = gameView.getSystems()
			.stream()
			.map(s -> new EntityModel<>(new StarDto(s.getId(), s.getStarName(), s.getStarType(), s.isSmall(),
					s.getColonyView().map(ColonyView::getPlayer),
					s.getColonyView().flatMap(ColonyView::getAnnexationStatus).flatMap(AnnexationStatus::siegingPlayer),
					s.getColonyView().flatMap(ColonyView::getAnnexationStatus).flatMap(AnnexationStatus::getProgress),
					s.getLocation().getX(), s.getLocation().getY(),
					s.getColonyView().flatMap(ColonyView::getRelocationTarget).map(rt -> toItinerary(s, rt, gameView))))
				.with(state.isSystemSelectable(s.getId()), () -> Action
					.get("select", context.toFrontendUri("main-page"))
					.with("selectedStarId",
							state.isTransferColonistsState() || state.isRelocateShipsState()
									? state.getSelectedSystemId().get().getValue() : s.getId().getValue())
					.with(state.isTransferColonistsState(), "transferStarId", () -> s.getId().getValue())
					.with(state.isRelocateShipsState(), "relocateStarId", () -> s.getId().getValue())
					.with(state.getSelectedFleetId().isPresent()
							&& gameView.getFleet(state.getSelectedFleetId().orElseThrow()).isDeployable(),
							"selectedFleetId", () -> state.getSelectedFleetId().map(FleetId::getValue).orElseThrow())
					.with(maybeShips.isPresent(),
							() -> maybeShips.orElseThrow()
								.getTypesWithCount()
								.stream()
								.map(twc -> new ActionField(twc.getKey().getId().getValue(), twc.getValue())))))
			.collect(Collectors.toList());

		Predicate<FleetId> isSpaceCombatAttacker = fleetId -> gameView.getSpaceCombats()
			.stream()
			.anyMatch(sc -> sc.getAttackerFleets().stream().anyMatch(af -> af.getId().equals(fleetId)));

		mainPage.starMap.getContent().fleets = gameView.getFleets()
			.stream()
			.map(fleet -> new EntityModel<>(new FleetDto(fleet.getId(), fleet.getPlayer(),
					fleet.getPreviousLocation().map(Location::getX), fleet.getPreviousLocation().map(Location::getY),
					fleet.isPreviousJustLeaving(), fleet.getLocation().getX(), fleet.getLocation().getY(),
					fleet.getType() == FleetViewType.ORBITING, fleet.isJustLeaving(), fleet.getSpeed(),
					fleet.getHorizontalDirection(),
					fleet.getFleetsBeforeArrival()
						.stream()
						.map(fba -> new EntityModel<>(new FleetDto(fba.getId(), fleet.getPlayer(),
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
			.collect(Collectors.groupingBy(fleetDto -> {
				FleetView fleet = context.getGameView().getFleet(new FleetId(fleetDto.getContent().id));
				return fleet.getParentId().orElse(fleet.getId()).getValue();
			}));

		mainPage.starMap.getContent().starNotifications = gameView.getSystemNotifications()
			.stream()
			.flatMap(sn -> sn.getMessages().stream().map(message -> {
				SystemView notificationSystem = gameView.getSystem(sn.getSystemId());
				return new StarNotificationDto(notificationSystem.getId(), notificationSystem.getLocation().getX(),
						notificationSystem.getLocation().getY(), message);
			}))
			.toList();

		Function<SystemId, Action> spotlightAction = (systemId) -> Action
			.get("spotlight", context.toFrontendUri("main-page"))
			.with("selectedStarId", selectedSystemId.getValue())
			.with("spotlightedStarId", systemId.getValue());

		mainPage.spaceCombats = gameView.getSpaceCombats()
			.stream()
			.map(sc -> new EntityModel<>(toSpaceCombat(sc, gameView.getSystem(sc.getSystemId())))
				.with(spotlightAction.apply(sc.getSystemId())))
			.toList();

		mainPage.explorations = gameView.getJustExploredSystemIds()
			.stream()
			.map(jesi -> new EntityModel<>(new MainPageDto.ExplorationDto(jesi)).with(spotlightAction.apply(jesi)))
			.toList();

		mainPage.colonizations = gameView.getColonizableSystemIds()
			.stream()
			.map(csi -> new EntityModel<>(new MainPageDto.ColonizationDto(csi, gameView.hasColonizationCommand(csi)))
				.with(spotlightAction.apply(csi)))
			.toList();

		mainPage.annexations = gameView.getAnnexableSystemIds()
			.stream()
			.map(asi -> new EntityModel<>(new MainPageDto.AnnexationDto(asi, gameView.hasAnnexationCommand(asi)))
				.with(spotlightAction.apply(asi)))
			.toList();

		return new EntityModel<>(mainPage).with(!context.getGameView().getSelectTechs().isEmpty(),
				() -> Action.get("select-tech", context.toFrontendUri("select-tech-page"))
					.with("selectedStarId", selectedSystemId.getValue()));
	}

	private static SystemId fromSelectedStar(GameView gameView, MainPageState state, MainPageDto mainPage,
			FrontendContext context) {
		SystemView selectedSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());

		mainPage.starMap.getContent().starSelection = new StarSelectionDto(selectedSystem.getId(),
				selectedSystem.getLocation().getX(), selectedSystem.getLocation().getY());

		boolean colonization = (state.isStarInspectionState() && selectedSystem.isColonizable())
				|| (state.isStarSpotlightState()
						&& gameView.getColonizableSystemIds().contains(state.getSelectedSystemId().get()));
		boolean annexation = (state.isStarInspectionState() && selectedSystem.getColonyView()
			.flatMap(ColonyView::getAnnexationStatus)
			.flatMap(AnnexationStatus::annexable)
			.orElse(Boolean.FALSE))
				|| (state.isStarSpotlightState()
						&& gameView.getAnnexableSystemIds().contains(state.getSelectedSystemId().get()));

		Function<SystemView, HabitabilityDto> habitabilityDtoSupplier = (system) -> new HabitabilityDto(
				system.getPlanetType().get(), system.getPlanetSpecial().get(), system.getPlanetMaxPopulation().get());

		if ((state.isStarInspectionState() || state.isTransferColonistsState() || state.isRelocateShipsState())
				&& !(colonization || annexation)) {
			if (selectedSystem.getStarName().isPresent()) {
				Optional<Integer> destinationEta = Optional.empty();
				SystemView inspectedSystem = selectedSystem;
				if ((state.isTransferColonistsState() && !state.getSelectedSystemId()
					.get()
					.equals(state.asTransferColonistsState().getTransferSystemId()))
						|| (state.isRelocateShipsState() && !state.getSelectedSystemId()
							.get()
							.equals(state.asRelocateShipsState().getRelocateSystemId()))) {
					SystemView destinationSystem = gameView.getSystem(
							state.isTransferColonistsState() ? state.asTransferColonistsState().getTransferSystemId()
									: state.asRelocateShipsState().getRelocateSystemId());
					inspectedSystem = destinationSystem;

					destinationEta = context.getPlayerGame()
						.calcTranportColonistsEta(selectedSystem.getId(), destinationSystem.getId())
						.filter(eta -> destinationSystem.getColonyView(gameView.getPlayer()).isPresent());

					mainPage.starMap.getContent().starSelection.itinerary = Optional
						.of(new ItineraryDto(destinationSystem.getLocation().getX(),
								destinationSystem.getLocation().getY(), selectedSystem.getLocation().getX(),
								selectedSystem.getLocation().getY(), false, false, destinationEta.isPresent(), false));
				}

				mainPage.inspector.systemDetails = new SystemDetailsDto(inspectedSystem.getStarName().get(),
						habitabilityDtoSupplier.apply(inspectedSystem),
						inspectedSystem.getColonyView()
							.map(c -> new ColonyDto(
									Optional.ofNullable(c.getPlayer() != gameView.getPlayer()
											? new ForeignColonyOwner(c.getRace(), c.getPlayer()) : null),
									c.getPopulation(), c.getRatios().map(r -> new ProductionDto(42, 78)),
									c.getAnnexationStatus().flatMap(AnnexationStatus::roundsUntilAnnexable),
									c.getAnnexationStatus()
										.filter(as -> context.getPlayer() == c.getPlayer())
										.flatMap(AnnexationStatus::siegingPlayer),
									c.getAnnexationStatus()
										.filter(as -> context.getPlayer() == c.getPlayer())
										.flatMap(AnnexationStatus::siegingRace))),
						inspectedSystem.getColonyView()
							.filter(c -> c.getPlayer() == gameView.getPlayer() && !state.isTransferColonistsState()
									&& !state.isRelocateShipsState())
							.map(c -> new EntityModel<>(new AllocationsDto(c.getId().getValue(), Map.of( //
									"ship", new AllocationCategoryDto(10, "None"), //
									"defence", new AllocationCategoryDto(15, "None"), //
									"industry", new AllocationCategoryDto(20, "2.7/y"), //
									"ecology", new AllocationCategoryDto(25, "Clean"), //
									"technology", new AllocationCategoryDto(30, "0RP"))))
								.with(Action
									.jsonPost("allocate-spending",
											context.toFrontendUri("main-page", "inspector", "spendings"))
									.with("selectedStarId", selectedSystem.getId().getValue()))),
						selectedSystem.getColonyView()
							.filter(c -> c.getPlayer() == gameView.getPlayer() && !state.isTransferColonistsState()
									&& !state.isRelocateShipsState())
							.map(c -> new EntityModel<>(
									new BuildQueueDto(c.getSpaceDock().get().getName(),
											c.getSpaceDock().get().getSize(), gameView.getPlayer(), 1))
								.with(Action
									.jsonPost("next-ship-type",
											context.toFrontendUri("main-page", "inspector", "ship-types"))
									.with("selectedStarId", selectedSystem.getId().getValue())
									.with("colonyId", c.getId().getValue()))
								.with(Action.get("relocate-ships", context.toFrontendUri("main-page"))
									.with("selectedStarId", selectedSystem.getId().getValue())
									.with("relocateStarId", selectedSystem.getId().getValue()))
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
									destinationEta))
								.with(Action.get("cancel", context.toFrontendUri("main-page"))
									.with("selectedStarId", selectedSystem.getId().getValue()))
								.with(destinationEta.isPresent(),
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
						state.isRelocateShipsState()
								? Optional.of(
										new EntityModel<>(new RelocateShipsDto(destinationEta))
											.with(Action.get("cancel", context.toFrontendUri("main-page"))
												.with("selectedStarId", selectedSystem.getId().getValue()))
											.with(Action
												.jsonPost("relocate",
														context.toFrontendUri("main-page", "inspector",
																"ship-relocations"))
												.with("selectedStarId", selectedSystem.getId().getValue())
												.with("relocateColonyId",
														state.asRelocateShipsState()
															.getRelocateSystemId()
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
				mainPage.inspector.colonization = new EntityModel<>(new ColonizationDto(
						selectedSystem.getStarName().get(), habitabilityDtoSupplier.apply(selectedSystem),
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
				mainPage.inspector.annexation = new EntityModel<>(new AnnexationDto(selectedSystem.getStarName().get(),
						habitabilityDtoSupplier.apply(selectedSystem),
						Optional.ofNullable(state.isStarSpotlightState() ? null
								: selectedSystem.getColonyView()
									.get()
									.getAnnexationStatus()
									.get()
									.annexationCommand()
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

	private static SystemId fromSelectedFleet(GameView gameView, MainPageState state, Optional<ShipsView> maybeShips,
			MainPageDto mainPage, FrontendContext context) {
		FleetView selectedFleet = gameView.getFleet(state.getSelectedFleetId().orElseThrow());
		SystemId selectedSystemId = selectedFleet.getOrbiting().or(selectedFleet::getClosest).orElseThrow();

		ShipsView ships = maybeShips.orElseThrow();
		mainPage.starMap.getContent().fleetSelection = new FleetSelectionDto(selectedFleet.getId(),
				selectedFleet.getLocation().getX(), selectedFleet.getLocation().getY(), selectedFleet.isDeployable(),
				selectedFleet.getOrbiting().isPresent(), selectedFleet.getOrbiting().map(SystemId::getValue),
				selectedFleet.isJustLeaving());

		ShipsView totalShips = gameView.getFleet(selectedFleet.getId()).getShips();

		if (state.isFleetInspectionState()) {
			if (selectedFleet.isDeployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(new FleetDeploymentDto(selectedFleet.getId(),
						gameView.getRound(), selectedFleet.getPlayer(), Optional.empty(), Optional.empty(), false,
						toDtoShipList(ships, Optional.of(totalShips))));
			}
			else {
				mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.getPlayer(),
						selectedFleet.getRace(), Optional.empty(), toDtoShipList(ships, Optional.of(totalShips))));
			}
		}
		else if (state.isFleetDeploymentState()) {
			SystemView selectedSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());
			Optional<Integer> eta = context.getPlayerGame()
				.calcEta(selectedFleet.getId(), selectedSystem.getId(), ships);

			if (selectedFleet.isDeployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(
						new FleetDeploymentDto(selectedFleet.getId(), gameView.getRound(), selectedFleet.getPlayer(),
								eta, eta.isPresent() ? Optional.empty() : selectedSystem.getRange(), true,
								toDtoShipList(ships, Optional.of(totalShips))))
					.with(selectedFleet.isDeployable() && eta.isPresent(),
							() -> Action
								.jsonPost("deploy", context.toFrontendUri("main-page", "inspector", "deployments"))
								.with("selectedFleetId", selectedFleet.getId().getValue())
								.with("selectedStarId", selectedSystem.getId().getValue())
								.with(ships.getTypesWithCount()
									.stream()
									.map(twc -> new ActionField(twc.getKey().getId().getValue(), twc.getValue()))));
			}
			else {
				mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.getPlayer(),
						selectedFleet.getRace(), eta, toDtoShipList(ships, Optional.empty())));
			}

			mainPage.starMap.getContent().fleetSelection.itinerary = Optional.of(new ItineraryDto(
					selectedFleet.getLocation().getX(), selectedFleet.getLocation().getY(),
					selectedSystem.getLocation().getX(), selectedSystem.getLocation().getY(),
					selectedFleet.getOrbiting().isPresent(), selectedFleet.isJustLeaving(), eta.isPresent(), false));
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
							() -> maybeShips.orElseThrow()
								.getTypesWithCount()
								.stream()
								.map(twc -> new ActionField(twc.getKey().getId().getValue(), twc.getValue()))));
		}

		return selectedSystemId;
	}

	static List<ShipsDto> toDtoShipList(ShipsView ships, Optional<ShipsView> totalShips) {
		return ships.getTypesWithCount()
			.stream()
			.map(e -> new ShipsDto(e.getKey().getId().getValue(), e.getKey().getName(), e.getKey().getSize(),
					e.getValue(), totalShips.map(ts -> ts.getCountByType(e.getKey()))))
			.sorted((first, second) -> first.name.compareTo(second.name))
			.collect(Collectors.toList());
	}

	static ItineraryDto toItinerary(SystemView originSystem, ColonyId destinationColony, GameView gameView) {
		SystemView destinationSystem = gameView.getSystem(SystemId.fromColonyId(destinationColony));
		return new ItineraryDto(originSystem.getLocation().getX(), originSystem.getLocation().getY(),
				destinationSystem.getLocation().getX(), destinationSystem.getLocation().getY(), false, false, true,
				true);
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
				.map(fba -> new FleetDto(fba.getId(), finalPlayer, Optional.of(fba.getLocation().getX()),
						Optional.of(fba.getLocation().getY()), Optional.of(fba.isJustLeaving()),
						combatSystem.getLocation().getX(), combatSystem.getLocation().getY(), finalOrbiting, false,
						Optional.of(fba.getSpeed()), Optional.of(fba.getHorizontalDirection()), List.of()))
				.toList());

			if (sc.getOutcome() == Outcome.ATTACKER_WON && sc.getDefenderFleet().isPresent()) {
				destroyedFleetDtos.add(new FleetDto(sc.getDefenderFleet().get(), finalPlayer, Optional.empty(),
						Optional.empty(), Optional.empty(), combatSystem.getLocation().getX(),
						combatSystem.getLocation().getY(), true, false, Optional.empty(), Optional.empty(), List.of()));
			}
		}

		return new MainPageDto.SpaceCombatDto(destroyedFleetDtos.stream().map(f -> new EntityModel<>(f)).toList());
	}

}
