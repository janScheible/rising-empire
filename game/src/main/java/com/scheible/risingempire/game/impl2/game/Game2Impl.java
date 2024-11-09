package com.scheible.risingempire.game.impl2.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.GameViewBuilder;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.empire.Empire;
import com.scheible.risingempire.game.impl2.game.FleetIdMapper.DeployedFleetId;
import com.scheible.risingempire.game.impl2.game.FleetIdMapper.DomainFleetId;
import com.scheible.risingempire.game.impl2.game.FleetIdMapper.OrbitingFleetId;
import com.scheible.risingempire.game.impl2.intelligence.Intelligence;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Navy;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployJustLeaving;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployOrbiting;
import com.scheible.risingempire.game.impl2.navy.Navy.Deployment;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;
import com.scheible.risingempire.game.impl2.ship.Shipyard;
import com.scheible.risingempire.game.impl2.technology.Technology;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.Universe;

public class Game2Impl implements Game {

	private final Universe universe;

	private final List<Empire> empires;

	private final Technology technology;

	private final Shipyard shipyard;

	private final Navy navy;

	private final Intelligence intelligence;

	private final Colonization colonization;

	private Round round;

	private final PlayerTurns playerTurns;

	public Game2Impl(GalaxySize galaxySize, List<Empire> empires, List<Star> stars, List<Fleet> fleets) {
		this.universe = new Universe(galaxySize, stars);
		this.empires = new ArrayList<>(empires);
		this.technology = new Technology();
		this.shipyard = new Shipyard();
		this.navy = new Navy(fleets, this.technology);
		this.intelligence = new Intelligence();
		this.colonization = new Colonization();

		this.round = new Round(1);
		this.playerTurns = new PlayerTurns(this.empires.stream().map(Empire::player).collect(Collectors.toSet()));
	}

	@Override
	public PlayerGame forPlayer(Player player) {
		return new PlayerGame2Impl(player,
				this.empires.stream().filter(e -> e.player().equals(player)).findAny().orElseThrow().race());
	}

	@Override
	public Set<Player> players() {
		return this.empires.stream().map(Empire::player).collect(Collectors.toSet());
	}

	@Override
	public void registerAi(Player player) {
	}

	@Override
	public boolean aiControlled(Player player) {
		return false;
	}

	@Override
	public void unregisterAi(Player player) {
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
		// populationGrowth();
		// industryProduction();
		// ArrivedFleets arrivedFleets = moveFleetsAndAddNewShips();
		// arrivedFleets = resolveSpaceCombats(arrivedFleets);
		// colonizeSystems();
		// annexColonies();

		this.round = this.round.next();
		this.playerTurns.beginNewRound(this.round);
	}

	private class PlayerGame2Impl implements PlayerGame {

		private final Player player;

		private final Race race;

		private PlayerGame2Impl(Player player, Race race) {
			this.player = player;
			this.race = race;
		}

