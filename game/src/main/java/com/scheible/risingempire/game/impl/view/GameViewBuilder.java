package com.scheible.risingempire.game.impl.view;

import static java.util.Collections.emptyMap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.view.FleetManager;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyManager;
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
import com.scheible.risingempire.game.api.view.tech.TechManager;
import com.scheible.risingempire.game.api.view.tech.TechView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.game.impl.colony.Colony;
import com.scheible.risingempire.game.impl.fleet.DeployedFleet;
import com.scheible.risingempire.game.impl.fleet.Fleet;
import com.scheible.risingempire.game.impl.fleet.OrbitingFleet;
import com.scheible.risingempire.game.impl.fraction.Technology;
import com.scheible.risingempire.game.impl.ship.AbstractSpecial;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.spacecombat.FireExchange;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;
import com.scheible.risingempire.game.impl.system.System;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.game.impl.system.SystemSnapshot;

/**
 *
 * @author sj
 */
public class GameViewBuilder {

	public static GameView buildView(final GalaxySize galaxySize, final int round,
			final Map<Player, Boolean> turnFinishedStatus, final Player player,
			final Map<Player, Race> playerRaceMapping, final Collection<System> systems, final Collection<Fleet> fleets,
			final Map<Player, Map<DesignSlot, ShipDesign>> designs,
			final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping,
			final BiFunction<Player, SystemId, Optional<SystemSnapshot>> snapshotProvider, final Technology technology,
			final Set<SpaceCombat> spaceCombats, final FleetManager fleetManager, final ColonyManager colonyManager,
			final TechManager techManager, final Set<SystemNotificationView> systemNotifications) {
		final Set<SystemView> systemViews = new HashSet<>(systems.size());
		final Set<FleetView> fleetViews = new HashSet<>(30);

		final Set<SystemOrb> uncolonized = new HashSet<>(30);
		for (final System system : systems) {
			systemViews.add(system.getColony(player).map(c -> SystemSnapshot.forKnown(round, system))
					.or(() -> snapshotProvider.apply(player, system.getId()))
					.or(() -> Optional.of(SystemSnapshot.forUnknown(round, system))).map(snapshot -> {
						final Optional<Player> colonyPlayer = snapshot.getColonyPlayer();

						final Optional<Colony> colony = system.getColony(player);
						final ShipTypeView spaceDock = colony.map(Colony::getSpaceDock)
								.map(ds -> ds.toShipType(designs.get(player).get(ds))).orElse(null);
						final Map<ProductionArea, Integer> ratios = colony.map(Colony::getRatios).orElse(null);

						final ColonyView colonyView = colonyPlayer
								.map(cc -> new ColonyView(snapshot.getId().toColonyId(), colonyPlayer.get(),
										playerRaceMapping.get(colonyPlayer.get()), snapshot.getColonyPopulation().get(),
										spaceDock, ratios, colonyManager))
								.orElse(null);

						final Integer seenInTurn = Optional.of(snapshot.getLastSeenTurn()).filter(t -> t != round)
								.orElse(null);
						final Integer range = system.getColony(player).isPresent() ? null
								: system.calcRange(player, systems);

						return new SystemView(snapshot.getId(), snapshot.wasJustExplored(round), snapshot.getLocation(),
								snapshot.getStarType(), system.getName().toLowerCase().contains("u"),
								system.isHomeSystem(player), range, snapshot.getPlanetType().orElse(null),
								snapshot.getPlanetSpecial().orElse(null), seenInTurn,
								snapshot.getStarName().orElse(null), snapshot.getPlanetMaxPopulation().orElse(null),
								colonyView, system.getColony(player).map(c -> technology.getFleetRange()).orElse(null),
								system.getColony(player).map(c -> technology.getExtendedFleetRange()).orElse(null),
								system.getColony(player).map(c -> technology.getColonyScannerRange()).orElse(null));
					}).orElseThrow());

			if (system.getColony().isEmpty()) {
				uncolonized.add(system);
			}
		}

		for (final Fleet fleet : fleets) {
			if (fleet.getPlayer() == player) {
				fleetViews.add(toOwnFleetView(fleet, orbitingArrivingMapping.get(fleet.getId()),
						playerRaceMapping.get(fleet.getPlayer()), designs.get(player), player, uncolonized,
						fleetManager.getClosest(fleet.getId()), technology.getFleetScannerRange()));
			} else if (isForeigenFleetVisible(systems, player, fleet, technology, fleets)) {
				fleetViews.add(toForeignFleetView(fleet, orbitingArrivingMapping.get(fleet.getId()),
						playerRaceMapping.get(fleet.getPlayer()), fleetManager.getClosest(fleet.getId())));
			}
		}

		final Set<SystemId> colonizableSystemIds = getColonizableSystemIds(fleetViews);

		final Set<SpaceCombatView> spaceCombatViews = spaceCombats.stream()
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

		final Set<SystemId> justExploredSystem = getJustExploredSystem(player, systemViews, colonizableSystemIds,
				spaceCombatViews.stream().map(SpaceCombatView::getSystemId).collect(Collectors.toSet()));

		final Set<TechGroupView> technologies = techManager.getSelectTechs(player).stream()
				.map(g -> new TechGroupView(
						g.stream().map(t -> new TechView(t.getKey(), t.getValue(), "-")).collect(Collectors.toSet())))
				.collect(Collectors.toSet());

		return new GameView(galaxySize.getWidth(), galaxySize.getHeight(), player, playerRaceMapping.get(player),
				playerRaceMapping.keySet(), round, turnFinishedStatus, systemViews, fleetViews, colonizableSystemIds,
				spaceCombatViews, justExploredSystem, technologies, systemNotifications);
	}

