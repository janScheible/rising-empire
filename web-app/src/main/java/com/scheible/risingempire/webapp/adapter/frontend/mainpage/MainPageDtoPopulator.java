package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import static java.util.Collections.emptyMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpMethod;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusView;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
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
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.ShipsDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.SpaceCombatDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.SpaceCombatSystem;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.SystemDetailsDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.InspectorDto.UnexploredDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPageDto.ButtonBarDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.MainPageDto.TurnStatusDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.FleetDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.FleetRangeDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.FleetSelectionDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.ItineraryDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.RangesDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.ScannerRangeDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.ScrollToDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.StarDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.StarNotificationDto;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.StarMapDto.StarSelectionDto;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

/**
 *
 * @author sj
 */
public class MainPageDtoPopulator {

	static EntityModel<MainPageDto> populate(final FrontendContext context, final MainPageState state,
			final Stream<ActionField> steps) {
		final GameView gameView = context.getGameView();

		final MainPageDto mainPage = new MainPageDto(state.isFleetMovementState(), gameView.getRound(),
				new TurnStatusDto(state.isTurnFinishedState(),
						gameView.getTurnFinishedStatus().entrySet().stream()
								.map(tfs -> TurnFinishedStatusPlayerDto.fromPlayer(tfs.getKey(), tfs.getValue()))
								.collect(Collectors.toList())),
				new StarMapDto(gameView.getGalaxyWidth(), gameView.getGalaxyHeight(), state.isMiniMap(),
						state.isFleetMovementState()),
				new MainPageDto.StateDescriptionDto(state.getClass().getSimpleName()));

		mainPage.starMap.getContent().ranges = new RangesDto(gameView.getGalaxyWidth(), gameView.getGalaxyHeight(),
				gameView.getPlayer());

		mainPage.starMap.getContent().ranges.fleetRanges.addAll(gameView.getSystems().stream()
				.filter(s -> s.getFleetRange().isPresent() && s.getExtendedFleetRange().isPresent())
				.map(s -> new FleetRangeDto("fleetRange@" + s.getId().getValue(), s.getLocation().getX(),
						s.getLocation().getY(), s.getFleetRange().orElseThrow(),
						s.getExtendedFleetRange().orElseThrow()))
				.collect(Collectors.toList()));

		mainPage.starMap
				.getContent().ranges.colonyScannerRanges
						.addAll(gameView.getSystems().stream().filter(s -> s.getScannerRange().isPresent())
								.map(s -> new ScannerRangeDto("scannerRange@" + s.getId().getValue(),
										s.getLocation().getX(), s.getLocation().getY(), s.getScannerRange().get()))
								.collect(Collectors.toList()));

		mainPage.starMap.getContent().ranges.fleetScannerRanges
				.addAll(gameView.getFleets().stream().filter(f -> !state.isMiniMap() && f.getScannerRange().isPresent())
						.map(f -> new ScannerRangeDto("scannerRange@" + f.getId().getValue(), f.getLocation().getX(),
								f.getLocation().getY(), f.getScannerRange().orElseThrow()))
						.collect(Collectors.toList()));

		SystemId turnSelectedStarId = null;
		if (state.getSelectedSystemId().isPresent()) {
			turnSelectedStarId = fromSelectedStar(gameView, state, mainPage, context);

			final SystemView turnSelectedStar = gameView.getSystem(turnSelectedStarId);
			if (state.isNewTurnState()) {
				mainPage.starMap.getContent().scrollTo = new ScrollToDto(turnSelectedStar.getLocation().getX(),
						turnSelectedStar.getLocation().getY(), false);
			}
		}

		final Optional<Map<ShipTypeView, Integer>> maybeShips = state.getShips()
				.or(() -> state.getSelectedFleetId().map(id -> gameView.getFleet(id).getShips()));

		if (state.getSelectedFleetId().isPresent()) {
			turnSelectedStarId = fromSelectedFleet(gameView, state, maybeShips, mainPage, context);
		}

		{
			final SystemId finalTurnSelectedStarId = turnSelectedStarId;
			mainPage.buttonBar = new EntityModel<>(new ButtonBarDto()).with(!state.isOneByOneSystemsState(),
					() -> Action
							.jsonPost("finish-turn", context.toFrontendUri("main-page", "button-bar", "finished-turns"))
							.with("selectedStarId", finalTurnSelectedStarId.getValue())
							.with("round", gameView.getRound()))
					.with(!state.isOneByOneSystemsState(),
							() -> Action.get("show-tech-page", context.toFrontendUri("tech-page"))
									.with("selectedStarId", finalTurnSelectedStarId.getValue()));
		}

		mainPage.starMap.getContent().stars = gameView
				.getSystems().stream().map(
						s -> new EntityModel<>(new StarDto(s.getId().getValue(), s.getStarName(), s.getStarType(),
								s.isSmall(), s.getColonyView().map(ColonyView::getPlayer),
								s.getColonyView().flatMap(ColonyView::getAnnexationStatus)
										.flatMap(AnnexationStatusView::siegingPlayer),
								s.getColonyView().flatMap(ColonyView::getAnnexationStatus).flatMap(
										as -> as.siegeRounds().isPresent() && as.roundsUntilAnnexable().isPresent()
												? Optional
														.of(Math.round(
																as.siegeRounds().get()
																		/ (float) (as.siegeRounds().get()
																				+ as.roundsUntilAnnexable().get())
																		* 100.0f))
												: Optional.empty()),
								s.getLocation().getX(), s.getLocation().getY()))
										.with(state.isSystemSelectable(s.getId()),
												() -> Action.get("select", context.toFrontendUri("main-page")) //
														.with("selectedStarId", s.getId().getValue()).with(
																state.getSelectedFleetId().isPresent()
																		&& gameView.getFleet(state.getSelectedFleetId()
																				.orElseThrow()).isDeployable(),
																"selectedFleetId",
																() -> state.getSelectedFleetId().map(FleetId::getValue)
																		.orElseThrow())
														.with(maybeShips.isPresent(),
																() -> maybeShips.map(ships -> toDtoShipList(ships))
																		.orElseThrow().stream()
																		.map(sh -> new ActionField(sh.id, sh.count)))))
				.collect(Collectors.toList());

		mainPage.starMap.getContent().fleets = Stream
				.concat(gameView.getFleets().stream()
						.flatMap(f -> FleetHelper.toStarMapFleets(f, gameView.getSpaceCombats(),

								state)),
						FleetHelper.createSpaceCombatFakeFleets(gameView, state))
				.map(f -> new EntityModel<>(new FleetDto(f.id.getValue(), f.player, f.x, f.y, f.orbiting, f.justLeaving,
						f.speed, f.horizontalDirection)).with(
								!state.isMiniMap() && state.isFleetSelectable(f.id),
								() -> Action.get("select", context.toFrontendUri("main-page")) //
										.with(state.isFleetSelectable(f.id), "selectedFleetId", () -> f.id.getValue())
										.with(f.destination != null, "selectedStarId", () -> f.destination.getValue())))
				.collect(Collectors.toList());

		return new EntityModel<>(mainPage)
				.with(state.isTurnFinishedState(),
						() -> context.toNamedAction("fleet-movements", HttpMethod.GET, true, false, "main-page")
								.with("turnFinishedRound", gameView.getRound())) // remove
				.with(state.isFleetMovementState(),
						() -> context.withSelectedStar(state.getSelectedSystemId().orElseThrow())
								.toNamedAction("begin-new-turn", HttpMethod.GET, true, false, "main-page").with(steps));
	}

