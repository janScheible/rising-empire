package com.scheible.risingempire.game.impl.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.GameOptions.FakeSystemNotificationProvider;
import com.scheible.risingempire.game.api.GameOptions.FakeTechProvider;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.TurnStatusBuilder;
import com.scheible.risingempire.game.api.ai.Ai;
import com.scheible.risingempire.game.api.ai.AiFactory;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrival;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl.colony.Colony;
import com.scheible.risingempire.game.impl.colony.ColonyManager;
import com.scheible.risingempire.game.impl.fleet.Fleet;
import com.scheible.risingempire.game.impl.fleet.FleetChanges;
import com.scheible.risingempire.game.impl.fleet.FleetFinder;
import com.scheible.risingempire.game.impl.fleet.FleetFormer;
import com.scheible.risingempire.game.impl.fleet.FleetIdGenerator;
import com.scheible.risingempire.game.impl.fleet.FleetManager;
import com.scheible.risingempire.game.impl.fleet.FleetTurn;
import com.scheible.risingempire.game.impl.fleet.JourneyCalculator;
import com.scheible.risingempire.game.impl.fleet.OrbitingFleet;
import com.scheible.risingempire.game.impl.fleet.SpaceCombatResolver;
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
import com.scheible.risingempire.game.impl.tech.TechManager;
import com.scheible.risingempire.game.impl.view.GameViewBuilder;
import com.scheible.risingempire.util.SeededRandom;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
public class GameImpl implements Game, FleetManager, ColonyManager, TechManager {

	private final GalaxySize galaxySize;

	private final Map<Player, Ai> ais = new EnumMap<>(Player.class);

	private final FleetIdGenerator fleetIdGenerator;

	private final Map<SystemId, System> systems;

	private final Map<Player, Fraction> fractions;

	private final Map<FleetId, Fleet> fleets;

	private final Map<Player, Map<FleetId, FleetId>> fleetChildParentMapping = new HashMap<>();

	private final Optional<FakeTechProvider> fakeTechProvider;

	private final Optional<FakeSystemNotificationProvider> fakeNotificationProvider;

	private final Map<Player, List<List<Entry<TechId, String>>>> selectTechGroups = new EnumMap<>(Player.class);

	private final Set<SpaceCombat> spaceCombats = new HashSet<>();

	private final Map<FleetId, Set<FleetBeforeArrival>> orbitingArrivingMapping = new HashMap<>();

	private final Set<ColonizeCommand> colonizeCommands = new HashSet<>();

	private final Set<AnnexCommand> annexCommands = new HashSet<>();

	private final Map<Player, Map<ColonyId, Map<ColonyId, Integer>>> colonistTransfers = new HashMap<>();

	private final Map<Player, Map<ColonyId, ColonyId>> shipRelocations = new HashMap<>();

	private final ShipDesignProvider shipDesignProvider;

	private final FleetFormer fleetFormer;

	private final JourneyCalculator journeyCalculator;

	private final FleetTurn fleetTurn;

	private final int annexationSiegeRounds;

	private final EnumSet<Player> finishedTurn = EnumSet.noneOf(Player.class);

	private int round = 1;

