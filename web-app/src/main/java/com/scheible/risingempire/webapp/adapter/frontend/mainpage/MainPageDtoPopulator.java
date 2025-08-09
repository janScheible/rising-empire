package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.AllocationView;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusView;
import com.scheible.risingempire.game.api.view.colony.ColonistTransferView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalView;
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
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.TransportsDto;
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

		MainPageDto mainPage = new MainPageDto(gameView.round(), gameView.player(),
				new TurnStatusDto(gameView.turnFinishedStatus().get(gameView.player()),
						gameView.turnFinishedStatus()
							.entrySet()
							.stream()
							.map(tfs -> TurnFinishedStatusPlayerDto.fromPlayer(tfs.getKey(), tfs.getValue()))
							.collect(Collectors.toList())),
				new StarMapDto(gameView.galaxyWidth(), gameView.galaxyHeight()),
				new MainPageDto.StateDescriptionDto(state.getClass().getSimpleName()));

		mainPage.starMap.getContent().ranges = new RangesDto(gameView.galaxyWidth(), gameView.galaxyHeight(),
				gameView.player());

		mainPage.starMap.getContent().ranges.fleetRanges.addAll(gameView.systems()
			.values()
			.stream()
			.filter(s -> s.fleetRange().isPresent() && s.extendedFleetRange().isPresent())
			.map(s -> new FleetRangeDto("fleetRange@" + s.id().value() + "r" + s.fleetRange().orElseThrow(),
					s.location().x(), s.location().y(), s.fleetRange().orElseThrow(),
					s.extendedFleetRange().orElseThrow()))
			.collect(Collectors.toList()));

		mainPage.starMap.getContent().ranges.colonyScannerRanges.addAll(gameView.systems()
			.values()
			.stream()
			.filter(s -> s.scannerRange().isPresent())
			.map(s -> new ScannerRangeDto("scannerRange@" + s.id().value(), s.location().x(), s.location().y(),
					s.scannerRange().get()))
			.collect(Collectors.toList()));

		mainPage.starMap.getContent().ranges.fleetScannerRanges.addAll(gameView.fleets()
			.values()
			.stream()
			.filter(f -> f.scannerRange().isPresent())
			.map(f -> new ScannerRangeDto("scannerRange@" + f.id().value(), f.location().x(), f.location().y(),
					f.scannerRange().orElseThrow()))
			.collect(Collectors.toList()));

		SystemId turnSelectedSystemId = null;
		if (state.getSelectedSystemId().isPresent()) {
			turnSelectedSystemId = fromSelectedStar(gameView, state, mainPage, context);
		}

		Optional<ShipsView> stateShips = state.isFleetDeploymentState() ? state.asFleetDeploymentState().getShips()
				: state.isFleetInspectionState() ? state.asFleetInspectionState().getShips() : Optional.empty();
		Optional<ShipsView> maybeShips = stateShips
			.or(() -> state.getSelectedFleetId().map(id -> gameView.fleet(id).ships()));

		if (state.getSelectedFleetId().isPresent()) {
			// we always need a selected system --> derive it for example from the
			// currently selected fleet
			turnSelectedSystemId = fromSelectedFleet(gameView, state, maybeShips, mainPage, context);
		}
		SystemId selectedSystemId = turnSelectedSystemId;

		{
			mainPage.buttonBar = new EntityModel<>(new ButtonBarDto())
				.with(Action.jsonPost("finish-turn", context.toFrontendUri("main-page", "button-bar", "finished-turns"))
					.with("selectedStarId", selectedSystemId.value())
					.with("round", gameView.round()))
				.with(Action.get("show-tech-page", context.toFrontendUri("tech-page"))
					.with("selectedStarId", selectedSystemId.value()));
		}

		mainPage.starMap.getContent().stars = gameView.systems()
			.values()
			.stream()
			.map(s -> new EntityModel<>(new StarDto(s.id(), s.starName(), s.starType(), s.small(),
					s.colony().map(ColonyView::player),
					s.colony().flatMap(ColonyView::annexationStatus).flatMap(AnnexationStatusView::siegingPlayer),
					s.colony().flatMap(ColonyView::annexationStatus).flatMap(AnnexationStatusView::progress),
					s.location().x(), s.location().y(),
					s.colony().flatMap(ColonyView::relocationTarget).map(rt -> toItinerary(s, rt, gameView))))
				.with(state.isSystemSelectable(s.id()), () -> Action.get("select", context.toFrontendUri("main-page"))
					.with("selectedStarId",
							state.isTransferColonistsState() || state.isRelocateShipsState()
									? state.getSelectedSystemId().get().value() : s.id().value())
					.with(state.isTransferColonistsState(), "transferStarId", () -> s.id().value())
					.with(state.isRelocateShipsState(), "relocateStarId", () -> s.id().value())
					.with(state.getSelectedFleetId().isPresent()
							&& gameView.fleet(state.getSelectedFleetId().orElseThrow()).deployable(), "selectedFleetId",
							() -> state.getSelectedFleetId().map(FleetId::value).orElseThrow())
					.with(maybeShips.isPresent(),
							() -> maybeShips.orElseThrow()
								.typesWithCount()
								.stream()
								.map(twc -> new ActionField(twc.getKey().id().value(), twc.getValue())))))
			.collect(Collectors.toList());

		Predicate<FleetId> isSpaceCombatAttacker = fleetId -> gameView.spaceCombats()
			.stream()
			.anyMatch(sc -> sc.attackerFleets().stream().anyMatch(af -> af.id().equals(fleetId)));

		mainPage.starMap
			.getContent().fleets = gameView.fleets()
				.values()
				.stream()
				.map(fleet -> new EntityModel<>(new FleetDto(fleet.id(), fleet.player(), fleet.colonistTransporters(),
						fleet.previousLocation().map(Location::x), fleet.previousLocation().map(Location::y),
						fleet.previousJustLeaving(), fleet.location().x(), fleet.location().y(),
						fleet.type() == FleetViewType.ORBITING, fleet.justLeaving(), fleet.speed(),
						fleet.horizontalDirection(),
						fleet.fleetsBeforeArrival()
							.stream()
							.map(fba -> new EntityModel<>(new FleetDto(fba.id(), fleet.player(), false,
									Optional.of(fba.location().x()), Optional.of(fba.location().y()), fba.justLeaving(),
									fleet.location().x(), fleet.location().y(), !isSpaceCombatAttacker.test(fba.id()),
									fba.justLeaving(), Optional.of(fba.speed()), Optional.of(fba.horizontalDirection()),
									List.of())))
							.toList()))
					.with(state.isFleetSelectable(fleet.id()),
							() -> Action.get("select", context.toFrontendUri("main-page"))
								.with(state.isFleetSelectable(fleet.id()), "selectedFleetId", () -> fleet.id().value())
								.with(fleet.destination().isPresent(), "selectedStarId",
										() -> fleet.destination().get().value())))
				.collect(Collectors.groupingBy(fleetDto -> {
					FleetView fleet = gameView.fleet(new FleetId(fleetDto.getContent().id));
					return fleet.parentId().orElse(fleet.id()).value();
				}));

		mainPage.starMap.getContent().starNotifications = gameView.systems()
			.values()
			.stream()
			.map(SystemView::notifications)
			.flatMap(Collection::stream)
			.flatMap(sn -> sn.messages().stream().map(message -> {
				SystemView notificationSystem = gameView.system(sn.systemId());
				return new StarNotificationDto(notificationSystem.id(), notificationSystem.location().x(),
						notificationSystem.location().y(), message);
			}))
			.toList();

		Function<SystemId, Action> spotlightAction = (systemId) -> Action
			.get("spotlight", context.toFrontendUri("main-page"))
			.with("selectedStarId", selectedSystemId.value())
			.with("spotlightedStarId", systemId.value());

		mainPage.spaceCombats = gameView.spaceCombats()
			.stream()
			.map(sc -> new EntityModel<>(toSpaceCombat(sc, gameView.system(sc.systemId())))
				.with(spotlightAction.apply(sc.systemId())))
			.toList();

		mainPage.explorations = gameView.justExploredSystemIds()
			.stream()
			.map(jesi -> new EntityModel<>(new MainPageDto.ExplorationDto(jesi)).with(spotlightAction.apply(jesi)))
			.toList();

		mainPage.colonizations = gameView.colonizableSystemIds()
			.stream()
			.map(csi -> new EntityModel<>(new MainPageDto.ColonizationDto(csi, gameView.colonizationCommand(csi)))
				.with(spotlightAction.apply(csi)))
			.toList();

		mainPage.annexations = gameView.annexableSystemIds()
			.stream()
			.map(asi -> new EntityModel<>(new MainPageDto.AnnexationDto(asi, gameView.annexationCommand(asi)))
				.with(spotlightAction.apply(asi)))
			.toList();

		return new EntityModel<>(mainPage)
			.with(!gameView.selectTechGroups().isEmpty(),
					() -> Action.get("select-tech", context.toFrontendUri("select-tech-page"))
						.with("selectedStarId", selectedSystemId.value()))
			.with(!gameView.newShips().isEmpty(),
					() -> Action.get("show-new-ships", context.toFrontendUri("new-ships-page"))
						.with("selectedStarId", selectedSystemId.value()));
	}

	private static SystemId fromSelectedStar(GameView gameView, MainPageState state, MainPageDto mainPage,
			FrontendContext context) {
		SystemView selectedSystem = gameView.system(state.getSelectedSystemId().orElseThrow());

		mainPage.starMap.getContent().starSelection = new StarSelectionDto(selectedSystem.id(),
				selectedSystem.location().x(), selectedSystem.location().y());

		boolean colonization = (state.isStarInspectionState() && selectedSystem.colonizable())
				|| (state.isStarSpotlightState()
						&& gameView.colonizableSystemIds().contains(state.getSelectedSystemId().get()));
		boolean annexation = (state.isStarInspectionState() && selectedSystem.colony()
			.flatMap(ColonyView::annexationStatus)
			.map(AnnexationStatusView::annexable)
			.orElse(Boolean.FALSE))
				|| (state.isStarSpotlightState()
						&& gameView.annexableSystemIds().contains(state.getSelectedSystemId().get()));

		Function<SystemView, HabitabilityDto> habitabilityDtoSupplier = (system) -> new HabitabilityDto(
				system.planetType().get(), system.planetSpecial().get(), system.planetMaxPopulation().get());

		if ((state.isStarInspectionState() || state.isTransferColonistsState() || state.isRelocateShipsState())
				&& !(colonization || annexation)) {
			if (selectedSystem.starName().isPresent()) {
				Optional<Integer> destinationEta = Optional.empty();
				SystemView inspectedSystem = selectedSystem;
				int transferWarningThreshold = 0;

				if ((state.isTransferColonistsState() && !state.getSelectedSystemId()
					.get()
					.equals(state.asTransferColonistsState().getTransferSystemId()))
						|| (state.isRelocateShipsState() && !state.getSelectedSystemId()
							.get()
							.equals(state.asRelocateShipsState().getRelocateSystemId()))) {
					SystemView destinationSystem = gameView.system(
							state.isTransferColonistsState() ? state.asTransferColonistsState().getTransferSystemId()
									: state.asRelocateShipsState().getRelocateSystemId());
					inspectedSystem = destinationSystem;

					destinationEta = context.getPlayerGame()
						.calcTranportColonistsEta(selectedSystem.id(), destinationSystem.id())
						.filter(eta -> destinationSystem.colony(gameView.player()).isPresent());

					transferWarningThreshold = destinationSystem.colony()
						.map(ColonyView::population)
						.flatMap(population -> destinationSystem.planetMaxPopulation()
							.map(maxPopulation -> maxPopulation - population))
						.orElse(0);

					mainPage.starMap.getContent().starSelection.itinerary = Optional
						.of(new ItineraryDto(destinationSystem.location().x(), destinationSystem.location().y(),
								selectedSystem.location().x(), selectedSystem.location().y(), false, false,
								destinationEta.isPresent(), false));
				}

				mainPage.inspector.systemDetails = new SystemDetailsDto(inspectedSystem.starName().get(),
						habitabilityDtoSupplier.apply(inspectedSystem),
						inspectedSystem.colony()
							.map(c -> new ColonyDto(
									Optional.ofNullable(c.player() != gameView.player()
											? new ForeignColonyOwner(c.race(), c.player()) : null),
									c.population(), c.outdated(), c.allocations().map(r -> new ProductionDto(42, 78)),
									c.annexationStatus().flatMap(AnnexationStatusView::roundsUntilAnnexable),
									c.annexationStatus()
										.filter(as -> context.getPlayer() == c.player())
										.flatMap(AnnexationStatusView::siegingPlayer),
									c.annexationStatus()
										.filter(as -> context.getPlayer() == c.player())
										.flatMap(AnnexationStatusView::siegingRace))),
						inspectedSystem.colony()
							.filter(c -> c.player() == gameView.player() && !state.isTransferColonistsState()
									&& !state.isRelocateShipsState())
							.map(c -> new EntityModel<>(toAllocationsDto(c.id().value(), c.allocations().orElseThrow()))
								.with(Action
									.jsonPost("allocate-spending",
											context.toFrontendUri("main-page", "inspector", "spendings"))
									.with("selectedStarId", selectedSystem.id().value()))),
						selectedSystem.colony()
							.filter(c -> c.player() == gameView.player() && !state.isTransferColonistsState()
									&& !state.isRelocateShipsState())
							.map(c -> new EntityModel<>(new BuildQueueDto(c.spaceDock().get().current().name(),
									c.spaceDock().get().current().size(), gameView
										.player(),
									c.spaceDock().get().count()))
								.with(Action
									.jsonPost("next-ship-type",
											context.toFrontendUri("main-page", "inspector", "ship-types"))
									.with("selectedStarId", selectedSystem.id().value())
									.with("colonyId", c.id().value()))
								.with(Action.get("relocate-ships", context.toFrontendUri("main-page"))
									.with("selectedStarId", selectedSystem.id().value())
									.with("relocateStarId", selectedSystem.id().value()))
								.with(Action.get("transfer-colonists", context.toFrontendUri("main-page"))
									.with("selectedStarId", selectedSystem.id().value())
									.with("transferStarId",
											c.colonistTransfer()
												.map(ColonistTransferView::desination)
												.map(SystemId::fromColonyId)
												.orElse(selectedSystem.id())
												.value()))),
						state.isTransferColonistsState()
								? Optional.of(new EntityModel<>(new TransferColonistsDto(
										selectedSystem.colony()
											.get()
											.colonistTransfer()
											.map(ColonistTransferView::colonists)
											.orElse(0),
										selectedSystem.colony().get().maxTransferPopulation(), transferWarningThreshold,
										destinationEta))
									.with(Action.get("cancel", context.toFrontendUri("main-page"))
										.with("selectedStarId", selectedSystem.id().value()))
									.with(destinationEta.isPresent(),
											() -> Action
												.jsonPost("transfer",
														context.toFrontendUri("main-page", "inspector",
																"colonist-transfers"))
												.with("selectedStarId", selectedSystem.id().value())
												.with("colonists", 0)
												.with("transferColonyId",
														state.asTransferColonistsState()
															.getTransferSystemId()
															.toColonyId()
															.value())))
								: Optional.empty(),
						state.isRelocateShipsState()
								? Optional.of(
										new EntityModel<>(new RelocateShipsDto(destinationEta))
											.with(Action.get("cancel", context.toFrontendUri("main-page"))
												.with("selectedStarId", selectedSystem.id().value()))
											.with(Action
												.jsonPost("relocate",
														context.toFrontendUri("main-page", "inspector",
																"ship-relocations"))
												.with("selectedStarId", selectedSystem.id().value())
												.with("relocateColonyId",
														state.asRelocateShipsState()
															.getRelocateSystemId()
															.toColonyId()
															.value())))
								: Optional.empty(),
						selectedSystem.closestColony());

			}
			else {
				mainPage.inspector.unexplored = new UnexploredDto(selectedSystem.starType(),
						selectedSystem.closestColony().orElseThrow());
			}
		}
		else if (colonization || annexation) {
			FleetId orbiting = gameView.orbiting(selectedSystem.id()).get().id();

			if (colonization) {
				mainPage.inspector.colonization = new EntityModel<>(new ColonizationDto(selectedSystem.starName().get(),
						habitabilityDtoSupplier.apply(selectedSystem),
						Optional.ofNullable(state.isStarSpotlightState() ? null : selectedSystem.colonizeCommand())))
					.with(Action.jsonPost("colonize", context.toFrontendUri("main-page", "inspector", "colonizations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().value()
										: selectedSystem.id().value())
						.with("fleetId", orbiting.value())
						.with("skip", Boolean.FALSE))
					.with(Action.jsonPost("cancel", context.toFrontendUri("main-page", "inspector", "colonizations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().value()
										: selectedSystem.id().value())
						.with("fleetId", orbiting.value())
						.with("skip", Boolean.TRUE));
			}
			else if (annexation) {
				mainPage.inspector.annexation = new EntityModel<>(new AnnexationDto(selectedSystem.starName().get(),
						habitabilityDtoSupplier.apply(selectedSystem),
						Optional.ofNullable(state.isStarSpotlightState() ? null
								: selectedSystem.colony()
									.flatMap(ColonyView::annexationStatus)
									.map(AnnexationStatusView::annexationCommand)
									.orElse(Boolean.FALSE))))
					.with(Action.jsonPost("annex", context.toFrontendUri("main-page", "inspector", "annexations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().value()
										: selectedSystem.id().value())
						.with("fleetId", orbiting.value())
						.with("skip", Boolean.FALSE))
					.with(Action.jsonPost("cancel", context.toFrontendUri("main-page", "inspector", "annexations"))
						.with("selectedStarId",
								state.isStarSpotlightState()
										? state.asStarSpotlightState().getActualSelectedSystemId().value()
										: selectedSystem.id().value())
						.with("fleetId", orbiting.value())
						.with("skip", Boolean.TRUE));
			}
		}
		else if (state.isStarSpotlightState()) {
			if (gameView.spaceCombats()
				.stream()
				.map(SpaceCombatView::systemId)
				.anyMatch(s -> s.equals(state.getSelectedSystemId().get()))) {
				SpaceCombatView spaceCombat = gameView.spaceCombats()
					.stream()
					.filter(sc -> sc.systemId().equals(selectedSystem.id()))
					.findAny()
					.orElseThrow();

				mainPage.inspector.spaceCombat = new EntityModel<>(new SpaceCombatDto(selectedSystem.starName()
					.map(name -> new SpaceCombatSystem(name,
							new HabitabilityDto(selectedSystem.planetType().get(), selectedSystem.planetSpecial().get(),
									selectedSystem.planetMaxPopulation().get()))),
						spaceCombat.attacker(), spaceCombat.attackerPlayer(), spaceCombat.defender(),
						spaceCombat.defenderPlayer()))
					.with(Action
						.get("continue",
								context.toFrontendUri("space-combat-page", state.getSelectedSystemId().get().value()))
						.with("selectedStarId", state.asStarSpotlightState().getActualSelectedSystemId().value()));
			}
			else if (gameView.justExploredSystemIds().contains(state.getSelectedSystemId().get())) {
				mainPage.inspector.exploration = new EntityModel<>(new ExplorationDto(
						selectedSystem.starName().orElseThrow(),
						new HabitabilityDto(selectedSystem.planetType().get(), selectedSystem.planetSpecial().get(),
								selectedSystem.planetMaxPopulation().get())))
					.with(Action.get("continue", context.toFrontendUri("main-page"))
						.with("selectedStarId", state.asStarSpotlightState().getActualSelectedSystemId().value()));
			}
		}

		return selectedSystem.id();
	}

	private static SystemId fromSelectedFleet(GameView gameView, MainPageState state, Optional<ShipsView> maybeShips,
			MainPageDto mainPage, FrontendContext context) {
		FleetView selectedFleet = gameView.fleet(state.getSelectedFleetId().orElseThrow());
		SystemId selectedSystemId = selectedFleet.orbiting().or(selectedFleet::closest).orElseThrow();

		ShipsView ships = maybeShips.orElseThrow();
		mainPage.starMap.getContent().fleetSelection = new FleetSelectionDto(selectedFleet.id(),
				selectedFleet.location().x(), selectedFleet.location().y(), selectedFleet.deployable(),
				selectedFleet.orbiting().isPresent(), selectedFleet.orbiting().map(SystemId::value),
				selectedFleet.justLeaving());

		ShipsView totalShips = gameView.fleet(selectedFleet.id()).ships();

		if (state.isFleetInspectionState()) {
			if (selectedFleet.deployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(new FleetDeploymentDto(selectedFleet.id(),
						gameView.round(), selectedFleet.player(), Optional.empty(), Optional.empty(), false,
						toDtoShipList(ships, Optional.of(totalShips))));
			}
			else {
				if (!selectedFleet.colonistTransporters()) {
					mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.player(),
							selectedFleet.race(), Optional.empty(), toDtoShipList(ships, Optional.of(totalShips))));
				}
				else {
					int transports = selectedFleet.ships()
						.ships()
						.entrySet()
						.stream()
						.map(Entry::getValue)
						.findFirst()
						.orElseThrow();
					mainPage.inspector.transports = new EntityModel<>(new TransportsDto(selectedFleet.player(),
							selectedFleet.race(), transports, Optional.empty()));
				}
			}
		}
		else if (state.isFleetDeploymentState()) {
			SystemView selectedSystem = gameView.system(state.getSelectedSystemId().orElseThrow());
			Optional<Integer> eta = context.getPlayerGame().calcEta(selectedFleet.id(), selectedSystem.id(), ships);

			if (selectedFleet.deployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(
						new FleetDeploymentDto(selectedFleet.id(), gameView.round(), selectedFleet.player(), eta,
								eta.isPresent() ? Optional.empty() : selectedSystem.closestColony(), true,
								toDtoShipList(ships, Optional.of(totalShips))))
					.with(selectedFleet.deployable() && eta.isPresent(),
							() -> Action
								.jsonPost("deploy", context.toFrontendUri("main-page", "inspector", "deployments"))
								.with("selectedFleetId", selectedFleet.id().value())
								.with("selectedStarId", selectedSystem.id().value())
								.with(ships.typesWithCount()
									.stream()
									.map(twc -> new ActionField(twc.getKey().id().value(), twc.getValue()))));
			}
			else {
				if (!selectedFleet.colonistTransporters()) {
					mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.player(),
							selectedFleet.race(), eta, toDtoShipList(ships, Optional.empty())));
				}
				else {
					int transports = selectedFleet.ships()
						.ships()
						.entrySet()
						.stream()
						.map(Entry::getValue)
						.findFirst()
						.orElseThrow();
					mainPage.inspector.transports = new EntityModel<>(
							new TransportsDto(selectedFleet.player(), selectedFleet.race(), transports, eta));
				}
			}

			mainPage.starMap.getContent().fleetSelection.itinerary = Optional
				.of(new ItineraryDto(selectedFleet.location().x(), selectedFleet.location().y(),
						selectedSystem.location().x(), selectedSystem.location().y(),
						selectedFleet.orbiting().isPresent(), selectedFleet.justLeaving(), eta.isPresent(), false));
		}

		if (mainPage.inspector.fleetDeployment != null) {
			mainPage.inspector.fleetDeployment
				.with(Action.get("cancel", context.toFrontendUri("main-page"))
					.with("selectedStarId",
							selectedFleet.orbiting().orElseGet(() -> selectedFleet.source().orElseThrow()).value()))
				.with(context.toNamedAction("assign-ships", HttpMethod.GET, true, true, "main-page")
					.with(maybeShips.isPresent(),
							() -> maybeShips.orElseThrow()
								.typesWithCount()
								.stream()
								.map(twc -> new ActionField(twc.getKey().id().value(), twc.getValue()))));
		}

		return selectedSystemId;
	}

	static List<ShipsDto> toDtoShipList(ShipsView ships, Optional<ShipsView> totalShips) {
		return ships.typesWithCount()
			.stream()
			.map(e -> new ShipsDto(e.getKey().id().value(), e.getKey().name(), e.getKey().size(), e.getValue(),
					totalShips.map(ts -> ts.countByType(e.getKey()))))
			.sorted((first, second) -> first.name.compareTo(second.name))
			.collect(Collectors.toList());
	}

	static ItineraryDto toItinerary(SystemView originSystem, ColonyId destinationColony, GameView gameView) {
		SystemView destinationSystem = gameView.system(SystemId.fromColonyId(destinationColony));
		return new ItineraryDto(originSystem.location().x(), originSystem.location().y(),
				destinationSystem.location().x(), destinationSystem.location().y(), false, false, true, true);
	}

	static MainPageDto.SpaceCombatDto toSpaceCombat(SpaceCombatView sc, SystemView combatSystem) {
		Player player = null;
		Set<FleetBeforeArrivalView> fleets = Set.of();
		boolean orbiting = false;

		if (sc.outcome() == Outcome.ATTACKER_WON) {
			player = sc.defenderPlayer();
			fleets = sc.defenderFleetsBeforeArrival();
			orbiting = true;
		}
		else if (sc.outcome() == Outcome.DEFENDER_WON) {
			player = sc.attackerPlayer();
			fleets = sc.attackerFleets();
			orbiting = false;
		}

		final Player finalPlayer = player;
		final boolean finalOrbiting = orbiting;
		List<FleetDto> destroyedFleetDtos = new ArrayList<>();

		if (player != null) {
			destroyedFleetDtos.addAll(fleets.stream()
				.map(fba -> new FleetDto(fba.id(), finalPlayer, false, Optional.of(fba.location().x()),
						Optional.of(fba.location().y()), fba.justLeaving(), combatSystem.location().x(),
						combatSystem.location().y(), finalOrbiting, false, Optional.of(fba.speed()),
						Optional.of(fba.horizontalDirection()), List.of()))
				.toList());

			if (sc.outcome() == Outcome.ATTACKER_WON && sc.defenderFleet().isPresent()) {
				destroyedFleetDtos.add(new FleetDto(sc.defenderFleet().get(), finalPlayer, false, Optional.empty(),
						Optional.empty(), false, combatSystem.location().x(), combatSystem.location().y(), true, false,
						Optional.empty(), Optional.empty(), List.of()));
			}
		}

		return new MainPageDto.SpaceCombatDto(destroyedFleetDtos.stream().map(f -> new EntityModel<>(f)).toList());
	}

	private static AllocationsDto toAllocationsDto(String id, Map<ProductionArea, AllocationView> allocations) {
		Map<String, AllocationCategoryDto> categories = Stream.of(ProductionArea.values())
			.collect(Collectors.toMap(pa -> pa.name().toLowerCase(Locale.ROOT), pa -> {
				AllocationView allocation = allocations.get(pa);
				return new AllocationCategoryDto(allocation.percentage(), allocation.status());
			}));

		return new AllocationsDto(id, categories);
	}

}
