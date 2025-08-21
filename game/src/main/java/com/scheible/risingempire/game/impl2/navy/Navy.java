package com.scheible.risingempire.game.impl2.navy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.common.Command;
import com.scheible.risingempire.game.impl2.navy.DepartingColonistTransportsProvider.DepartingColonistTransport;
import com.scheible.risingempire.game.impl2.navy.DestroyedShipsProvider.DestroyedShips;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.NewColoniesProvider.NewColony;

import static java.util.Collections.unmodifiableSet;

public class Navy {

	private final Fleets fleets;

	private final Dispatcher dispatcher;

	private final NewShipsProvider newShipsProvider;

	private final NewColoniesProvider newColoniesProvider;

	private final ColonyShipSpecsProvider colonyShipSpecsProvider;

	private final DepartingColonistTransportsProvider departingColonistTransportsProvider;

	private final DestroyedShipsProvider destroyedShipsProvider;

	private final Set<ArrivedColonistTransport> arrivedColonistTransports = new HashSet<>();

	public Navy(List<Fleet> fleets, ShipSpeedSpecsProvider shipSpeedSpecsProvider, NewShipsProvider newShipsProvider,
			NewColoniesProvider newColoniesProvider, ColonyShipSpecsProvider colonyShipSpecsProvider,
			DepartingColonistTransportsProvider departingColonistTransportsProvider,
			DestroyedShipsProvider destroyedShipsProvider) {
		this.fleets = new Fleets(new ArrayList<>(fleets), shipSpeedSpecsProvider);
		this.dispatcher = new Dispatcher(this.fleets);
		this.newShipsProvider = newShipsProvider;
		this.newColoniesProvider = newColoniesProvider;
		this.colonyShipSpecsProvider = colonyShipSpecsProvider;
		this.departingColonistTransportsProvider = departingColonistTransportsProvider;
		this.destroyedShipsProvider = destroyedShipsProvider;

	}

	private Navy(Fleets fleets, NewShipsProvider newShipsProvider, NewColoniesProvider newColoniesProvider,
			ColonyShipSpecsProvider colonyShipSpecsProvider,
			DepartingColonistTransportsProvider departingColonistTransportsProvider,
			DestroyedShipsProvider destroyedShipsProvider) {
		this.fleets = new Fleets(new ArrayList<>(fleets.fleets()), fleets.shipSpeedSpecsProvider());
		this.dispatcher = new Dispatcher(this.fleets);
		this.newShipsProvider = newShipsProvider;
		this.newColoniesProvider = newColoniesProvider;
		this.colonyShipSpecsProvider = colonyShipSpecsProvider;
		this.departingColonistTransportsProvider = departingColonistTransportsProvider;
		this.destroyedShipsProvider = destroyedShipsProvider;
	}

	public Navy apply(Round round, List<ShipDeployment> deployments) {
		Navy copy = new Navy(this.fleets, this.newShipsProvider, this.newColoniesProvider, this.colonyShipSpecsProvider,
				this.departingColonistTransportsProvider, this.destroyedShipsProvider);
		copy.dispatcher.dispatch(round, deployments);
		return copy;
	}

	public void dispatch(Round round, List<ShipDeployment> deployments) {
		this.dispatcher.dispatch(round, deployments);
	}

	public void moveFleets(Round round, List<ShipDeployment> deployments) {
		this.arrivedColonistTransports.clear();

		this.dispatcher.dispatch(round, deployments);

		Set<Fleet> obsoleteFleets = new HashSet<>();

		for (int i = 0; i < this.fleets.size(); i++) {
			Fleet fleet = this.fleets.get(i);

			if (fleet.location() instanceof Itinerary itinerary) {
				Position origin = itinerary.origin();
				Position destination = itinerary.destination();

				Position previous = itinerary.current();
				Position current = Position.interpolate(origin, destination, itinerary.current(),
						this.fleets.effectiveSpeed(fleet.player(), fleet.ships()).distance());
				boolean arrived = current.equals(destination);

				if (!arrived) {
					Fleet newFleet = new Fleet(fleet.player(),
							new Itinerary(itinerary.origin(), itinerary.destination(), Optional.of(previous), current,
									round, this.fleets.effectiveSpeed(fleet.player(), fleet.ships())),
							fleet.ships());
					this.fleets.set(i, newFleet);
				}
				else {
					if (fleet.colonistTransport()) {
						// if colonist transporter arrives just remove it and remember
						// that it arrived
						obsoleteFleets.add(fleet);
						this.arrivedColonistTransports
							.add(new ArrivedColonistTransport(fleet.player(), itinerary.destination(),
									fleet.ships().counts().get(ShipClassId.COLONISTS_TRANSPORTER)));
					}
					else {
						Optional<Fleet> alreadyOrbiting = this.fleets
							.findAllOrbiting(fleet.player(), itinerary.destination())
							.stream()
							.filter(Predicate.not(obsoleteFleets::contains))
							.findFirst();
						alreadyOrbiting.ifPresent(obsoleteFleets::add);

						Set<Itinerary> partsBeforeArrival = new HashSet<>(Set.of(itinerary));
						partsBeforeArrival.addAll(alreadyOrbiting.map(Fleet::location)
							.flatMap(Location::asOrbit)
							.map(Orbit::partsBeforArrival)
							.orElse(Set.of()));

						Ships shipsInOrbit = alreadyOrbiting.map(Fleet::ships).orElse(Ships.NONE);

						Fleet newFleet = new Fleet(fleet.player(),
								new Orbit(itinerary.destination(), partsBeforeArrival),
								fleet.ships().merge(shipsInOrbit));
						this.fleets.set(i, newFleet);
					}
				}
			}
			else if (!obsoleteFleets.contains(fleet) && fleet.location() instanceof Orbit orbit) {
				// fleet arrived in the round before
				if (!orbit.partsBeforArrival().isEmpty()) {
					this.fleets.set(i, new Fleet(fleet.player(), new Orbit(orbit.system()), fleet.ships()));
				}
			}
		}

		obsoleteFleets.forEach(this.fleets::remove);
	}

