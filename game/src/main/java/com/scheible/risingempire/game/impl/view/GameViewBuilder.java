package com.scheible.risingempire.game.impl.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusView;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.spacecombat.CombatantShipSpecsView;
import com.scheible.risingempire.game.api.view.spacecombat.FireExchangeView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.game.impl.colony.Colony;
import com.scheible.risingempire.game.impl.fleet.DeployedFleet;
import com.scheible.risingempire.game.impl.fleet.Fleet;
import com.scheible.risingempire.game.impl.fleet.FleetManager;
import com.scheible.risingempire.game.impl.fleet.OrbitingFleet;
import com.scheible.risingempire.game.impl.fraction.Technology;
import com.scheible.risingempire.game.impl.ship.AbstractSpecial;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.spacecombat.FireExchange;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;
import com.scheible.risingempire.game.impl.system.System;
import com.scheible.risingempire.game.impl.system.SystemSnapshot;
import com.scheible.risingempire.game.impl.tech.TechManager;

/**
 * @author sj
 */
public class GameViewBuilder {

	public static GameView buildView(GalaxySize galaxySize, int round, Map<Player, Boolean> turnFinishedStatus,
			Player player, Map<Player, Race> playerRaceMapping, Collection<System> systems, Collection<Fleet> fleets,
			Map<Player, Map<DesignSlot, ShipDesign>> designs,
			Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping,
			BiFunction<Player, SystemId, Optional<SystemSnapshot>> snapshotProvider, Technology technology,
			Set<SpaceCombat> spaceCombats, FleetManager fleetManager, TechManager techManager,
			Set<SystemNotificationView> systemNotifications, int annexationSiegeRounds) {
		Set<SystemView> systemViews = new HashSet<>(systems.size());
		Set<FleetView> fleetViews = new HashSet<>(30);

		Map<SystemId, OrbitingFleet> orbitingFleets = fleets.stream()
			.filter(Fleet::isOrbiting)
			.map(Fleet::asOrbiting)
			.collect(Collectors.toMap(f -> f.getSystem().getId(), Function.identity(), (a, b) -> a));

		Predicate<System> isColonizable = system -> {
			OrbitingFleet orbiting = orbitingFleets.get(system.getId());
			return orbiting != null && orbiting.getPlayer() == player && fleetManager.canColonize(orbiting.getId());
		};

		Predicate<System> hasColonizeCommand = system -> isColonizable.test(system)
				&& fleetManager.hasColonizeCommand(player, system.getId(), orbitingFleets.get(system.getId()).getId());

		Predicate<System> isAnnexable = system -> {
			OrbitingFleet orbiting = orbitingFleets.get(system.getId());
			return orbiting != null && orbiting.getPlayer() == player && fleetManager.canAnnex(orbiting.getId());
		};

		Predicate<System> hasAnnexCommand = system -> isAnnexable.test(system)
				&& fleetManager.hasAnnexCommand(player, system.getId(), orbitingFleets.get(system.getId()).getId());

		Function<System, Integer> siegeProgress = (system) -> orbitingFleets.containsKey(system.getId())
				? fleetManager.getSiegeProgress(orbitingFleets.get(system.getId()).getId()).orElse(null) : null;

		Function<System, Integer> siegeRounds = (system) -> siegeProgress.apply(system) != null
				? Math.round((siegeProgress.apply(system) / 100.f) * annexationSiegeRounds) : null;

		Function<System, Integer> roundsUntilAnnexable = (system) -> siegeRounds.apply(system) != null
				? annexationSiegeRounds - siegeRounds.apply(system) : null;

		Function<System, Player> siegePlayer = (system) -> siegeProgress.apply(system) != null
				? orbitingFleets.get(system.getId()).getPlayer() : null;

		for (System system : systems) {
			systemViews.add(system.getColony(player)
				.map(c -> SystemSnapshot.forKnown(round, system))
				.or(() -> snapshotProvider.apply(player, system.getId()))
				.or(() -> Optional.of(SystemSnapshot.forUnknown(round, system)))
				.map(snapshot -> {
					Optional<Player> colonyPlayer = snapshot.getColonyPlayer();

					Optional<Colony> colony = system.getColony(player);
					Optional<ShipTypeView> spaceDock = colony.map(Colony::getSpaceDock)
						.map(ds -> ds.toShipType(designs.get(player).get(ds)));
					Optional<Map<ProductionArea, Integer>> ratios = colony.map(Colony::getRatios);

					Optional<ColonyView> colonyView = colonyPlayer
						.map(cc -> new ColonyView(snapshot.getId().toColonyId(), colonyPlayer.get(),
								playerRaceMapping.get(colonyPlayer.get()), snapshot.getColonyPopulation().get(),
								spaceDock, ratios,
								Optional.ofNullable(!(siegePlayer.apply(system) == null && !isAnnexable.test(system))
										? new AnnexationStatusView(Optional.ofNullable(siegeRounds.apply(system)),
												Optional.ofNullable(roundsUntilAnnexable.apply(system)),
												Optional.ofNullable(siegePlayer.apply(system)),
												Optional.ofNullable(siegePlayer.apply(system))
													.map(playerRaceMapping::get),
												Optional.of(isAnnexable.test(system)),
												Optional.of(hasAnnexCommand.test(system)))
										: null)));

					Optional<Integer> seenInTurn = Optional.ofNullable(snapshot.getLastSeenTurn())
						.filter(t -> t != round);
					Optional<Integer> range = Optional
						.ofNullable(system.getColony(player).isPresent() ? null : system.calcRange(player, systems));

					return new SystemView(snapshot.getId(), snapshot.wasJustExplored(round), snapshot.getLocation(),
							snapshot.getStarType(), system.getName().toLowerCase(Locale.ROOT).contains("u"),
							system.isHomeSystem(player), range, snapshot.getPlanetType(), snapshot.getPlanetSpecial(),
							seenInTurn, snapshot.getStarName(), snapshot.getPlanetMaxPopulation(), colonyView,
							system.getColony(player).map(c -> technology.getFleetRange()),
							system.getColony(player).map(c -> technology.getExtendedFleetRange()),
							system.getColony(player).map(c -> technology.getColonyScannerRange()),
							Optional.of(isColonizable.test(system)), Optional.of(hasColonizeCommand.test(system)));
				})
				.orElseThrow());
		}

		Set<SystemId> annexableSystemIds = new HashSet<>(30);
		Set<SystemId> colonizableSystemIds = new HashSet<>(30);

		for (Fleet fleet : fleets) {
			if (fleet.getPlayer() == player) {
				if (fleetManager.canColonize(fleet.getId())) {
					colonizableSystemIds.add(fleet.asOrbiting().getSystem().getId());
				}
				else if (fleetManager.canAnnex(fleet.getId())) {
					annexableSystemIds.add(fleet.asOrbiting().getSystem().getId());
				}

				fleetViews.add(toOwnFleetView(fleet, orbitingArrivingMapping.getOrDefault(fleet.getId(), Set.of()),
						playerRaceMapping.get(fleet.getPlayer()), designs.get(player), player,
						fleetManager.getClosest(fleet.getId()), technology.getFleetScannerRange()));
			}
			else if (isForeigenFleetVisible(systems, player, fleet, technology, fleets)) {
				fleetViews.add(toForeignFleetView(fleet, orbitingArrivingMapping.getOrDefault(fleet.getId(), Set.of()),
						playerRaceMapping.get(fleet.getPlayer()), fleetManager.getClosest(fleet.getId())));
			}
		}

		Set<SpaceCombatView> spaceCombatViews = spaceCombats.stream()
			.filter(sc -> sc.getAttacker() == player || sc.getDefender() == player)
			.map(sc -> new SpaceCombatView(sc.getSystemId(), sc.getOrder(), sc.getFireExchangeCount(),
					playerRaceMapping.get(sc.getAttacker()), sc.getAttacker(), sc.getAttackerFleet(),
					toCombatantShipSpecs(sc.getAttackerShipCounts(), sc.getAttackerFireExchanges(),
							designs.get(sc.getAttacker())),
					playerRaceMapping.get(sc.getDefender()), sc.getDefender(), sc.getDefenderFleet(),
					toCombatantShipSpecs(sc.getDefenderShipCounts(), sc.getDefenderFireExchanges(),
							designs.get(sc.getDefender())),
					sc.getOutcome()))
			.collect(Collectors.toSet());

		Set<SystemId> justExploredSystem = getJustExploredSystem(player, systemViews, colonizableSystemIds,
				spaceCombatViews.stream().map(SpaceCombatView::getSystemId).collect(Collectors.toSet()));

		Set<TechGroupView> technologies = techManager.getSelectTechs(player)
			.stream()
			.map(g -> new TechGroupView(
					g.stream().map(t -> new TechView(t.getKey(), t.getValue(), "-")).collect(Collectors.toSet())))
			.collect(Collectors.toSet());

		return new GameView(galaxySize.getWidth(), galaxySize.getHeight(), player, playerRaceMapping.get(player),
				playerRaceMapping.keySet(), round, turnFinishedStatus, systemViews, fleetViews, colonizableSystemIds,
				annexableSystemIds, spaceCombatViews, justExploredSystem, technologies, systemNotifications);
	}