	public GameImpl(Set<System> systems, Set<Fraction> fractions, Set<StartFleet> startFleets,
			GameOptions gameOptions) {
		this.galaxySize = gameOptions.galaxySize();

		this.systems = Collections
			.unmodifiableMap(systems.stream().collect(Collectors.toMap(System::getId, Function.identity())));
		this.fractions = Collections
			.unmodifiableMap(fractions.stream().collect(Collectors.toMap(Fraction::getPlayer, Function.identity())));
		this.fleets = new HashMap<>();
		this.fleetIdGenerator = new FleetIdGenerator(this.fleets.keySet());
		this.fleets.putAll(startFleets.stream()
			.map(sf -> new OrbitingFleet(this.fleetIdGenerator.createRandom(), sf.getPlayer(), sf.getShips(),
					sf.getSystem(), this.round))
			.collect(Collectors.toMap(Fleet::getId, Function.identity())));

		this.fakeTechProvider = gameOptions.fakeTechProvider();
		this.fakeNotificationProvider = gameOptions.fakeSystemNotificationProvider();

		this.shipDesignProvider = (player, slot) -> this.fractions.get(player).getShipDesigns().get(slot);
		this.journeyCalculator = new JourneyCalculator(this.systems, this.shipDesignProvider,
				gameOptions.fleetSpeedFactor());

		FleetFinder fleetFinder = new FleetFinder(this.fleets, this.journeyCalculator);
		SeededRandom random = new SeededRandom();
		this.fleetFormer = new FleetFormer(this.fleetIdGenerator, fleetFinder, this.journeyCalculator);
		this.fleetTurn = new FleetTurn(() -> this.round, this.systems,
				(player, systemId, snapshot) -> this.fractions.get(player).updateSnapshot(systemId, snapshot),
				this.fleetFormer, fleetFinder,
				gameOptions.spaceCombatOutcome()
					.<SpaceCombatResolver>map(KnownInAdvanceWinnerSpaceCombatResolver::new)
					.orElseGet(() -> new SimulatingSpaceCombatResolver(random)),
				this.shipDesignProvider);

		this.annexationSiegeRounds = gameOptions.annexationSiegeRounds();
	}

	private void nextTurn() {
		this.round++;

		this.spaceCombats.clear();
		this.orbitingArrivingMapping.clear();
		this.fleetChildParentMapping.clear();

		List<Fleet> sortedFleets = new ArrayList<>(this.fleets.values());
		sortedFleets
			.sort((first, second) -> Double.compare(first.getDestinationDistance(), second.getDestinationDistance()));

		for (Fleet fleet : sortedFleets) {
			FleetChanges changes = this.fleetTurn.nextTurn(fleet);
			changes.getAdded().forEach(f -> this.fleets.put(f.getId(), f));
			changes.getRemoved().forEach(f -> this.fleets.remove(f.getId()));
			changes.getCombats()
				.forEach(spaceCombat -> this.spaceCombats
					.add(SpaceCombat.withOrder(spaceCombat, this.spaceCombats.size())));
			changes.getOrbitingArrivingMapping()
				.forEach((orbitingId, deployedIds) -> this.orbitingArrivingMapping
					.computeIfAbsent(orbitingId, key -> new HashSet<>())
					.addAll(deployedIds));
		}

		// TODO Create a new kind of transporter fleet for each transfer.
		this.colonistTransfers.clear();

		this.colonizeCommands.stream().forEach(command -> {
			if (this.fleets.containsKey(command.fleetId())) {
				Fleet fleet = this.fleets.get(command.fleetId());
				if (fleet.getPlayer() == command.player() && fleet.isOrbiting()
						&& fleet.asOrbiting().getSystem().getId().equals(command.systemId())) {
					OrbitingFleet orbiting = fleet.asOrbiting();
					FleetId fleetId = fleet.getId();
					System system = this.systems.get(command.systemId());

					Optional<DesignSlot> colonyShipSlot = fleet.getShips()
						.keySet()
						.stream()
						.filter(ds -> this.shipDesignProvider.get(fleet.getPlayer(), ds).hasColonyBase())
						.findFirst();

					if (colonyShipSlot.isPresent()) {
						orbiting.detach(Map.of(colonyShipSlot.get(), 1));
						system.colonize(orbiting.getPlayer(), colonyShipSlot.get());
						if (!orbiting.hasShips()) {
							this.fleets.remove(fleetId);
						}
					}
				}
			}
		});
		this.colonizeCommands.clear();

		this.annexCommands.stream()
			.forEach(command -> this.systems.get(command.systemId()).annex(command.player(), DesignSlot.FIRST));
		this.annexCommands.clear();
	}

