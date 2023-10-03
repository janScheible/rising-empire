package com.scheible.risingempire.game.impl.game;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.GameOptions.FakeSystemNotificationProvider;
import com.scheible.risingempire.game.api.GameOptions.FakeTechProvider;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.view.FleetManager;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.ai.Ai;
import com.scheible.risingempire.game.api.view.ai.AiFactory;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ColonyManager;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.tech.TechManager;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.game.impl.colony.Colony;
import com.scheible.risingempire.game.impl.fleet.Fleet;
import com.scheible.risingempire.game.impl.fleet.FleetChanges;
import com.scheible.risingempire.game.impl.fleet.FleetFinder;
import com.scheible.risingempire.game.impl.fleet.FleetFormer;
import com.scheible.risingempire.game.impl.fleet.FleetIdGenerator;
import com.scheible.risingempire.game.impl.fleet.FleetTurn;
import com.scheible.risingempire.game.impl.fleet.JourneyCalculator;
import com.scheible.risingempire.game.impl.fleet.OrbitingFleet;
import com.scheible.risingempire.game.impl.fleet.StartFleet;
import com.scheible.risingempire.game.impl.fraction.Fraction;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.ship.ShipDesignProvider;
import com.scheible.risingempire.game.impl.spacecombat.SpaceCombat;
import com.scheible.risingempire.game.impl.spacecombat.resolver.predetermined.KnownInAdvanceWinnerSpaceCombatResolver;
import com.scheible.risingempire.game.impl.spacecombat.resolver.simulated.SimulatingSpaceCombatResolver;
import com.scheible.risingempire.game.impl.system.System;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import com.scheible.risingempire.game.impl.view.GameViewBuilder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
@SuppressFBWarnings(value = "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY", justification = "GameViewImpl must access game to perform all actions.")
public class GameImpl implements Game, FleetManager, ColonyManager, TechManager {

	private final GalaxySize galaxySize;

	private final Map<Player, Ai> ais = new EnumMap<>(Player.class);

	private final FleetIdGenerator fleetIdGenerator;

	private final Map<SystemId, System> systems;
	private final Map<Player, Fraction> fractions;
	private final Map<FleetId, Fleet> fleets;

	private final FakeTechProvider fakeTechProvider;
	private final FakeSystemNotificationProvider fakeNotificationProvider;

	private final Map<Player, List<List<Entry<TechId, String>>>> selectTechGroups = new EnumMap<>(Player.class);
	private final Set<SpaceCombat> spaceCombats = new HashSet<>();
	private final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping = new HashMap<>();

	private final ShipDesignProvider shipDesignProvider;
	private final FleetFormer fleetFormer;
	private final JourneyCalculator journeyCalculator;
	private final FleetTurn fleetTurn;
	private final int annexationSiegeTurns;

	private final EnumSet<Player> finishedTurn = EnumSet.noneOf(Player.class);
	private int round = 1;

	public GameImpl(final Set<System> systems, final Set<Fraction> fractions, final Set<StartFleet> startFleets,
			final GameOptions gameOptions) {
		this.galaxySize = gameOptions.getGalaxySize();

		this.systems = Collections
				.unmodifiableMap(systems.stream().collect(Collectors.toMap(System::getId, identity())));
		this.fractions = Collections
				.unmodifiableMap(fractions.stream().collect(Collectors.toMap(Fraction::getPlayer, identity())));
		this.fleets = new HashMap<>();
		fleetIdGenerator = new FleetIdGenerator(this.fleets.keySet());
		this.fleets
				.putAll(startFleets
						.stream().map(sf -> new OrbitingFleet(fleetIdGenerator.createRandom(), sf.getPlayer(),
								sf.getShips(), sf.getSystem(), round))
						.collect(Collectors.toMap(Fleet::getId, identity())));

		this.fakeTechProvider = gameOptions.getFakeTechProvider().orElse(null);
		this.fakeNotificationProvider = gameOptions.getFakeNotificationProvider().orElse(null);

		shipDesignProvider = (player, slot) -> this.fractions.get(player).getShipDesigns().get(slot);
		journeyCalculator = new JourneyCalculator(this.systems, this.fleets, shipDesignProvider,
				gameOptions.getFleetSpeedFactor());

		final FleetFinder fleetFinder = new FleetFinder(fleets, journeyCalculator);
		fleetFormer = new FleetFormer(fleetIdGenerator, fleetFinder, journeyCalculator);
		fleetTurn = new FleetTurn(() -> round, this.systems,
				(player, systemId, snapshot) -> this.fractions.get(player).updateSnapshot(systemId, snapshot),
				fleetFormer, fleetFinder,
				gameOptions.getSpaceCombatOutcome() == null ? new SimulatingSpaceCombatResolver()
						: new KnownInAdvanceWinnerSpaceCombatResolver(gameOptions.getSpaceCombatOutcome()),
				shipDesignProvider);

		this.annexationSiegeTurns = gameOptions.getAnnexationSiegeTurns();
	}

