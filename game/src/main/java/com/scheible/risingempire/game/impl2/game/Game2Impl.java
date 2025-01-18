package com.scheible.risingempire.game.impl2.game;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colonization.Colonize;
import com.scheible.risingempire.game.impl2.colonization.Colonization.ColonyCommand;
import com.scheible.risingempire.game.impl2.colonization.Colonization.SpaceDockShipClass;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.empire.Empire;
import com.scheible.risingempire.game.impl2.empire.Empires;
import com.scheible.risingempire.game.impl2.game.Adapters.BuildCapacityProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ColoniesProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ColonyFleetProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ColonyIntelProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ControlledSystemProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.EncounteringFleetShipsProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.FleetItinearySegmentProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.NewShipsProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.OrbitingFleetsProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ResearchPointProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ScanAreasProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ShipMovementSpecsProviderAdapter;
import com.scheible.risingempire.game.impl2.game.Adapters.ShipScannerSpecsProviderAdapter;
import com.scheible.risingempire.game.impl2.intelligence.fleet.FleetIntelligence;
import com.scheible.risingempire.game.impl2.intelligence.fleet.ScanAreasProvider.ScanArea;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemIntelligence;
import com.scheible.risingempire.game.impl2.military.Military;
import com.scheible.risingempire.game.impl2.military.Military.Annex;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Navy;
import com.scheible.risingempire.game.impl2.navy.Navy.Deploy;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployJustLeaving;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployOrbiting;
import com.scheible.risingempire.game.impl2.navy.Navy.RelocateShips;
import com.scheible.risingempire.game.impl2.navy.Navy.TransferColonists;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.navy.eta.EtaCalculator;
import com.scheible.risingempire.game.impl2.ship.Shipyard;
import com.scheible.risingempire.game.impl2.spaceforce.RetreatingFleet;
import com.scheible.risingempire.game.impl2.spaceforce.SpaceForce;
import com.scheible.risingempire.game.impl2.technology.Technology;
import com.scheible.risingempire.game.impl2.technology.Technology.SelectTechnology;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.Universe;
import com.scheible.risingempire.game.impl2.view.FleetIdMapper;
import com.scheible.risingempire.game.impl2.view.FleetIdMapper.DeployedFleetId;
import com.scheible.risingempire.game.impl2.view.FleetIdMapper.DomainFleetId;
import com.scheible.risingempire.game.impl2.view.FleetIdMapper.OrbitingFleetId;
import com.scheible.risingempire.game.impl2.view.FleetViewMapper;
import com.scheible.risingempire.game.impl2.view.LocationMapper;
import com.scheible.risingempire.game.impl2.view.SystemIdMapper;
import com.scheible.risingempire.game.impl2.view.SystemViewMapper;

public class Game2Impl implements Game {

	private final Universe universe;

	private final Empires empires;

	private final Technology technology;

	private final Shipyard shipyard;

	private final Navy navy;

	private final EtaCalculator etaCalculator;

	private final Colonization colonization;

	private final Military military;

	private final SpaceForce spaceForce;

	private final SystemIntelligence systemIntelligence;

	private final FleetIntelligence fleetIntelligence;

	private Round round;

	private final PlayerTurns playerTurns;