	private static SystemId fromSelectedStar(final GameView gameView, final MainPageState state,
			final MainPageDto mainPage, final FrontendContext context) {
		SystemId turnSelectedStarId;
		final SystemView selectedSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());
		mainPage.starMap.getContent().starSelection = new StarSelectionDto(selectedSystem.getLocation().getX(),
				selectedSystem.getLocation().getY());
		turnSelectedStarId = selectedSystem.getId();
		if (state.isStarInspectionState() || state.isNotificationState()) {
			if (selectedSystem.getStarName().isPresent()) {
				final HabitabilityDto habitabilityDto = new HabitabilityDto(selectedSystem.getPlanetType().get(),
						selectedSystem.getPlanetSpecial().get(), selectedSystem.getPlanetMaxPopulation().get());

				if (selectedSystem.isColonizable()) {
					final FleetId orbiting = gameView.getOrbiting(selectedSystem.getId()).get().getId();

					mainPage.inspector.colonization = new EntityModel<>(
							new ColonizationDto(selectedSystem.getStarName().get(), habitabilityDto,
									selectedSystem.hasColonizeCommand().orElse(Boolean.FALSE)))
											.with(Action
													.jsonPost("colonize",
															context.toFrontendUri("main-page", "inspector",
																	"colonizations"))
													.with("selectedStarId", selectedSystem.getId().getValue())
													.with("fleetId", orbiting.getValue()).with("skip", Boolean.FALSE))
											.with(Action
													.jsonPost("cancel",
															context.toFrontendUri("main-page", "inspector",
																	"colonizations"))
													.with("selectedStarId", selectedSystem.getId().getValue())
													.with("fleetId", orbiting.getValue()).with("skip", Boolean.TRUE));
				} else if (selectedSystem.getColonyView().flatMap(ColonyView::getAnnexationStatus)
						.flatMap(AnnexationStatusView::annexable).orElse(Boolean.FALSE)) {
					final FleetId orbiting = gameView.getOrbiting(selectedSystem.getId()).get().getId();

					mainPage.inspector.annexation = new EntityModel<>(
							new AnnexationDto(selectedSystem.getStarName().get(), habitabilityDto,
									selectedSystem.getColonyView().get().getAnnexationStatus().get().annexCommand()
											.orElse(Boolean.FALSE)))
													.with(Action
															.jsonPost("annex",
																	context.toFrontendUri("main-page", "inspector",
																			"annexations"))
															.with("selectedStarId", selectedSystem.getId().getValue())
															.with("fleetId", orbiting.getValue())
															.with("skip", Boolean.FALSE))
													.with(Action
															.jsonPost("cancel",
																	context.toFrontendUri("main-page", "inspector",
																			"annexations"))
															.with("selectedStarId", selectedSystem.getId().getValue())
															.with("fleetId", orbiting.getValue())
															.with("skip", Boolean.TRUE));
				} else {
					mainPage.inspector.systemDetails = new SystemDetailsDto(selectedSystem.getStarName().get(),
							habitabilityDto,
							selectedSystem.getColonyView()
									.map(c -> new ColonyDto(
											Optional.ofNullable(c.getPlayer() != gameView.getPlayer()
													? new ForeignColonyOwner(c.getRace(), c.getPlayer())
													: null),
											c.getPopulation(), c.getRatios().map(r -> new ProductionDto(42, 78)))),
							selectedSystem.getColonyView().filter(c -> c.getPlayer() == gameView.getPlayer())
									.map(c -> new EntityModel<>(new AllocationsDto(Map.of( //
											"ship", new AllocationCategoryDto(10, "None"), //
											"defence", new AllocationCategoryDto(15, "None"), //
											"industry", new AllocationCategoryDto(20, "2.7/y"), //
											"ecology", new AllocationCategoryDto(25, "Clean"), //
											"technology", new AllocationCategoryDto(30, "0RP")),
											state.getLockedCategory()))
													.with(Action
															.jsonPost("allocate-spending",
																	context.toFrontendUri("main-page", "inspector",
																			"spendings"))
															.with("selectedStarId",
																	selectedSystem.getId().getValue()))),
							selectedSystem.getColonyView().filter(c -> c.getPlayer() == gameView.getPlayer())
									.map(c -> new EntityModel<>(new BuildQueueDto(c.getSpaceDock().get().getName(),
											c.getSpaceDock().get().getSize(), gameView.getPlayer(), 1))
													.with(Action
															.jsonPost("next-ship-type",
																	context.toFrontendUri("main-page", "inspector",
																			"ship-types"))
															.with("selectedStarId", selectedSystem.getId().getValue())
															.with(state.getLockedCategory().isPresent(), "locked",
																	() -> state.getLockedCategory().get())
															.with("colonyId", c.getId().getValue()))),
							selectedSystem.getRange());
				}
			} else {
				mainPage.inspector.unexplored = new UnexploredDto(selectedSystem.getStarType(),
						selectedSystem.getRange().orElseThrow());
			}

			if (state.isNotificationState()) {
				final SystemView notificationSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());
				final SystemNotificationView notification = gameView.getSystemNotifications().stream()
						.filter(sn -> sn.getSystemId().equals(notificationSystem.getId())).findFirst().get();
				mainPage.starMap.getContent()
						.setStarNotification(new EntityModel<>(new StarNotificationDto(
								notificationSystem.getLocation().getX(), notificationSystem.getLocation().getY(),
								notification.getMessages().iterator().next())).with(Action
										.get("confirm", context.toFrontendUri("main-page")) //
										.with("selectedStarId",
												state.asNotificationState().getActualSelectedSystemId().getValue())
										.with(state.asNotificationState().hasRemainingNotificationSystems(),
												"notificationSystemId",
												() -> state.asNotificationState().getRemainingNotificationSystemIds()
														.stream().map(SystemId::getValue))
										.with(willBeNewTurn(state.asNotificationState()), "newTurn",
												() -> Boolean.TRUE)));
			}
		} else if (state.isSpaceCombatSystemState()) {
			final SpaceCombatView spaceCombat = gameView.getSpaceCombats().stream()
					.filter(sc -> sc.getSystemId().equals(selectedSystem.getId())).findAny().orElseThrow();

			mainPage.inspector.spaceCombat = new EntityModel<>(new SpaceCombatDto(selectedSystem.getStarName()
					.map(name -> new SpaceCombatSystem(name, new HabitabilityDto(selectedSystem.getPlanetType().get(),
							selectedSystem.getPlanetSpecial().get(), selectedSystem.getPlanetMaxPopulation().get()))),
					spaceCombat.getAttacker(), spaceCombat.getAttackerPlayer(), spaceCombat.getDefender(),
					spaceCombat.getDefenderPlayer()))
							.with(context
									.toNamedAction("continue", HttpMethod.GET, false, false, "space-combat-page",
											state.getSelectedSystemId().orElseThrow().getValue())
									.with("selectedStarId",
											state.asSpaceCombatSystemState().getActualSelectedSystemId().getValue())
									.with(state.asSpaceCombatSystemState().hasRemainingSpaceCombatSystems(),
											"spaceCombatSystemId",
											() -> state.asSpaceCombatSystemState().getRemainingSpaceCombatSystemIds()
													.stream().map(scsId -> scsId.getKey() + "@" + scsId.getValue())) // CPD-OFF
									.with(gameView.getJustExploredSystemIds().stream()
											.map(esId -> new ActionField("exploredSystemId", esId.getValue())))
									.with(gameView.getColonizableSystemIds().stream()
											.map(csId -> new ActionField("colonizableSystemId", csId.getValue())))
									.with(gameView.getAnnexableSystemIds().stream()
											.map(asId -> new ActionField("annexableSystemId", asId.getValue())))
									.with(gameView.getSystemNotifications().stream()
											.map(SystemNotificationView::getSystemId)
											.map(nsId -> new ActionField("notificationSystemId", nsId.getValue())))); // CPD-ON
		} else if (state.isExplorationState()) {
			final boolean willBeNewTurn = willBeNewTurn(state.asExplorationState(), gameView.getSystemNotifications(),
					gameView.getColonizableSystemIds(), gameView.getAnnexableSystemIds());

			mainPage.inspector.exploration = new EntityModel<>(new ExplorationDto(
					selectedSystem.getStarName().orElseThrow(),
					new HabitabilityDto(selectedSystem.getPlanetType().get(), selectedSystem.getPlanetSpecial().get(),
							selectedSystem.getPlanetMaxPopulation().get()))) //
									.with(Action.get("continue", context.toFrontendUri("main-page")) //
											.with("selectedStarId",
													state.asExplorationState().getActualSelectedSystemId().getValue())
											.with(state.asExplorationState().hasRemainingExploredSystems(),
													"exploredSystemId",
													() -> state.asExplorationState().getRemainingExplorationSystemIds()
															.stream().map(SystemId::getValue))
											.with(gameView.getSystemNotifications().stream()
													.map(SystemNotificationView::getSystemId)
													.map(nsId -> new ActionField("notificationSystemId",
															nsId.getValue())))
											.with(gameView.getColonizableSystemIds().stream().map(
													csId -> new ActionField("colonizableSystemId", csId.getValue())))
											.with(gameView.getAnnexableSystemIds().stream()
													.map(csId -> new ActionField("annexableSystemId", csId.getValue())))
											.with(willBeNewTurn, "newTurn", () -> Boolean.TRUE));
		} else if (state.isColonizationState()) {
			Action colonizeAction;
			Action cancelAction;
			mainPage.inspector.colonization = new EntityModel<>(
					new ColonizationDto(selectedSystem.getStarName().orElseThrow(),
							new HabitabilityDto(selectedSystem.getPlanetType().get(),
									selectedSystem.getPlanetSpecial().get(),
									selectedSystem.getPlanetMaxPopulation().get()),
							null)).with(
									colonizeAction = Action
											.jsonPost("colonize",
													context.toFrontendUri("main-page", "inspector", "colonizations"))
											.with("fleetId",
													gameView.getOrbiting(selectedSystem.getId()).orElseThrow().getId()
															.getValue()))
									.with(cancelAction = Action.get("cancel", context.toFrontendUri("main-page")));

			final boolean willBeNewTurn = willBeNewTurn(state.asColonizationState(), gameView.getAnnexableSystemIds(),
					gameView.getSystemNotifications());

			Stream.of(colonizeAction, cancelAction)
					.forEach(colonizationAction -> colonizationAction
							.with("selectedStarId", state.asColonizationState().getActualSelectedSystemId().getValue())
							.with(state.asColonizationState().hasRemainingColonizableSystems(), "colonizableSystemId",
									() -> state.asColonizationState().getRemainingColonizableSystemIds().stream()
											.map(SystemId::getValue))
							.with(gameView.getAnnexableSystemIds().stream()
									.map(csId -> new ActionField("annexableSystemId", csId.getValue())))
							.with(gameView.getSystemNotifications().stream().map(SystemNotificationView::getSystemId)
									.map(nsId -> new ActionField("notificationSystemId", nsId.getValue())))
							.with(willBeNewTurn, "newTurn", () -> Boolean.TRUE));
		} else if (state.isAnnexationState()) {
			Action annexAction;
			Action cancelAction;
			mainPage.inspector.annexation = new EntityModel<>(
					new AnnexationDto(selectedSystem.getStarName().orElseThrow(),
							new HabitabilityDto(selectedSystem.getPlanetType().get(),
									selectedSystem.getPlanetSpecial().get(),
									selectedSystem.getPlanetMaxPopulation().get()),
							null)).with(
									annexAction = Action
											.jsonPost("annex",
													context.toFrontendUri("main-page", "inspector", "annexations"))
											.with("fleetId",
													gameView.getOrbiting(selectedSystem.getId()).orElseThrow().getId()
															.getValue()))
									.with(cancelAction = Action.get("cancel", context.toFrontendUri("main-page")));

			final boolean willBeNewTurn = willBeNewTurn(state.asAnnexationState(), gameView.getSystemNotifications());

			Stream.of(annexAction, cancelAction)
					.forEach(annexationAction -> annexationAction
							.with("selectedStarId", state.asAnnexationState().getActualSelectedSystemId().getValue())
							.with(state.asAnnexationState().hasRemainingAnnexableSystemIds(), "annexableSystemId",
									() -> state.asAnnexationState().getRemainingAnnexableSystemIds().stream()
											.map(SystemId::getValue))
							.with(gameView.getSystemNotifications().stream().map(SystemNotificationView::getSystemId)
									.map(nsId -> new ActionField("notificationSystemId", nsId.getValue())))
							.with(willBeNewTurn, "newTurn", () -> Boolean.TRUE));
		}

		return turnSelectedStarId;
	}

	private static boolean willBeNewTurn(final MainPageState.ExplorationState explorationState,
			final Set<SystemNotificationView> systemNotifications, final Set<SystemId> colonizableSystemIds,
			final Set<SystemId> annexableSystemIds) {
		return !explorationState.hasRemainingExploredSystems() && colonizableSystemIds.isEmpty()
				&& annexableSystemIds.isEmpty() && systemNotifications.isEmpty();
	}

	private static boolean willBeNewTurn(final MainPageState.ColonizationState colonizationState,
			final Set<SystemId> annexableSystemIds, final Set<SystemNotificationView> systemNotifications) {
		return !colonizationState.hasRemainingColonizableSystems() && annexableSystemIds.isEmpty()
				&& systemNotifications.isEmpty();
	}

	private static boolean willBeNewTurn(final MainPageState.AnnexationState annexationState,
			final Set<SystemNotificationView> systemNotifications) {
		return !annexationState.hasRemainingAnnexableSystemIds() && systemNotifications.isEmpty();
	}

	private static boolean willBeNewTurn(final MainPageState.NotificationState notificationState) {
		return !notificationState.hasRemainingNotificationSystems();
	}

	private static SystemId fromSelectedFleet(final GameView gameView, final MainPageState state,
			final Optional<Map<ShipTypeView, Integer>> maybeShips, final MainPageDto mainPage,
			final FrontendContext context) {
		SystemId turnSelectedStarId;
		final FleetView selectedFleet = gameView.getFleet(state.getSelectedFleetId().orElseThrow());
		final boolean justLeaving = selectedFleet.isJustLeaving().orElse(Boolean.FALSE);
		final Map<ShipTypeView, Integer> ships = maybeShips.orElseThrow();
		mainPage.starMap.getContent().fleetSelection = new FleetSelectionDto(selectedFleet.getLocation().getX(),
				selectedFleet.getLocation().getY(), selectedFleet.getOrbiting().isPresent(), justLeaving);
		turnSelectedStarId = selectedFleet.getOrbiting().or(selectedFleet::getClosest).orElseThrow();
		final Map<ShipTypeView, Integer> totalShips = gameView.getFleet(selectedFleet.getId()).getShips();

		if (state.isFleetInspectionState()) {
			if (selectedFleet.isDeployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(new FleetDeploymentDto(selectedFleet.getPlayer(),
						null, null, false, toDtoShipList(ships, totalShips)));
			} else {
				mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.getPlayer(),
						selectedFleet.getRace(), null, toDtoShipList(ships)));
			}
		} else if (state.isFleetDeploymentState()) {
			final SystemView selectedSystem = gameView.getSystem(state.getSelectedSystemId().orElseThrow());
			final Optional<Integer> eta = context.getPlayerGame().calcEta(selectedFleet.getId(), selectedSystem.getId(),
					ships.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue)));

			if (selectedFleet.isDeployable()) {
				mainPage.inspector.fleetDeployment = new EntityModel<>(new FleetDeploymentDto(selectedFleet.getPlayer(),
						eta.orElse(null), eta.isPresent() ? null
								: selectedSystem.getRange().orElse(null),
						true, toDtoShipList(ships, totalShips))).with(
								selectedFleet.isDeployable() && eta.isPresent(),
								() -> Action
										.jsonPost("deploy",
												context.toFrontendUri("main-page", "inspector", "deployments"))
										.with("selectedFleetId", selectedFleet.getId().getValue()) //
										.with("selectedStarId", selectedSystem.getId().getValue())
										.with(toDtoShipList(ships).stream().map(s -> new ActionField(s.id, s.count))));
			} else {
				mainPage.inspector.fleetView = new EntityModel<>(new FleetViewDto(selectedFleet.getPlayer(),
						selectedFleet.getRace(), eta.orElse(null), toDtoShipList(ships)));
			}

			mainPage.starMap.getContent().itinerary = new ItineraryDto(selectedFleet.getLocation().getX(),
					selectedFleet.getLocation().getY(), selectedSystem.getLocation().getX(),
					selectedSystem.getLocation().getY(), selectedFleet.getOrbiting().isPresent(), justLeaving,
					eta.isPresent());
		}

		if (mainPage.inspector.fleetDeployment != null) {
			mainPage.inspector.fleetDeployment.with(Action.get("cancel", context.toFrontendUri("main-page")) //
					.with("selectedStarId",
							selectedFleet.getOrbiting().orElseGet(() -> selectedFleet.getSource().orElseThrow())
									.getValue()))
					.with(context.toNamedAction("assign-ships", HttpMethod.GET, true, true, "main-page") //
							.with(maybeShips.isPresent(), () -> maybeShips.map(s -> toDtoShipList(s)).orElseThrow()
									.stream().map(sh -> new ActionField(sh.id, sh.count))));
		}

		return turnSelectedStarId;
	}

	static List<ShipsDto> toDtoShipList(final Map<ShipTypeView, Integer> ships) {
		return toDtoShipList(ships, emptyMap());
	}

	static List<ShipsDto> toDtoShipList(final Map<ShipTypeView, Integer> ships,
			final Map<ShipTypeView, Integer> totalShips) {
		return ships.entrySet().stream()
				.map(e -> new ShipsDto(e.getKey().getId().getValue(), e.getKey().getName(), e.getKey().getSize(),
						e.getValue(), totalShips.get(e.getKey())))
				.sorted((first, second) -> first.name.compareTo(second.name)).collect(Collectors.toList());
	}
}