		@Override
		public GameView view() {
			Navy navy = Game2Impl.this.navy.apply(Game2Impl.this.round, List.of());

			return GameViewBuilder.builder()
				.galaxyWidth(LocationMapper.toLocationValue(Game2Impl.this.universe.width()))
				.galaxyHeight(LocationMapper.toLocationValue(Game2Impl.this.universe.height()))
				.player(this.player)
				.race(this.race)
				.players(Game2Impl.this.players())
				.round(Game2Impl.this.round())
				.turnFinishedStatus(Game2Impl.this.playerTurns.turnStatus())
				.systems(Game2Impl.this.universe.stars()
					.stream()
					.map(star -> Map.entry(SystemIdMapper.toSystemId(star.position()),
							SystemViewMapper.toSystemView(this.player, star, Game2Impl.this.universe.planet(star),
									Game2Impl.this.colonization.colony(star.position()), Game2Impl.this.intelligence,
									Game2Impl.this.technology)))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
				.fleets(navy.fleets()
					.stream()
					.map(fleet -> Map.entry(FleetIdMapper.toFleetId(fleet.location()),
							FleetViewMapper.toFleetView(this.player,
									Game2Impl.this.empires.stream()
										.filter(empire -> empire.player().equals(fleet.player()))
										.findFirst()
										.orElseThrow(),
									fleet, Game2Impl.this.universe.closest(fleet.location().current()),
									Game2Impl.this.technology, Game2Impl.this.shipyard)))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
				.colonizableSystemIds(Set.of()) // Shouldn't that be a property of a
												// system?
				.annexableSystemIds(Set.of()) // Shouldn't that be a property of a system
												// (or even colony)?
				.spaceCombats(Set.of())
				.justExploredSystem(Set.of()) // Shouldn't that be a property of a system?
				.selectTechGroups(Set.of())
				.systemNotifications(Set.of()) // Shouldn't that be a property of a
												// system?
				.colonizationCommandSystemsIds(Set.of()) // Shouldn't that be a property
															// of a
															// system?
				.annexationCommandSystemsIds(Set.of()) // Shouldn't that be a property of
														// a
														// system?
				.build();
		}

		@Override
		public Optional<Integer> calcEta(FleetId fleetId, SystemId destinationId, ShipsView ships) {
			Position origin = switch (FleetIdMapper.fromFleetId(fleetId)) {
				case OrbitingFleetId orbitingFleetId -> orbitingFleetId.system();
				case DeployedFleetId deployedFleetId ->
					Game2Impl.this.navy
						.findDispatched(this.player, deployedFleetId.origin(), deployedFleetId.destination(),
								deployedFleetId.dispatchment(), deployedFleetId.speed())
						.orElseThrow()
						.location()
						.current();
			};

			return Game2Impl.this.navy
				.calcEta(this.player, origin, SystemIdMapper.fromSystemId(destinationId), toShips(ships))
				.map(Rounds::quantity);
		}

		@Override
		public Optional<Integer> calcTranportColonistsEta(SystemId originId, SystemId destinationId) {
			return Game2Impl.this.navy
				.calcTranportColonistsEta(this.player, SystemIdMapper.fromSystemId(originId),
						SystemIdMapper.fromSystemId(destinationId))
				.map(Rounds::quantity);
		}

		@Override
		public Player player() {
			return this.player;
		}

		@Override
		public void nextShipType(ColonyId colonyId) {
			// colonization
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void transferColonists(ColonyId originId, ColonyId destinationId, int colonists) {
			// colonization + navy
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void relocateShips(ColonyId originId, ColonyId destinationId) {
			// colonization
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void colonizeSystem(SystemId systemId, FleetId fleetId, boolean skip) {
			// colonization
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void annexSystem(ColonyId colonyId, FleetId fleetId, boolean skip) {
			// colonization
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public void deployFleet(FleetId fleetId, SystemId destinationId, ShipsView ships) {
			DomainFleetId domainFleetId = FleetIdMapper.fromFleetId(fleetId);

			Deployment deployment = switch (domainFleetId) {
				case OrbitingFleetId(Position origin) ->
					new DeployOrbiting(this.player, origin, SystemIdMapper.fromSystemId(destinationId), toShips(ships));
				case DeployedFleetId(Position origin, Position destination, Round _, Speed speed) ->
					new DeployJustLeaving(this.player, origin, destination, speed,
							SystemIdMapper.fromSystemId(destinationId), toShips(ships));
			};

			Game2Impl.this.playerTurns.addOrder(this.player, deployment);
		}

		private static Ships toShips(ShipsView ships) {
			return new Ships(ships.ships()
				.entrySet()
				.stream()
				.map(e -> Map.entry(new ShipClassId(e.getKey().id().value()), e.getValue()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		}

		@Override
		public void selectTech(TechId techId) {
			// technology
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public TurnStatus finishTurn() {
			return Game2Impl.this.finishTurn(this.player);
		}

	}

}