	public Game2Impl(GalaxySize galaxySize, List<Empire> empires, List<Star> stars, List<Fleet> fleets) {
		ColoniesProviderAdapter coloniesProviderAdapter = new ColoniesProviderAdapter();
		ShipMovementSpecsProviderAdapter shipMovementSpecsProviderAdapter = new ShipMovementSpecsProviderAdapter();
		ColonyFleetProviderAdapter colonyFleetProviderAdapter = new ColonyFleetProviderAdapter();
		ControlledSystemProviderAdapter controlledSystemProviderAdapter = new ControlledSystemProviderAdapter();
		BuildCapacityProviderAdapter buildCapacityProviderAdpater = new BuildCapacityProviderAdapter();
		ResearchPointProviderAdapter researchPointProviderAdapter = new ResearchPointProviderAdapter();
		NewShipsProviderAdapter newShipsProviderAdapter = new NewShipsProviderAdapter();
		OrbitingFleetsProviderAdapter orbitingFleetsProviderAdapter = new OrbitingFleetsProviderAdapter();
		ColonyIntelProviderAdapter colonyProviderAdapter = new ColonyIntelProviderAdapter();
		ScanAreasProviderAdapter scanAreasProviderAdapter = new ScanAreasProviderAdapter();
		ShipScannerSpecsProviderAdapter shipScannerSpecsProviderAdapter = new ShipScannerSpecsProviderAdapter();
		FleetItinearySegmentProviderAdapter fleetItinearySegmentProviderAdapter = new FleetItinearySegmentProviderAdapter();
		EncounteringFleetShipsProviderAdapter encounteringFleetShipsProviderAdapter = new EncounteringFleetShipsProviderAdapter();

		this.universe = new Universe(LocationMapper.fromLocationValue(galaxySize.width()),
				LocationMapper.fromLocationValue(galaxySize.height()), stars);
		this.empires = new Empires(empires);
		this.technology = new Technology(researchPointProviderAdapter);
		this.shipyard = new Shipyard(buildCapacityProviderAdpater);
		this.navy = new Navy(fleets, shipMovementSpecsProviderAdapter, newShipsProviderAdapter);
		this.etaCalculator = new EtaCalculator(shipMovementSpecsProviderAdapter, coloniesProviderAdapter);
		this.colonization = new Colonization(colonyFleetProviderAdapter);
		this.military = new Military(controlledSystemProviderAdapter);
		this.spaceForce = new SpaceForce(encounteringFleetShipsProviderAdapter);
		this.systemIntelligence = new SystemIntelligence(orbitingFleetsProviderAdapter, colonyProviderAdapter);
		this.fleetIntelligence = new FleetIntelligence(scanAreasProviderAdapter, shipScannerSpecsProviderAdapter,
				fleetItinearySegmentProviderAdapter);

		coloniesProviderAdapter.delegate(this.colonization);
		shipMovementSpecsProviderAdapter.delegate(this.technology);
		colonyFleetProviderAdapter.delegate(this::colonizableSystems);
		controlledSystemProviderAdapter.delegate(this::controlledSystems);
		buildCapacityProviderAdpater.delegate(this.colonization);
		researchPointProviderAdapter.delegate(this.colonization);
		newShipsProviderAdapter.delegate(this.shipyard);
		orbitingFleetsProviderAdapter.delegate(this.navy);
		colonyProviderAdapter.delegate(this.colonization);
		scanAreasProviderAdapter.delegate(this::scanAreas);
		shipScannerSpecsProviderAdapter.delegate(this.technology);
		fleetItinearySegmentProviderAdapter.delegate(this.navy);
		encounteringFleetShipsProviderAdapter.delegate(this.navy);

		this.round = new Round(1);
		this.playerTurns = new PlayerTurns(this.empires.players());
	}

	@Override
	public PlayerGame forPlayer(Player player) {
		return new PlayerGame2Impl(player);
	}

	@Override
	public Set<Player> players() {
		return this.empires.players();
	}

	@Override
	public void registerAi(Player player) {
		this.playerTurns.enableAutoTurn(player);
	}

	@Override
	public boolean aiControlled(Player player) {
		return this.playerTurns.autoTurn(player);
	}

	@Override
	public void unregisterAi(Player player) {
		this.playerTurns.disableAutoTurn(player);
	}

	@Override
	public int round() {
		return this.round.quantity();
	}

	private TurnStatus finishTurn(Player player) {
		this.playerTurns.finishTurn(player);

		boolean roundFinished = this.playerTurns.roundFinished();
		if (roundFinished) {
			finishRound();
		}

		return new TurnStatus(this.playerTurns.turnStatus(), roundFinished);
	}

	private void finishRound() {
		this.colonization.updateColonies(this.playerTurns.commands(ColonyCommand.class));
		this.technology.advanceResearch(this.playerTurns.commands(SelectTechnology.class));
		this.shipyard.buildShips();
		this.navy.commissionNewShips();
		this.navy.issueRelocations(this.playerTurns.commands(RelocateShips.class));
		this.navy.moveFleets(this.round, this.playerTurns.commands(Deploy.class));
		this.spaceForce.resolveSpaceCombats();
		this.navy.removeDestroyedShips();
		this.colonization.welcomeColonistTransports();
		this.colonization.colonizeSystems(this.playerTurns.commands(Colonize.class));
		this.military.annexSystems(this.playerTurns.commands(Annex.class));
		this.systemIntelligence.recon(this.round);

		this.round = this.round.next();
		this.playerTurns.beginNewRound(this.round);

		for (RetreatingFleet retreatingFleet : this.spaceForce.retreatingFleets()) {
			Fleet fleet = this.navy.findOrbiting(retreatingFleet.player(), retreatingFleet.position()).orElseThrow();
			Position closestColony = this.universe
				.closest(retreatingFleet.position(),
						star -> this.colonization.colony(retreatingFleet.player(), star.position()).isPresent())
				.position();
			this.playerTurns.addCommand(retreatingFleet.player(), new DeployOrbiting(retreatingFleet.player(),
					retreatingFleet.position(), closestColony, fleet.ships()));
		}
	}

	private Set<Position> colonizableSystems(Player player) {
		return this.navy.fleets()
			.stream()
			.filter(f -> f.player().equals(player) && f.orbiting()
					&& this.shipyard.colonizable(player, f.ships().counts().keySet(),
							this.universe.planet(f.location().current()).type()))
			.map(f -> f.location().current())
			.collect(Collectors.toSet());
	}

