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
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.AllocationView;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusView;
import com.scheible.risingempire.game.api.view.colony.ColonistTransferView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.colony.SpaceDockView;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.spacecombat.CombatantShipSpecsView;
import com.scheible.risingempire.game.api.view.spacecombat.FireExchangeView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.system.SystemNotificationView;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.tech.TechView;
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
			Map<FleetId, Set<FleetBeforeArrivalView>> orbitingArrivingMapping,
			BiFunction<Player, SystemId, Optional<SystemSnapshot>> snapshotProvider, Technology technology,
			Set<SpaceCombat> spaceCombats, FleetManager fleetManager, TechManager techManager,
			Set<SystemNotificationView> systemNotifications, int annexationSiegeRounds,
			Map<ColonyId, Map<ColonyId, Integer>> colonistTransfers, Map<ColonyId, ColonyId> shipRelocations,
			BiFunction<Player, Fleet, Optional<FleetId>> parentFleetProvider) {
		Set<SystemView> systemViews = new HashSet<>(systems.size());
		Set<FleetView> fleetViews = new HashSet<>(30);

		Map<SystemId, OrbitingFleet> orbitingFleets = fleets.stream()
			.filter(f -> f.getPlayer() == player)
			.filter(Fleet::isOrbiting)
			.map(Fleet::asOrbiting)
			.collect(Collectors.toMap(f -> f.getSystem().getId(), Function.identity()));

		Map<SystemId, List<SystemNotificationView>> notificationMapping = systemNotifications.stream()
			.collect(Collectors.groupingBy(SystemNotificationView::systemId));

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
					Optional<SpaceDockView> spaceDock = colony.map(Colony::getSpaceDock)
						.map(ds -> SpaceDockView.builder()
							.current(ds.toShipType(designs.get(player).get(ds)))
							.count(1)
							.build());
					Optional<Map<ProductionArea, AllocationView>> allocations = colony.map(Colony::getAllocations)
						.map(a -> a.entrySet()
							.stream()
							.collect(Collectors.toMap(Entry::getKey,
									e -> new AllocationView(e.getValue().percentage(), e.getValue().status()))));

					Optional<ColonyView> colonyView = colonyPlayer.map(cc -> ColonyView.builder()
						.id(snapshot.getId().toColonyId())
						.player(colonyPlayer.get())
						.race(playerRaceMapping.get(colonyPlayer.get()))
						.population(snapshot.getColonyPopulation().get())
						.outdated(false) // information not available
						.spaceDock(spaceDock)
						.allocations(allocations)
						.annexationStatus(Optional.ofNullable(
								!(siegePlayer.apply(system) == null && !isAnnexable.test(system)) ? AnnexationStatusView
									.builder()
									.siegeRounds(Optional.ofNullable(siegeRounds.apply(system)))
									.roundsUntilAnnexable(Optional.ofNullable(roundsUntilAnnexable.apply(system)))
									.siegingPlayer(Optional.ofNullable(siegePlayer.apply(system)))
									.siegingRace(
											Optional.ofNullable(siegePlayer.apply(system)).map(playerRaceMapping::get))
									.annexable(isAnnexable.test(system))
									.annexationCommand(hasAnnexCommand.test(system))
									.build() : null))
						.maxTransferPopulation(snapshot.getColonyPopulation().get() / 2)
						.colonistTransfer(colonistTransfers.getOrDefault(snapshot.getId().toColonyId(), Map.of())
							.entrySet()
							.stream()
							.findFirst()
							.map(e -> ColonistTransferView.builder()
								.desination(e.getKey())
								.colonists(e.getValue())
								.build()))
						.relocationTarget(Optional.ofNullable(shipRelocations.get(snapshot.getId().toColonyId())))
						.build());

					Optional<Integer> range = Optional
						.ofNullable(system.getColony(player).isPresent() ? null : system.calcRange(player, systems));

					return SystemView.builder()
						.id(snapshot.getId())
						.justExplored(snapshot.wasJustExplored(round))
						.location(snapshot.getLocation())
						.starType(snapshot.getStarType())
						.small(system.getName().toLowerCase(Locale.ROOT).contains("u"))
						.homeSystem(system.isHomeSystem(player))
						.closestColony(range)
						.planetType(snapshot.getPlanetType())
						.planetSpecial(snapshot.getPlanetSpecial())
						.starName(snapshot.getStarName())
						.planetMaxPopulation(snapshot.getPlanetMaxPopulation())
						.colony(colonyView)
						.fleetRange(system.getColony(player).map(c -> technology.getFleetRange()))
						.extendedFleetRange(system.getColony(player).map(c -> technology.getExtendedFleetRange()))
						.scannerRange(system.getColony(player).map(c -> technology.getColonyScannerRange()))
						.colonizable(isColonizable.test(system))
						.colonizeCommand(hasColonizeCommand.test(system))
						.notifications(new HashSet<>(notificationMapping.getOrDefault(system.getId(), List.of())))
						.build();
				})
				.orElseThrow());
		}

		for (Fleet fleet : fleets) {
			if (fleet.getPlayer() == player) {
				fleetViews.add(toOwnFleetView(fleet, orbitingArrivingMapping.getOrDefault(fleet.getId(), Set.of()),
						playerRaceMapping.get(fleet.getPlayer()), designs.get(player), player,
						fleetManager.getClosest(fleet.getId()), technology.getFleetScannerRange(),
						parentFleetProvider));
			}
			else if (isForeigenFleetVisible(systems, player, fleet, technology, fleets)) {
				fleetViews.add(toForeignFleetView(fleet, orbitingArrivingMapping.getOrDefault(fleet.getId(), Set.of()),
						playerRaceMapping.get(fleet.getPlayer()), fleetManager.getClosest(fleet.getId()),
						parentFleetProvider));
			}
		}

		Set<SpaceCombatView> spaceCombatViews = spaceCombats.stream()
			.filter(sc -> sc.getAttacker() == player || sc.getDefender() == player)
			.map(sc -> SpaceCombatView.builder()
				.systemId(sc.getSystemId())
				.systemName(systems.stream()
					.filter(s -> s.getId().equals(sc.getSystemId()))
					.findFirst()
					.orElseThrow()
					.getName())
				.fireExchangeCount(sc.getFireExchangeCount())
				.attacker(playerRaceMapping.get(sc.getAttacker()))
				.attackerPlayer(sc.getAttacker())
				.attackerFleets(Set.of(sc.getAttackerFleet()))
				.attackerShipSpecs(toCombatantShipSpecs(sc.getPreviousAttackerShipCounts(), sc.getAttackerShipCounts(),
						sc.getAttackerFireExchanges(), designs.get(sc.getAttacker())))
				.defender(playerRaceMapping.get(sc.getDefender()))
				.defenderPlayer(sc.getDefender())
				.destroyedDefenderFleet(Optional.of(sc.getDefenderFleet()))
				.defenderFleetsBeforeArrival(Set.of() /* TODO pass data */)
				.defenderShipSpecs(toCombatantShipSpecs(sc.getPreviousDefenderShipCounts(), sc.getDefenderShipCounts(),
						sc.getDefenderFireExchanges(), designs.get(sc.getDefender())))
				.outcome(sc.getOutcome())
				.build())
			.collect(Collectors.toSet());

		Set<TechGroupView> technologies = techManager.getSelectTechs(player)
			.stream()
			.map(g -> TechGroupView.builder()
				.researched(TechView.builder()
					.id(new TechId("researched"))
					.name("Better Ships III")
					.description("All of ships is level III.")
					.expense(120)
					.build())
				.next(g.stream()
					.map(t -> TechView.builder()
						.id(t.getKey())
						.name(t.getValue())
						.description("-")
						.expense(120)
						.build())
					.toList())
				.build())
			.collect(Collectors.toSet());

		return GameView.builder()
			.galaxyWidth(galaxySize.width())
			.galaxyHeight(galaxySize.height())
			.player(player)
			.race(playerRaceMapping.get(player))
			.players(playerRaceMapping.keySet())
			.round(round)
			.turnFinishedStatus(turnFinishedStatus)
			.systems(systemViews.stream().collect(Collectors.toMap(SystemView::id, Function.identity())))
			.fleets(fleetViews.stream().collect(Collectors.toMap(FleetView::id, Function.identity())))
			.spaceCombats(spaceCombatViews)
			.selectTechGroups(technologies)
			.newShips(Map.of())
			.build();
	}

	private static List<CombatantShipSpecsView> toCombatantShipSpecs(Map<DesignSlot, Integer> previosShipCounts,
			Map<DesignSlot, Integer> shipCounts, Map<DesignSlot, List<FireExchange>> fireExchanges,
			Map<DesignSlot, ShipDesign> designs) {
		List<CombatantShipSpecsView> result = new ArrayList<>();

		for (Entry<DesignSlot, Integer> shipCount : shipCounts.entrySet()) {
			ShipDesign shipDesign = designs.get(shipCount.getKey());
			ShipTypeView shipType = shipCount.getKey().toShipType(shipDesign);

			int previousCount = previosShipCounts.get(shipCount.getKey());
			int count = shipCount.getValue();

			List<String> equipment = Stream
				.concat(shipDesign.getWeaponSlots().stream().map(ws -> ws.getCount() + " " + ws.getWeapon().getName()),
						shipDesign.getSpecials().stream().map(AbstractSpecial::getName))
				.collect(Collectors.toList());

			result.add(CombatantShipSpecsView.builder()
				.id(shipType.id())
				.name(shipType.name())
				.count(count)
				.previousCount(previousCount)
				.size(shipType.size())
				.shield(Optional.of(shipDesign.getHitsAbsorbedByShield()))
				.beamDefence(Optional.of(shipDesign.getBeamDefence()))
				.attackLevel(Optional.of(shipDesign.getAttackLevel()))
				.warp(Optional.of(shipDesign.getWarpSpeed()))
				.missleDefence(Optional.of(shipDesign.getMissileDefence()))
				.hits(Optional.of(shipDesign.getHitPoints()))
				.speed(Optional.of(shipDesign.getCombatSpeed()))
				.equipment(equipment)
				.fireExchanges(count == previousCount ? List.of()
						: fireExchanges.getOrDefault(shipCount.getKey(), List.of())
							.stream()
							.map(fe -> FireExchangeView.builder()
								.round(fe.getRound())
								.lostHitPoints(fe.getLostHitPoints())
								.damage(fe.getDamage())
								.count(fe.getShipCount())
								.build())
							.collect(Collectors.toList()))
				.build());
		}

		return result;
	}

	private static FleetView toOwnFleetView(Fleet fleet, Set<FleetBeforeArrivalView> fleetsBeforeArrival, Race race,
			Map<DesignSlot, ShipDesign> designs, Player player, SystemId closest, int scannerRange,
			BiFunction<Player, Fleet, Optional<FleetId>> parentFleetProvider) {
		Map<ShipTypeView, Integer> shipTypesAndCounts = fleet.getShips()
			.entrySet()
			.stream()
			.map(slotAndCount -> Map.entry(slotAndCount.getKey().toShipType(designs.get(slotAndCount.getKey())),
					slotAndCount.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (fleet.isDeployed()) {
			DeployedFleet deployedFleet = fleet.asDeployed();
			return FleetView.create(FleetView.deployedBuilder()
				.id(fleet.getId())
				.parentId(parentFleetProvider.apply(fleet.getPlayer(), fleet))
				.player(player)
				.race(race)
				.ships(ShipsView.builder().ships(shipTypesAndCounts).build())
				.colonistTransporters(false)
				.source(Optional.of(deployedFleet.getSource().getId()))
				.destination(Optional.of(deployedFleet.getDestination().getId()))
				.previousLocation(deployedFleet.getPreviousLocation())
				.previousJustLeaving(deployedFleet.isPreviousJustLeaving())
				.location(deployedFleet.getLocation())
				.speed(deployedFleet.getSpeed())
				.closest(closest)
				.orientation(deployedFleet.getHorizontalDirection())
				.deployable(deployedFleet.isJustLeaving())
				.scannerRange(Optional.of(scannerRange))
				.justLeaving(deployedFleet.isJustLeaving())
				.build());
		}
		else if (fleet.isOrbiting()) {
			OrbitingFleet orbitingFleet = fleet.asOrbiting();

			return FleetView.create(FleetView.orbitingBuilder()
				.id(fleet.getId())
				.parentId(parentFleetProvider.apply(fleet.getPlayer(), fleet))
				.player(player)
				.race(race)
				.ships(ShipsView.builder().ships(shipTypesAndCounts).build())
				.orbiting(orbitingFleet.getSystem().getId())
				.location(orbitingFleet.getSystem().getLocation())
				.fleetsBeforeArrival(fleetsBeforeArrival)
				.deployable(true)
				.scannerRange(Optional.of(scannerRange))
				.build());
		}

		throw new IllegalStateException("Unknown fleet type!");
	}

	private static boolean isForeigenFleetVisible(Collection<System> systems, Player player, Fleet fleet,
			Technology technology, Collection<Fleet> fleets) {
		boolean scannedByColony = systems.stream()
			.filter(s -> s.getColony(player).isPresent())
			.mapToDouble(s -> s.getLocation().distance(fleet.getLocation()))
			.min()
			.stream()
			.anyMatch(d -> d <= technology.getColonyScannerRange());

		boolean scannedByFleet = fleets.stream()
			.filter(f -> f.getPlayer() == player)
			.anyMatch(f -> f.getLocation().distance(fleet.getLocation()) < technology.getFleetScannerRange());

		return scannedByColony || scannedByFleet;
	}

	private static FleetView toForeignFleetView(Fleet fleet, Set<FleetBeforeArrivalView> fleetsBeforeArrival, Race race,
			SystemId closest, BiFunction<Player, Fleet, Optional<FleetId>> parentFleetProvider) {
		if (fleet.isDeployed()) {
			return FleetView.create(FleetView.deployedBuilder()
				.id(fleet.getId())
				.parentId(parentFleetProvider.apply(fleet.getPlayer(), fleet))
				.player(fleet.getPlayer())
				.race(race)
				.ships(ShipsView.builder().ships(Map.of()).build())
				.colonistTransporters(false)
				.source(Optional.empty())
				.destination(Optional.empty())
				.previousLocation(fleet.asDeployed().getPreviousLocation())
				.previousJustLeaving(fleet.asDeployed().isPreviousJustLeaving())
				.location(fleet.getLocation())
				.speed(fleet.asDeployed().getSpeed())
				.closest(closest)
				.orientation(fleet.asDeployed().getHorizontalDirection())
				.deployable(false)
				.scannerRange(Optional.empty())
				.justLeaving(fleet.asDeployed().isJustLeaving())
				.build());
		}
		else if (fleet.isOrbiting()) {
			OrbitingFleet orbitingFleet = fleet.asOrbiting();
			return FleetView.create(FleetView.orbitingBuilder()
				.id(fleet.getId())
				.parentId(parentFleetProvider.apply(fleet.getPlayer(), fleet))
				.player(fleet.getPlayer())
				.race(race)
				.ships(ShipsView.builder().ships(Map.of()).build())
				.orbiting(orbitingFleet.getSystem().getId())
				.location(orbitingFleet.getSystem().getLocation())
				.fleetsBeforeArrival(fleetsBeforeArrival)
				.deployable(false)
				.scannerRange(Optional.empty())
				.build());
		}

		throw new IllegalStateException("Unknown fleet type!");
	}

}