	private void nextTurn() {
		round++;
		spaceCombats.clear();
		orbitingArrivingMapping.clear();

		final List<Fleet> sortedFleets = new ArrayList<>(fleets.values());
		sortedFleets.sort(
				(first, second) -> Double.compare(first.getDestinationDistance(), second.getDestinationDistance()));

		for (final Fleet fleet : sortedFleets) {
			final FleetChanges changes = fleetTurn.nextTurn(fleet);
			changes.getAdded().forEach(f -> fleets.put(f.getId(), f));
			changes.getRemoved().forEach(f -> fleets.remove(f.getId()));
			changes.getCombats()
					.forEach(spaceCombat -> spaceCombats.add(SpaceCombat.withOrder(spaceCombat, spaceCombats.size())));
			changes.getOrbitingArrivingMapping().forEach((orbitingId, deployedIds) -> orbitingArrivingMapping
					.computeIfAbsent(orbitingId, key -> new HashSet<>()).addAll(deployedIds));
		}
	}

	@Override
	public void selectTech(final Player player, final TechId techId) {
		List<Entry<TechId, String>> selectTechGroup = null;
		for (final List<Entry<TechId, String>> techGroup : selectTechGroups.get(player)) {
			if (techGroup.stream().anyMatch(e -> e.getKey().equals(techId))) {
				selectTechGroup = techGroup;
				break;
			}
		}
		if (selectTechGroup == null) {
			throw new IllegalArgumentException("There is no tech group with a techId of '" + techId + "'");
		}

		selectTechGroups.get(player).remove(selectTechGroup);
	}

	@Override
	public List<List<Entry<TechId, String>>> getSelectTechs(final Player player) {
		return unmodifiableList(new ArrayList<>(selectTechGroups.getOrDefault(player, emptyList())));
	}

	public TurnStatus finishTurn(final Player player) {
		validateTurnFinished(player);
		finishedTurn.add(player);

		ais.entrySet().stream().filter(e -> !finishedTurn.contains(e.getKey())).forEach(e -> {
			e.getValue().finishTurn(forPlayer(e.getKey()));
			validateTurnFinished(player);
			finishedTurn.add(e.getKey());
		});

		final boolean turnFinished = fractions.size() == finishedTurn.size();
		if (turnFinished) {
			nextTurn();

			if (fakeTechProvider != null) {
				selectTechGroups.clear();

				finishedTurn.forEach(p -> selectTechGroups.put(p,
						fakeTechProvider.get(p, round).stream().map(tgv -> tgv.stream()
								.map(tv -> (Entry<TechId, String>) new SimpleImmutableEntry<>(tv.getId(), tv.getName()))
								.collect(Collectors.toList())).collect(Collectors.toList())));
			}

			finishedTurn.clear();
		}

		return new TurnStatus(getTurnFinishedStatus(), turnFinished);
	}

	private void validateTurnFinished(final Player player) {
		if (!getSelectTechs(player).isEmpty()) {
			throw new IllegalStateException(player + " can't finish turn because tech was not selected.");
		}
	}

	private Map<Player, Boolean> getTurnFinishedStatus() {
		return fractions.keySet().stream().collect(Collectors.toMap(identity(), c -> finishedTurn.contains(c),
				(l, r) -> l, () -> new EnumMap<>(Player.class)));
	}

	@Override
	public void deployFleet(final Player player, final FleetId fleetId, final SystemId destinationId,
			final Map<ShipTypeId, Integer> ships) {
		final Fleet from = fleets.get(fleetId);

		final SystemOrb source = from.isOrbiting() ? from.asOrbiting().getSystem() : from.asDeployed().getSource();
		final SystemOrb destination = systems.get(destinationId);

		fleetFormer.deployFleet(player, from, source, destination, DesignSlot.toSlotAndCounts(ships.entrySet()), round)
				.consume(addedFleet -> fleets.put(addedFleet.getId(), addedFleet),
						removedFleet -> fleets.remove(removedFleet.getId()));
	}

	@Override
	public void colonizeSystem(final Player player, final FleetId fleetId) {
		final Fleet fleet = fleets.get(fleetId);

		if (!fleet.isOrbiting()) {
			throw new IllegalArgumentException(
					"The fleet '" + fleetId + "' can't colonize a system because it is deployed!");
		}

		final OrbitingFleet orbiting = fleet.asOrbiting();

		final Optional<DesignSlot> colonyShipSlot = fleet.getShips().keySet().stream()
				.filter(ds -> shipDesignProvider.get(fleet.getPlayer(), ds).hasColonyBase()).findFirst();

		if (colonyShipSlot.isEmpty()) {
			throw new IllegalArgumentException("The fleet '" + fleetId + "' can't colonize the '"
					+ orbiting.getSystem().getId() + "' system because it does not contain a colony ship!");
		}

		final System system = systems.get(orbiting.getSystem().getId());

		orbiting.detach(Map.of(colonyShipSlot.get(), 1));
		system.colonize(orbiting.getPlayer(), colonyShipSlot.get());
		if (!orbiting.hasShips()) {
			fleets.remove(fleetId);
		}
	}