	@Override
	public void selectTech(Player player, TechId techId) {
		List<Entry<TechId, String>> selectTechGroup = null;
		for (List<Entry<TechId, String>> techGroup : this.selectTechGroups.get(player)) {
			if (techGroup.stream().anyMatch(e -> e.getKey().equals(techId))) {
				selectTechGroup = techGroup;
				break;
			}
		}
		if (selectTechGroup == null) {
			throw new IllegalArgumentException("There is no tech group with a techId of '" + techId + "'");
		}

		this.selectTechGroups.get(player).remove(selectTechGroup);
	}

	@Override
	public List<List<Entry<TechId, String>>> getSelectTechs(Player player) {
		return unmodifiableList(new ArrayList<>(this.selectTechGroups.getOrDefault(player, List.of())));
	}

	public TurnStatus finishTurn(Player player) {
		validateTurnFinished(player);
		this.finishedTurn.add(player);

		this.ais.entrySet().stream().filter(e -> !this.finishedTurn.contains(e.getKey())).forEach(e -> {
			e.getValue().finishTurn(forPlayer(e.getKey()));
			validateTurnFinished(player);
			this.finishedTurn.add(e.getKey());
		});

		boolean turnFinished = this.fractions.size() == this.finishedTurn.size();
		if (turnFinished) {
			nextTurn();

			if (this.fakeTechProvider.isPresent()) {
				this.selectTechGroups.clear();

				this.finishedTurn.forEach(p -> this.selectTechGroups.put(p, this.fakeTechProvider.get()
					.get(p, this.round)
					.stream()
					.map(tgv -> tgv.stream().map(tv -> Map.entry(tv.id(), tv.name())).collect(Collectors.toList()))
					.collect(Collectors.toList())));
			}

			this.finishedTurn.clear();
		}

		return TurnStatusBuilder.builder().playerStatus(getTurnFinishedStatus()).roundFinished(turnFinished).build();
	}

	private void validateTurnFinished(Player player) {
		if (!getSelectTechs(player).isEmpty()) {
			throw new IllegalStateException(player + " can't finish turn because tech was not selected.");
		}
	}

	private Map<Player, Boolean> getTurnFinishedStatus() {
		return this.fractions.keySet()
			.stream()
			.collect(Collectors.toMap(Function.identity(), this.finishedTurn::contains, (l, r) -> l,
					() -> new EnumMap<>(Player.class)));
	}

	@Override
	public void deployFleet(Player player, FleetId fleetId, SystemId destinationId, ShipsView ships) {
		Fleet from = this.fleets.get(fleetId);

		SystemOrb source = from.isOrbiting() ? from.asOrbiting().getSystem() : from.asDeployed().getSource();
		SystemOrb destination = this.systems.get(destinationId);

		FleetChanges fleetChanges = this.fleetFormer.deployFleet(player, from, source, destination,
				DesignSlot.toSlotAndCounts(ships), this.round);
		fleetChanges.getAdded()
			.forEach(addedFleet -> this.fleetChildParentMapping.computeIfAbsent(player, key -> new HashMap<>())
				.put(addedFleet.getId(), from.getId()));
		fleetChanges.consume(addedFleet -> this.fleets.put(addedFleet.getId(), addedFleet),
				removedFleet -> this.fleets.remove(removedFleet.getId()));
	}

	private Optional<FleetId> getFleetParent(Player player, Fleet fleet) {
		FleetId current = fleet.getId();

		while (true) {
			FleetId parent = this.fleetChildParentMapping.getOrDefault(player, Map.of()).get(current);

			if (parent != null) {
				current = parent;
			}
			else {
				if (!current.equals(fleet.getId())) {
					return Optional.of(current);
				}
				else {
					return Optional.empty();
				}
			}
		}
	}