	private static List<CombatantShipSpecsView> toCombatantShipSpecs(Map<DesignSlot, Integer> shipCounts,
			Map<DesignSlot, List<FireExchange>> fireExchanges, Map<DesignSlot, ShipDesign> designs) {
		List<CombatantShipSpecsView> result = new ArrayList<>();

		for (Entry<DesignSlot, Integer> shipCount : shipCounts.entrySet()) {
			ShipDesign shipDesign = designs.get(shipCount.getKey());
			ShipTypeView shipType = shipCount.getKey().toShipType(shipDesign);

			List<FireExchange> shipsFireExchange = fireExchanges.get(shipCount.getKey());
			int previousCount = shipCount.getValue();
			int count = shipsFireExchange == null || shipsFireExchange.isEmpty() ? previousCount
					: shipsFireExchange.get(shipsFireExchange.size() - 1).getShipCount();

			List<String> equipment = Stream
				.concat(shipDesign.getWeaponSlots().stream().map(ws -> ws.getCount() + " " + ws.getWeapon().getName()),
						shipDesign.getSpecials().stream().map(AbstractSpecial::getName))
				.collect(Collectors.toList());

			result.add(new CombatantShipSpecsView(shipType.getId(), shipType.getName(), count, previousCount,
					shipType.getSize(), shipDesign.getHitsAbsorbedByShield(), shipDesign.getBeamDefence(),
					shipDesign.getAttackLevel(), shipDesign.getWarpSpeed(), shipDesign.getMissileDefence(),
					shipDesign.getHitPoints(), shipDesign.getCombatSpeed(), equipment,
					count == previousCount ? List.of()
							: shipsFireExchange.stream()
								.map(fe -> new FireExchangeView(fe.getRound(), fe.getLostHitPoints(), fe.getDamage(),
										fe.getShipCount()))
								.collect(Collectors.toList())));
		}

		return result;
	}