	private static List<CombatantShipSpecsView> toCombatantShipSpecs(final Map<DesignSlot, Integer> shipCounts,
			final Map<DesignSlot, List<FireExchange>> fireExchanges, final Map<DesignSlot, ShipDesign> designs) {
		final List<CombatantShipSpecsView> result = new ArrayList<>();

		for (final Entry<DesignSlot, Integer> shipCount : shipCounts.entrySet()) {
			final ShipDesign shipDesign = designs.get(shipCount.getKey());
			final ShipTypeView shipType = shipCount.getKey().toShipType(shipDesign);

			final List<FireExchange> shipsFireExchange = fireExchanges.get(shipCount.getKey());
			final int previousCount = shipCount.getValue();
			final int count = shipsFireExchange == null || shipsFireExchange.isEmpty() ? previousCount
					: shipsFireExchange.get(shipsFireExchange.size() - 1).getShipCount();

			final List<String> equipment = Stream
					.concat(shipDesign.getWeaponSlots().stream()
							.map(ws -> ws.getCount() + " " + ws.getWeapon().getName()),
							shipDesign.getSpecials().stream().map(AbstractSpecial::getName))
					.collect(Collectors.toList());

			result.add(new CombatantShipSpecsView(shipType.getId(), shipType.getName(), count, previousCount,
					shipType.getSize(), shipDesign.getHitsAbsorbedByShield(), shipDesign.getBeamDefence(),
					shipDesign.getAttackLevel(), shipDesign.getWarpSpeed(), shipDesign.getMissileDefence(),
					shipDesign.getHitPoints(), shipDesign.getCombatSpeed(), equipment,
					count == previousCount ? Collections.emptyList()
							: shipsFireExchange.stream().map(fe -> new FireExchangeView(fe.getRound(),
									fe.getLostHitPoints(), fe.getDamage(), fe.getShipCount()))
									.collect(Collectors.toList())));
		}

		return result;
	}

	private static Set<SystemId> getColonizableSystemIds(final Set<FleetView> fleetViews) {
		return fleetViews.stream().filter(f -> f.canColonize().orElse(Boolean.FALSE))
				.flatMap(f -> f.getOrbiting().stream()).collect(Collectors.toSet());
	}