	@Override
	public void colonizeSystem(Player player, FleetId fleetId, boolean skip) {
		Fleet fleet = this.fleets.get(fleetId);

		if (!fleet.isOrbiting()) {
			throw new IllegalArgumentException(
					"The fleet '" + fleetId + "' can't colonize a system because it is deployed!");
		}

		OrbitingFleet orbiting = fleet.asOrbiting();

		Optional<DesignSlot> colonyShipSlot = fleet.getShips()
			.keySet()
			.stream()
			.filter(ds -> this.shipDesignProvider.get(fleet.getPlayer(), ds).hasColonyBase())
			.findFirst();

		if (colonyShipSlot.isEmpty()) {
			throw new IllegalArgumentException("The fleet '" + fleetId + "' can't colonize the '"
					+ orbiting.getSystem().getId() + "' system because it does not contain a colony ship!");
		}

		System system = this.systems.get(orbiting.getSystem().getId());

		if (!skip) {
			this.colonizeCommands.add(new ColonizeCommand(player, system.getId(), orbiting.getId()));
		}
		else {
			Iterator<ColonizeCommand> commandIterator = this.colonizeCommands.iterator();
			while (commandIterator.hasNext()) {
				ColonizeCommand command = commandIterator.next();
				if (command.player() == player && command.systemId().equals(system.getId())
						&& command.fleetId().equals(orbiting.getId())) {
					commandIterator.remove();
				}
			}
		}
	}