	@Override
	public void annexSystem(final Player player, final FleetId fleetId) {
		final Fleet fleet = fleets.get(fleetId);

		if (!fleet.isOrbiting()) {
			throw new IllegalArgumentException(
					"The fleet '" + fleetId + "' can't annex a system because it is deployed!");
		}

		final OrbitingFleet orbiting = fleet.asOrbiting();
		final System system = systems.get(orbiting.getSystem().getId());

		if (system.getColony().isEmpty()) {
			throw new IllegalArgumentException("The fleet '" + orbiting + "' can't annex the system '" + system.getId()
					+ "' because there is no colony!");
		} else if (system.getColony().get().getPlayer() == player) {
			throw new IllegalArgumentException("The fleet '" + orbiting + "' can't annex the system '" + system.getId()
					+ "' because the colony already belong to " + player + "!");
		} else if (!isSiegeSuccessful(orbiting)) {
			throw new IllegalArgumentException("The fleet '" + orbiting + "' can't annex the system '" + system.getId()
					+ "' because the fleet is only there for " + (round - orbiting.getArrivalRound())
					+ " but must be at least " + annexationSiegeTurns + "!");
		} else {
			system.annex(player, DesignSlot.FIRST);
		}
	}

	private boolean isSiegeSuccessful(final OrbitingFleet orbiting) {
		return round - orbiting.getArrivalRound() >= annexationSiegeTurns;
	}

	@Override
	public boolean canAnnex(final FleetId fleetId) {
		final Fleet fleet = fleets.get(fleetId);
		if (fleet.isOrbiting()) {
			final OrbitingFleet orbiting = fleet.asOrbiting();

			final SystemId systemId = orbiting.getSystem().getId();
			final System system = systems.get(systemId);

			final Optional<Colony> colony = system.getColony();

			if (colony.isPresent() && colony.get().getPlayer() != orbiting.getPlayer() && isSiegeSuccessful(orbiting)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Optional<Integer> calcEta(final Player player, final FleetId fleetId, final SystemId destinationId,
			final Map<ShipTypeId, Integer> ships) {
		return journeyCalculator.calcEta(player, fleetId, destinationId, DesignSlot.toSlotAndCounts(ships.entrySet()),
				fractions.get(player).getTechnology().getFleetRange());
	}

	@Override
	public SystemId getClosest(final FleetId fleetId) {
		final Fleet fleet = fleets.get(fleetId);

		System closest = null;
		Double distance = null;

		for (final System system : systems.values()) {
			final double currentDistance = system.getLocation().getDistance(fleet.getLocation());
			if (closest == null || currentDistance < distance) {
				closest = system;
				distance = currentDistance;
			}
		}

		return closest.getId();
	}

	@Override
	public ShipTypeView nextShipType(final Player player, final ColonyId colonyId) {
		final Colony colony = systems.get(SystemId.fromColonyId(colonyId)).getColony(player).get();

		final List<DesignSlot> usedSlots = new ArrayList<>(fractions.get(player).getShipDesigns().keySet());
		Collections.sort(usedSlots);
		usedSlots.add(usedSlots.get(0));
		final DesignSlot nextSlot = usedSlots.get(usedSlots.indexOf(colony.getSpaceDock()) + 1);

		colony.build(nextSlot);
		return nextSlot.toShipType(fractions.get(player).getShipDesigns().get(nextSlot));
	}

	@Override
	public Map<ProductionArea, Integer> adjustRatio(final Player player, final ColonyId id, final ProductionArea area,
			final int percentage) {
		final Colony colony = systems.get(SystemId.fromColonyId(id)).getColony(player).get();
		colony.adjustRation(area, percentage);
		return colony.getRatios();
	}

	GameView getGameState(final Player player) {
		final Map<Player, Race> playerRaceMapping = fractions.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getRace()));
		final Map<Player, Map<DesignSlot, ShipDesign>> designs = fractions.keySet().stream()
				.collect(Collectors.toMap(identity(), p -> fractions.get(player).getShipDesigns()));
		final Set<SystemNotificationView> systemNotifications = fakeNotificationProvider != null
				? fakeNotificationProvider.get(player, round)
				: emptySet();

		return GameViewBuilder.buildView(galaxySize, round, getTurnFinishedStatus(), player, playerRaceMapping,
				systems.values(), fleets.values(), designs, orbitingArrivingMapping,
				(c, sid) -> fractions.get(c).getSnapshot(sid), fractions.get(player).getTechnology(), spaceCombats,
				this, this, this, systemNotifications);
	}

	@Override
	public PlayerGame forPlayer(final Player player) {
		return new PlayerGameImpl(player, this);
	}

	@Override
	public void registerAi(final Player player) {
		if (ais.get(player) == null) {
			ais.put(player, AiFactory.get().create());
		}
	}

	@Override
	public boolean isAiControlled(final Player player) {
		return ais.containsKey(player);
	}

	@Override
	public void unregisterAi(final Player player) {
		ais.remove(player);
	}

	@Override
	public Set<Player> getPlayers() {
		return Collections
				.unmodifiableSet(fractions.values().stream().map(Fraction::getPlayer).collect(Collectors.toSet()));
	}

	@Override
	public int getRound() {
		return round;
	}
}
