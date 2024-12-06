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
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.common.Command;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;

public class Navy {

	private final Fleets fleets;

	private final Dispatcher dispatcher;

	public Navy(List<Fleet> fleets, ShipSpeedSpecsProvider shipSpeedSpecsProvider) {
		this.fleets = new Fleets(new ArrayList<>(fleets), shipSpeedSpecsProvider);
		this.dispatcher = new Dispatcher(this.fleets);
	}

	private Navy(Fleets fleets) {
		this.fleets = new Fleets(new ArrayList<>(fleets.fleets()), fleets.shipSpeedSpecsProvider());
		this.dispatcher = new Dispatcher(this.fleets);
	}

	public Navy apply(Round round, List<Deployment> deployments) {
		Navy copy = new Navy(this.fleets);
		copy.dispatcher.dispatch(round, deployments);
		return copy;
	}

	public ArrivedFleets finishRound(Round round, List<Deployment> deployments, List<NewShips> newShips) {
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

		return new ArrivedFleets(new ArrayList<>(destinationArrivedFleetMapping.values()));
	}

	public Optional<Fleet> findDispatched(Player player, Position origin, Position destination, Round dispatchment,
			Speed speed) {
		return this.fleets.findDispatched(player, origin, destination, dispatchment, speed);
	}

	public void removeDestroyedShips(List<DestroyedShips> destroyedShips) {

	}

	public List<Fleet> fleets() {
		return Collections.unmodifiableList(this.fleets.fleets());
	}

	public sealed interface Deployment extends Command {

		Player player();

		Position origin();

		Position destination();

	}

	public sealed interface ShipDeployment extends Deployment {

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
			int transporterCount) implements Deployment {

	}

	public record NewShips(Player player, Position target, Ships ships) {

	}

	public record DestroyedShips(Player player, Position target, Ships ships) {

	}

}