	@Override
	public boolean canColonize(FleetId fleetId) {
		Fleet fleet = this.fleets.get(fleetId);

		if (fleet.isOrbiting()) {
			OrbitingFleet orbiting = fleet.asOrbiting();
			SystemId systemId = orbiting.getSystem().getId();
			System system = this.systems.get(systemId);

			boolean hasColonyShip = orbiting.getShips()
				.keySet()
				.stream()
				.map(this.fractions.get(orbiting.getPlayer()).getShipDesigns()::get)
				.anyMatch(ShipDesign::hasColonyBase);

			if (hasColonyShip && system.getColony().isEmpty()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasColonizeCommand(Player player, SystemId systemId, FleetId fleetId) {
		return this.colonizeCommands.stream()
			.anyMatch(command -> command.player() == player && command.systemId().equals(systemId)
					&& command.fleetId().equals(fleetId));

	}

	@Override
	public void annexSystem(Player player, FleetId fleetId, boolean skip) {
		Fleet fleet = this.fleets.get(fleetId);

		if (!fleet.isOrbiting()) {
			throw new IllegalArgumentException(
					"The fleet '" + fleetId + "' can't annex a system because it is deployed!");
		}

		OrbitingFleet orbiting = fleet.asOrbiting();
		System system = this.systems.get(orbiting.getSystem().getId());

		if (system.getColony().isEmpty()) {
			throw new IllegalArgumentException("The fleet '" + orbiting + "' can't annex the system '" + system.getId()
					+ "' because there is no colony!");
		}
		else if (system.getColony().get().getPlayer() == player) {
			throw new IllegalArgumentException("The fleet '" + orbiting + "' can't annex the system '" + system.getId()
					+ "' because the colony already belong to " + player + "!");
		}
		else if (!isSiegeSuccessful(orbiting)) {
			throw new IllegalArgumentException("The fleet '" + orbiting + "' can't annex the system '" + system.getId()
					+ "' because the fleet is only there for " + (this.round - orbiting.getArrivalRound())
					+ " but must be at least " + this.annexationSiegeRounds + "!");
		}
		else {
			if (!skip) {
				this.annexCommands.add(new AnnexCommand(player, system.getId(), orbiting.getId()));
			}
			else {
				Iterator<AnnexCommand> annexIterator = this.annexCommands.iterator();
				while (annexIterator.hasNext()) {
					AnnexCommand command = annexIterator.next();
					if (command.player() == player && command.systemId().equals(system.getId())
							&& command.fleetId().equals(orbiting.getId())) {
						annexIterator.remove();
					}
				}
			}
		}
	}

	private boolean isSiegeSuccessful(OrbitingFleet orbiting) {
		return this.round - orbiting.getArrivalRound() >= this.annexationSiegeRounds;
	}

	@Override
	public boolean canAnnex(FleetId fleetId) {
		Fleet fleet = this.fleets.get(fleetId);

		if (fleet.isOrbiting()) {
			OrbitingFleet orbiting = fleet.asOrbiting();

			SystemId systemId = orbiting.getSystem().getId();
			System system = this.systems.get(systemId);

			Optional<Colony> colony = system.getColony();

			if (colony.isPresent() && colony.get().getPlayer() != orbiting.getPlayer() && isSiegeSuccessful(orbiting)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasAnnexCommand(Player player, SystemId systemId, FleetId fleetId) {
		return this.annexCommands.stream()
			.anyMatch(command -> command.player() == player && command.systemId().equals(systemId)
					&& command.fleetId().equals(fleetId));
	}

	@Override
	public Optional<Integer> getSiegeProgress(FleetId fleetId) {
		Fleet fleet = this.fleets.get(fleetId);
		if (fleet != null && fleet.isOrbiting()) {
			OrbitingFleet orbiting = fleet.asOrbiting();
			System system = this.systems.get(orbiting.getSystem().getId());

			if (system.getColony().isPresent() && system.getColony().get().getPlayer() != orbiting.getPlayer()) {
				return Optional.of((int) Math.min(100,
						100.0 * (this.round - orbiting.getArrivalRound()) / this.annexationSiegeRounds));
			}
		}

		return Optional.empty();
	}

	@Override
	public Optional<Integer> calcEta(Player player, FleetId fleetId, SystemId destinationId, ShipsView ships) {
		return this.journeyCalculator.calcEta(player, this.fleets.get(fleetId).getLocation(), destinationId,
				DesignSlot.toSlotAndCounts(ships), this.fractions.get(player).getTechnology().getFleetRange());
	}

	@Override
	public Optional<Integer> calcTranportColonistsEta(Player player, SystemId originId, SystemId destinationId) {
		return this.journeyCalculator.calcEta(player, this.systems.get(originId).getLocation(), destinationId,
				this.fractions.get(player).getTechnology().getMaxWarpSpeed(),
				this.fractions.get(player).getTechnology().getFleetRange());
	}

	@Override
	public void transferColonists(Player player, ColonyId originId, ColonyId destinationId, int colonists) {
		System originSystem = this.systems.get(SystemId.fromColonyId(originId));
		if (originSystem.getColony(player).isEmpty()) {
			throw new IllegalArgumentException(
					"Can't transfer colonists from " + originId + " bacause " + player + " has no colony there!");
		}
		Colony originColony = originSystem.getColony().get();
		if (colonists > originColony.getPopulation() / 2) {
			throw new IllegalArgumentException("At most half of a colony's population can be transfered!");
		}

		System destinationSystem = this.systems.get(SystemId.fromColonyId(destinationId));
		if (destinationSystem.getColony(player).isEmpty()) {
			throw new IllegalArgumentException(
					"Can't transfer colonists from " + destinationId + " bacause " + player + " has no colony there!");
		}

		this.colonistTransfers.computeIfAbsent(player, key -> new HashMap<>())
			.computeIfAbsent(originId, key -> new HashMap<>())
			.put(destinationId, colonists);
	}

	@Override
	public void relocateShips(Player player, ColonyId originId, ColonyId destinationId) {
		System originSystem = this.systems.get(SystemId.fromColonyId(originId));
		if (originSystem.getColony(player).isEmpty()) {
			throw new IllegalArgumentException(
					"Can't relocate ships from " + originId + " bacause " + player + " has no colony there!");
		}

		System destinationSystem = this.systems.get(SystemId.fromColonyId(destinationId));
		if (destinationSystem.getColony(player).isEmpty()) {
			throw new IllegalArgumentException(
					"Can't relocate ships to " + destinationId + " bacause " + player + " has no colony there!");
		}
		if (!originId.equals(destinationId)) {
			this.shipRelocations.computeIfAbsent(player, key -> new HashMap<>()).put(originId, destinationId);
		}
		else {
			Map<ColonyId, ColonyId> playersRelocations = this.shipRelocations.getOrDefault(player, Map.of());
			if (playersRelocations.containsKey(originId)) {
				playersRelocations.remove(originId);
			}
		}
	}

	@Override
	public SystemId getClosest(FleetId fleetId) {
		Fleet fleet = this.fleets.get(fleetId);

		System closest = null;
		Double distance = null;

		for (System system : this.systems.values()) {
			double currentDistance = system.getLocation().distance(fleet.getLocation());
			if (closest == null || currentDistance < distance) {
				closest = system;
				distance = currentDistance;
			}
		}

		return closest.getId();
	}

	@Override
	public ShipTypeView nextShipType(Player player, ColonyId colonyId) {
		Colony colony = this.systems.get(SystemId.fromColonyId(colonyId)).getColony(player).get();

		List<DesignSlot> usedSlots = new ArrayList<>(this.fractions.get(player).getShipDesigns().keySet());
		Collections.sort(usedSlots);
		usedSlots.add(usedSlots.get(0));
		DesignSlot nextSlot = usedSlots.get(usedSlots.indexOf(colony.getSpaceDock()) + 1);

		colony.build(nextSlot);
		return nextSlot.toShipType(this.fractions.get(player).getShipDesigns().get(nextSlot));
	}

	@Override
	public Map<ProductionArea, Integer> adjustRatio(Player player, ColonyId id, ProductionArea area, int percentage) {
		Colony colony = this.systems.get(SystemId.fromColonyId(id)).getColony(player).get();
		colony.adjustRation(area, percentage);
		return colony.getRatios();
	}

	GameView getGameState(Player player) {
		Map<Player, Race> playerRaceMapping = this.fractions.entrySet()
			.stream()
			.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getRace()));
		Map<Player, Map<DesignSlot, ShipDesign>> designs = this.fractions.keySet()
			.stream()
			.collect(Collectors.toMap(Function.identity(), p -> this.fractions.get(player).getShipDesigns()));
		Set<SystemNotificationView> systemNotifications = this.fakeNotificationProvider
			.map(fnp -> fnp.get(player, this.round))
			.orElseGet(Set::of);

		return GameViewBuilder.buildView(this.galaxySize, this.round, getTurnFinishedStatus(), player,
				playerRaceMapping, this.systems.values(), this.fleets.values(), designs, this.orbitingArrivingMapping,
				(c, sid) -> this.fractions.get(c).getSnapshot(sid), this.fractions.get(player).getTechnology(),
				this.spaceCombats, this, this, systemNotifications, this.annexationSiegeRounds,
				this.colonistTransfers.getOrDefault(player, Map.of()),
				this.shipRelocations.getOrDefault(player, Map.of()), this::getFleetParent);
	}

	@Override
	public PlayerGame forPlayer(Player player) {
		return new PlayerGameImpl(player, this);
	}

	@Override
	public void registerAi(Player player) {
		if (this.ais.get(player) == null) {
			this.ais.put(player, AiFactory.get().create());
		}
	}

	@Override
	public boolean aiControlled(Player player) {
		return this.ais.containsKey(player);
	}

	@Override
	public void unregisterAi(Player player) {
		this.ais.remove(player);
	}

	@Override
	public Set<Player> players() {
		return Collections
			.unmodifiableSet(this.fractions.values().stream().map(Fraction::getPlayer).collect(Collectors.toSet()));
	}

	@Override
	public int round() {
		return this.round;
	}

}
