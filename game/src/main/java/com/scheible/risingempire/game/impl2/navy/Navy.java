package com.scheible.risingempire.game.impl2.navy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.common.Command;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.NewColoniesProvider.NewColony;

public class Navy {

	private final Fleets fleets;

	private final Dispatcher dispatcher;

	private final NewShipsProvider newShipsProvider;

	private final NewColoniesProvider newColoniesProvider;

	private final ColonyShipSpecsProvider colonyShipSpecsProvider;

	public Navy(List<Fleet> fleets, ShipSpeedSpecsProvider shipSpeedSpecsProvider, NewShipsProvider newShipsProvider,
			NewColoniesProvider newColoniesProvider, ColonyShipSpecsProvider colonyShipSpecsProvider) {
		this.fleets = new Fleets(new ArrayList<>(fleets), shipSpeedSpecsProvider);
		this.dispatcher = new Dispatcher(this.fleets);
		this.newShipsProvider = newShipsProvider;
		this.newColoniesProvider = newColoniesProvider;
		this.colonyShipSpecsProvider = colonyShipSpecsProvider;

	}

	private Navy(Fleets fleets, NewShipsProvider newShipsProvider, NewColoniesProvider newColoniesProvider,
			ColonyShipSpecsProvider colonyShipSpecsProvider) {
		this.fleets = new Fleets(new ArrayList<>(fleets.fleets()), fleets.shipSpeedSpecsProvider());
		this.dispatcher = new Dispatcher(this.fleets);
		this.newShipsProvider = newShipsProvider;
		this.newColoniesProvider = newColoniesProvider;
		this.colonyShipSpecsProvider = colonyShipSpecsProvider;
	}

	public Navy apply(Round round, List<Deploy> deployments) {
		Navy copy = new Navy(this.fleets, this.newShipsProvider, this.newColoniesProvider,
				this.colonyShipSpecsProvider);
		copy.dispatcher.dispatch(round, deployments);
		return copy;
	}

	public void moveFleets(Round round, List<Deploy> deployments) {
		this.dispatcher.dispatch(round, deployments);

		Map<Position, Fleet> destinationArrivedFleetMapping = new HashMap<>();
		Set<Fleet> obsoleteOrbitingFleets = new HashSet<>();

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
					Optional<Fleet> alreadyOrbiting = this.fleets.findOrbiting(fleet.player(), itinerary.destination());
					alreadyOrbiting.ifPresent(obsoleteOrbitingFleets::add);

					Set<Itinerary> partsBeforeArrival = new HashSet<>(Set.of(itinerary));
					partsBeforeArrival.addAll(alreadyOrbiting.map(Fleet::location)
						.flatMap(Location::asOrbit)
						.map(Orbit::partsBeforArrival)
						.orElse(Set.of()));

					Ships shipsInOrbit = alreadyOrbiting.map(Fleet::ships).orElse(Ships.NONE);

					Fleet newFleet = new Fleet(fleet.player(), new Orbit(itinerary.destination(), partsBeforeArrival),
							fleet.ships().merge(shipsInOrbit));
					this.fleets.set(i, newFleet);
					destinationArrivedFleetMapping.put(itinerary.destination(), newFleet);
				}
			}
			else if (!obsoleteOrbitingFleets.contains(fleet) && fleet.location() instanceof Orbit orbit) {
				// fleet arrived in the round before
				if (!orbit.partsBeforArrival().isEmpty()) {
					this.fleets.set(i, new Fleet(fleet.player(), new Orbit(orbit.system()), fleet.ships()));
				}
			}
		}

		obsoleteOrbitingFleets.forEach(this.fleets::remove);
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

	public void issueRelocations(List<RelocateShips> commands) {
	}

	public void removeDestroyedShips() {
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
			this.fleets.add(orbiting.detach(new Ships(Map.of(colonyShipClassId, 1))));
		}
	}

	public sealed interface Deploy extends Command {

		Player player();

		Position origin();

		Position destination();

	}

	public sealed interface ShipDeployment extends Deploy {

		Ships ships();

	}

	public record DeployOrbiting(Player player, Position origin, Position destination,
			Ships ships) implements ShipDeployment {

	}

	public record DeployJustLeaving(Player player, Position origin, Position previousDestination, Speed speed,
			Position newDestination, Ships ships) implements ShipDeployment {

		@Override
		public Position destination() {
			return this.newDestination;
		}

	}

	public record TransferColonists(Player player, Position origin, Position destination,
			int transporterCount) implements Deploy {

	}

	public record RelocateShips(Player player, Position origin, Position target) implements Command {

	}

}