	private static Set<SystemId> getJustExploredSystem(Player player, Set<SystemView> systemViews,
			Set<SystemId> colonizableSystemIds, Set<SystemId> spaceCombatSystemIds) {
		return systemViews.stream()
			.filter(s -> s.getColonyView().filter(c -> c.getPlayer() == player).isEmpty())
			.filter(SystemView::wasJustExplored)
			.map(SystemView::getId)
			.filter(esId -> !colonizableSystemIds.contains(esId) && !spaceCombatSystemIds.contains(esId))
			.collect(Collectors.toSet());
	}

	private static FleetView toOwnFleetView(Fleet fleet, Set<FleetBeforeArrival> fleetsBeforeArrival, Race race,
			Map<DesignSlot, ShipDesign> designs, Player player, SystemId closest, int scannerRange) {
		Map<ShipTypeView, Integer> shipTypesAndCounts = fleet.getShips()
			.entrySet()
			.stream()
			.map(slotAndCount -> Map.entry(slotAndCount.getKey().toShipType(designs.get(slotAndCount.getKey())),
					slotAndCount.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (fleet.isDeployed()) {
			DeployedFleet deployedFleet = fleet.asDeployed();

			return FleetView.createDeployed(fleet.getId(), player, race, shipTypesAndCounts,
					Optional.of(deployedFleet.getSource().getId()), Optional.of(deployedFleet.getDestination().getId()),
					deployedFleet.getLocation(), deployedFleet.getSpeed(), closest,
					deployedFleet.getHorizontalDirection(), deployedFleet.isJustLeaving(), Optional.of(scannerRange),
					fleetsBeforeArrival, deployedFleet.isJustLeaving());
		}
		else if (fleet.isOrbiting()) {
			OrbitingFleet orbitingFleet = fleet.asOrbiting();

			return FleetView.createOrbiting(fleet.getId(), player, race, shipTypesAndCounts,
					orbitingFleet.getSystem().getId(), orbitingFleet.getSystem().getLocation(), fleetsBeforeArrival,
					true, Optional.of(scannerRange));
		}

		throw new IllegalStateException("Unknown fleet type!");
	}

	private static boolean isForeigenFleetVisible(Collection<System> systems, Player player, Fleet fleet,
			Technology technology, Collection<Fleet> fleets) {
		boolean scannedByColony = systems.stream()
			.filter(s -> s.getColony(player).isPresent())
			.mapToDouble(s -> s.getLocation().getDistance(fleet.getLocation()))
			.min()
			.stream()
			.anyMatch(d -> d <= technology.getColonyScannerRange());

		boolean scannedByFleet = fleets.stream()
			.filter(f -> f.getPlayer() == player)
			.anyMatch(f -> f.getLocation().getDistance(fleet.getLocation()) < technology.getFleetScannerRange());

		return scannedByColony || scannedByFleet;
	}

	private static FleetView toForeignFleetView(Fleet fleet, Set<FleetBeforeArrival> fleetsBeforeArrival, Race race,
			SystemId closest) {
		if (fleet.isDeployed()) {
			return FleetView.createDeployed(fleet.getId(), fleet.getPlayer(), race, Map.of(), Optional.empty(),
					Optional.empty(), fleet.getLocation(), fleet.asDeployed().getSpeed(), closest,
					fleet.asDeployed().getHorizontalDirection(), false, Optional.empty(), fleetsBeforeArrival, false);
		}
		else if (fleet.isOrbiting()) {
			OrbitingFleet orbitingFleet = fleet.asOrbiting();

			return FleetView.createOrbiting(fleet.getId(), fleet.getPlayer(), race, Map.of(),
					orbitingFleet.getSystem().getId(), orbitingFleet.getSystem().getLocation(), fleetsBeforeArrival,
					false, null);
		}

		throw new IllegalStateException("Unknown fleet type!");
	}

}