	private static Set<SystemId> getJustExploredSystem(final Player player, final Set<SystemView> systemViews,
			final Set<SystemId> colonizableSystemIds, final Set<SystemId> spaceCombatSystemIds) {
		return systemViews.stream().filter(s -> s.getColonyView().filter(c -> c.getPlayer() == player).isEmpty())
				.filter(SystemView::wasJustExplored).map(SystemView::getId)
				.filter(esId -> !colonizableSystemIds.contains(esId) && !spaceCombatSystemIds.contains(esId))
				.collect(Collectors.toSet());
	}

	private static FleetView toOwnFleetView(final Fleet fleet, final Set<FleetBeforeArrival> fleetsBeforeArrival,
			final Race race, final Map<DesignSlot, ShipDesign> designs, final Player player,
			final Set<SystemOrb> uncolonized, final SystemId closest, final int scannerRange) {
		final Map<ShipTypeView, Integer> shipTypesAndCounts = fleet.getShips().entrySet().stream()
				.map(slotAndCount -> new AbstractMap.SimpleImmutableEntry<>(
						slotAndCount.getKey().toShipType(designs.get(slotAndCount.getKey())), slotAndCount.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (fleet.isDeployed()) {
			final DeployedFleet deployedFleet = fleet.asDeployed();

			return FleetView.createDeployed(fleet.getId(), player, race, shipTypesAndCounts,
					deployedFleet.getSource().getId(), deployedFleet.getDestination().getId(),
					deployedFleet.getLocation(), deployedFleet.getSpeed(), closest,
					deployedFleet.getHorizontalDirection(), deployedFleet.isJustLeaving(), scannerRange,
					fleetsBeforeArrival, deployedFleet.isJustLeaving());
		} else if (fleet.isOrbiting()) {
			final OrbitingFleet orbitingFleet = fleet.asOrbiting();

			final boolean hasColonyShip = orbitingFleet.getShips().keySet().stream()
					.filter(ds -> designs.get(ds).hasColonyBase()).findFirst().isPresent();
			final boolean canColonize = hasColonyShip && uncolonized.contains(orbitingFleet.getSystem());

			return FleetView.createOrbiting(fleet.getId(), player, race, shipTypesAndCounts,
					orbitingFleet.getSystem().getId(), orbitingFleet.getSystem().getLocation(), fleetsBeforeArrival,
					true, scannerRange, canColonize);
		}

		throw new IllegalStateException("Unknown fleet type!");
	}

	private static boolean isForeigenFleetVisible(final Collection<System> systems, final Player player,
			final Fleet fleet, final Technology technology, final Collection<Fleet> fleets) {
		final boolean scannedByColony = systems.stream().filter(s -> s.getColony(player).isPresent())
				.mapToDouble(s -> s.getLocation().getDistance(fleet.getLocation())).min().stream()
				.anyMatch(d -> d <= technology.getColonyScannerRange());

		final boolean scannedByFleet = fleets.stream().filter(f -> f.getPlayer() == player)
				.anyMatch(f -> f.getLocation().getDistance(fleet.getLocation()) < technology.getFleetScannerRange());

		return scannedByColony || scannedByFleet;
	}

	private static FleetView toForeignFleetView(final Fleet fleet, final Set<FleetBeforeArrival> fleetsBeforeArrival,
			final Race race, final SystemId closest) {
		if (fleet.isDeployed()) {
			return FleetView.createDeployed(fleet.getId(), fleet.getPlayer(), race, emptyMap(), null, null,
					fleet.getLocation(), fleet.asDeployed().getSpeed(), closest,
					fleet.asDeployed().getHorizontalDirection(), false, null, fleetsBeforeArrival, false);
		} else if (fleet.isOrbiting()) {
			final OrbitingFleet orbitingFleet = fleet.asOrbiting();

			return FleetView.createOrbiting(fleet.getId(), fleet.getPlayer(), race, emptyMap(),
					orbitingFleet.getSystem().getId(), orbitingFleet.getSystem().getLocation(), fleetsBeforeArrival,
					false, null, false);
		}

		throw new IllegalStateException("Unknown fleet type!");
	}
}