	private Set<Position> controlledSystems(Player player) {
		return this.navy.fleets()
			.stream()
			.filter(f -> f.orbiting() && this.colonization.colony(player, f.location().current()).isEmpty())
			.map(f -> f.location().current())
			.collect(Collectors.toSet());
	}

	private Set<ScanArea> scanAreas(Player player) {
		return Stream
			.concat(this.colonization.colonies(player)
				.stream()
				.map(c -> new ScanArea(c.position(), this.technology.colonyScanRange(player))),
					this.navy.fleets()
						.stream()
						.filter(f -> f.player().equals(player))
						.map(f -> new ScanArea(f.location().current(),
								this.technology.effectiveScanRange(f.player(), f.ships().counts().keySet()))))
			.collect(Collectors.toSet());
	}

	private class PlayerGame2Impl implements PlayerGame {

		private final Player player;

		private PlayerGame2Impl(Player player) {
			this.player = player;
		}

		@Override
		public GameView view() {
			Colonization colonization = colonization();

			return GameView.builder()
				.galaxyWidth(LocationMapper.toLocationValue(Game2Impl.this.universe.width()))
				.galaxyHeight(LocationMapper.toLocationValue(Game2Impl.this.universe.height()))
				.player(this.player)
				.race(Game2Impl.this.empires.race(this.player))
				.players(Game2Impl.this.players())
				.round(Game2Impl.this.round())
				.turnFinishedStatus(Game2Impl.this.playerTurns.turnStatus())
				.systems(Game2Impl.this.universe.stars()
					.stream()
					.map(star -> Map.entry(SystemIdMapper.toSystemId(star.position()),
							SystemViewMapper.toSystemView(Game2Impl.this.round, this.player, star,
									Game2Impl.this.universe.planet(star), Game2Impl.this.technology,
									Game2Impl.this.universe, colonization, Game2Impl.this.systemIntelligence,
									Game2Impl.this.military, Game2Impl.this.empires, Game2Impl.this.spaceForce,
									Game2Impl.this.shipyard)))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
				.fleets(navy().fleets()
					.stream()
					.flatMap(fleet -> FleetViewMapper
						.toFleetView(this.player, fleet, Game2Impl.this.universe, Game2Impl.this.technology,
								Game2Impl.this.shipyard, Game2Impl.this.fleetIntelligence, Game2Impl.this.spaceForce,
								Game2Impl.this.empires)
						.stream())
					.map(f -> Map.entry(f.id(), f))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
				.spaceCombats(Game2Impl.this.spaceForce.spaceCombats()
					.stream()
					.map(spaceCombat -> SpaceCombatView.builder()
						.systemId(SystemIdMapper.toSystemId(spaceCombat.system()))
						.fireExchangeCount(0)
						.attacker(Game2Impl.this.empires.race(spaceCombat.attacker()))
						.attackerPlayer(spaceCombat.attacker())
						.attackerFleets(Set.of())
						.attackerShipSpecs(List.of())
						.defender(Game2Impl.this.empires.race(spaceCombat.defender()))
						.defenderPlayer(spaceCombat.defender())
						.defenderFleet(Optional.empty())
						.defenderFleetsBeforeArrival(Set.of())
						.defenderShipSpecs(List.of())
						.outcome(spaceCombat.outcome())
						.build())
					.collect(Collectors.toSet()))
				.selectTechGroups(Set.of())
				.build();
		}

		@Override
		public Optional<Integer> calcEta(FleetId fleetId, SystemId destinationId, ShipsView ships) {
			Navy navy = navy();

			Position origin = switch (FleetIdMapper.fromFleetId(fleetId)) {
				case OrbitingFleetId orbitingFleetId -> orbitingFleetId.system();
				case DeployedFleetId deployedFleetId ->
					navy.findDispatched(this.player, deployedFleetId.origin(), deployedFleetId.destination(),
							deployedFleetId.dispatchment(), deployedFleetId.speed())
						.orElseThrow()
						.location()
						.current();
			};

			return Game2Impl.this.etaCalculator
				.calc(this.player, origin, SystemIdMapper.fromSystemId(destinationId), toShips(ships))
				.map(Rounds::quantity);
		}

		@Override
		public Optional<Integer> calcTranportColonistsEta(SystemId originId, SystemId destinationId) {
			return Game2Impl.this.etaCalculator
				.calc(this.player, SystemIdMapper.fromSystemId(originId), SystemIdMapper.fromSystemId(destinationId),
						Ships.COLONISTS_TRANSPORTER)
				.map(Rounds::quantity);
		}

		@Override
		public Player player() {
			return this.player;
		}

		@Override
		public void nextShipType(ColonyId colonyId) {
			requireOwnership(colonyId);

			Position system = SystemIdMapper.fromColonyId(colonyId);

			Colony colony = colonization().colony(this.player, system).orElseThrow();

			Game2Impl.this.playerTurns.removeCommands(this.player,
					command -> command.player().equals(this.player) && command.colony().equals(system),
					SpaceDockShipClass.class);

			Game2Impl.this.playerTurns.addCommand(this.player, new SpaceDockShipClass(this.player, system,
					Game2Impl.this.shipyard.nextShipClass(colony.spaceDockShipClass())));
		}

		@Override
		public void transferColonists(ColonyId originId, ColonyId destinationId, int colonists) {
			requireOwnership(originId);

			Position colonySystem = SystemIdMapper.fromColonyId(originId);

			if (Game2Impl.this.colonization.transfareable(this.player, colonySystem, colonists)) {
				Game2Impl.this.playerTurns.addCommand(this.player, new TransferColonists(this.player, colonySystem,
						SystemIdMapper.fromColonyId(destinationId), colonists));
			}
			else {
				throw new IllegalArgumentException(
						"The colony " + originId + " hasn't " + colonists + " available for transfer!");
			}
		}

		@Override
		public void relocateShips(ColonyId originId, ColonyId destinationId) {
			requireOwnership(originId);

			Game2Impl.this.playerTurns.addCommand(this.player, new RelocateShips(this.player,
					SystemIdMapper.fromColonyId(originId), SystemIdMapper.fromColonyId(destinationId)));
		}

		@Override
		public void colonizeSystem(SystemId systemId, FleetId fleetId, boolean skip) {
			Position system = SystemIdMapper.fromSystemId(systemId);

			if (Game2Impl.this.colonizableSystems(this.player).stream().anyMatch(cs -> cs.equals(system))) {
				Game2Impl.this.playerTurns.addCommand(this.player, new Colonize(this.player, system, skip));
			}
			else {
				throw new IllegalArgumentException("The system " + systemId + " can't be colonized!");
			}
		}

		@Override
		public void annexSystem(ColonyId colonyId, FleetId fleetId, boolean skip) {
			Position system = SystemIdMapper.fromColonyId(colonyId);

			if (Game2Impl.this.military.annexable(this.player, system)) {
				Game2Impl.this.playerTurns.addCommand(this.player, new Annex(this.player, system, skip));
			}
			else {
				throw new IllegalArgumentException("The colony " + colonyId + " can't be annexed!");
			}
		}

		@Override
		public void deployFleet(FleetId fleetId, SystemId destinationId, ShipsView ships) {
			if (calcEta(fleetId, destinationId, ships).isEmpty()) {
				throw new IllegalArgumentException(
						"The star " + destinationId + " is beyond the reach of the " + fleetId + " fleet.");
			}

			DomainFleetId domainFleetId = FleetIdMapper.fromFleetId(fleetId);

			Deploy deployment = switch (domainFleetId) {
				case OrbitingFleetId(Position origin) ->
					new DeployOrbiting(this.player, origin, SystemIdMapper.fromSystemId(destinationId), toShips(ships));
				case DeployedFleetId(Position origin, Position destination, Round _, Speed speed) ->
					new DeployJustLeaving(this.player, origin, destination, speed,
							SystemIdMapper.fromSystemId(destinationId), toShips(ships));
			};

			Game2Impl.this.playerTurns.addCommand(this.player, deployment);
		}

		@Override
		public void selectTech(TechId techId) {
			if (Game2Impl.this.technology.selectableTechnologies(this.player).contains(techId)) {
				Game2Impl.this.playerTurns.addCommand(this.player, new SelectTechnology(this.player, techId));
			}
			else {
				throw new IllegalArgumentException("The technology " + techId + " can't be selected!");
			}
		}

		@Override
		public TurnStatus finishTurn() {
			return Game2Impl.this.finishTurn(this.player);
		}

		private Navy navy() {
			return Game2Impl.this.navy.apply(Game2Impl.this.round,
					Game2Impl.this.playerTurns.commands(this.player, Deploy.class));
		}

		private Colonization colonization() {
			return Game2Impl.this.colonization
				.apply(Game2Impl.this.playerTurns.commands(this.player, ColonyCommand.class));
		}

		private static Ships toShips(ShipsView ships) {
			return new Ships(ships.ships()
				.entrySet()
				.stream()
				.map(e -> Map.entry(new ShipClassId(e.getKey().id().value()), e.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}

		private void requireOwnership(ColonyId colonyId) {
			Position colonySystem = SystemIdMapper.fromColonyId(colonyId);

			if (!Game2Impl.this.colonization.colony(this.player, colonySystem).isPresent()) {
				throw new IllegalArgumentException("The player " + this.player + " has no colony at " + colonyId + "!");
			}
		}

	}

}