	public Optional<Fleet> findOrbiting(Player player, Position system) {
		return this.fleets.findOrbiting(player, system);
	}

	public Optional<Fleet> findDispatched(Player player, Position origin, Position destination, Round dispatchment,
			Speed speed) {
		return this.fleets.findDispatched(player, origin, destination, dispatchment, speed);
	}

	public List<Fleet> fleets() {
		return Collections.unmodifiableList(this.fleets.fleets());
	}

	public void commissionNewShips() {
		for (Player player : Player.values()) {
			Map<Position, Map<ShipClassId, Integer>> newShips = this.newShipsProvider.newShips(player);

			for (Position system : newShips.keySet()) {
				Optional<Fleet> alreadyOrbitingFleet = this.fleets.findOrbiting(player, system);
				if (alreadyOrbitingFleet.isPresent()) {
					int fleetIndex = this.fleets.indexOf(alreadyOrbitingFleet.get());
					this.fleets.set(fleetIndex, new Fleet(player, new Orbit(system),
							alreadyOrbitingFleet.get().ships().merge(new Ships(newShips.get(system)))));
				}
				else {
					this.fleets.add(new Fleet(player, new Orbit(system), new Ships(newShips.get(system))));
				}
			}
		}
	}

	public void sendColonistTransports(Round round) {
		for (DepartingColonistTransport colonistTransport : this.departingColonistTransportsProvider
			.colonistTransports()) {
			Ships ships = Ships.transporters(colonistTransport.transporters());

			Fleet deployed = Fleet.createDeployed(colonistTransport.player(), colonistTransport.origin(),
					colonistTransport.destination(), round,
					this.fleets.effectiveSpeed(colonistTransport.player(), ships), ships);

			this.fleets.add(deployed);
		}
	}

	public Set<ArrivedColonistTransport> arrivedColonistTransports() {
		return unmodifiableSet(this.arrivedColonistTransports);
	}

	public void issueRelocations(List<RelocateShips> commands) {
	}

	public void removeDestroyedShips() {
		for (DestroyedShips destroyedShips : this.destroyedShipsProvider.destroyedShips()) {
			Fleet orbiting = this.fleets.findOrbiting(destroyedShips.player(), destroyedShips.system()).orElseThrow();

			this.fleets.remove(orbiting);
			if (!destroyedShips.ships().empty()) {
				this.fleets.add(new Fleet(orbiting.player(), orbiting.location(), destroyedShips.ships()));
			}
		}
	}

	public void removeUsedColonyShips() {
		for (NewColony newColony : this.newColoniesProvider.newColonies()) {
			Fleet orbiting = this.fleets.findOrbiting(newColony.player(), newColony.system()).orElseThrow();

			ShipClassId colonyShipClassId = orbiting.ships()
				.counts()
				.keySet()
				.stream()
				.filter(this.colonyShipSpecsProvider::colonyShip)
				.findFirst()
				.orElseThrow();

			this.fleets.remove(orbiting);

			Fleet withoutColonyShip = orbiting.detach(new Ships(Map.of(colonyShipClassId, 1)));
			if (!withoutColonyShip.ships().empty()) {
				this.fleets.add(withoutColonyShip);
			}
		}
	}

	public sealed interface ShipDeployment extends Command {

		Player player();

		Position origin();

		Position destination();

		Ships ships();

	}

	public record DeployOrbiting(Player player, Position origin, Position destination, Ships ships,
			boolean retreating) implements ShipDeployment {

		public DeployOrbiting(Player player, Position origin, Position destination, Ships ships) {
			this(player, origin, destination, ships, false);
		}

	}

	public record DeployJustLeaving(Player player, Position origin, Position previousDestination, Speed speed,
			Position newDestination, Ships ships) implements ShipDeployment {

		@Override
		public Position destination() {
			return this.newDestination;
		}

	}

	public record RelocateShips(Player player, Position origin, Position target) implements Command {

	}

}
