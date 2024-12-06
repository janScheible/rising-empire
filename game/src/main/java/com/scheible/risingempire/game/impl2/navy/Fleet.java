package com.scheible.risingempire.game.impl2.navy;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;

public record Fleet(Player player, Location location, Ships ships) {

	public static Fleet createOrbiting(Player player, Position system, Ships ships) {
		return new Fleet(player, new Orbit(system, Set.of()), ships);
	}

	public static Fleet createDeployed(Player player, Position origin, Position destination, Round dispatchment,
			Speed speed, Ships ships) {
		return new Fleet(player, new Itinerary(origin, destination, dispatchment, speed), ships);
	}

	public Fleet detach(Ships ships) {
		return new Fleet(this.player, this.location, this.ships.detach(ships));
	}

	public Fleet merge(Ships ships) {
		return new Fleet(this.player, this.location, this.ships.merge(ships));
	}

	public boolean colonistTransport() {
		return this.ships.transporters();
	}

	public boolean contains(ShipClassId shipClass) {
		return this.ships.contains(shipClass);
	}

	public sealed interface Location {

		Position current();

		default Optional<Orbit> asOrbit() {
			return this instanceof Orbit ? Optional.of((Orbit) this) : Optional.empty();
		}

		default Optional<Itinerary> asItinerary() {
			return this instanceof Itinerary ? Optional.of((Itinerary) this) : Optional.empty();
		}

		record Orbit(Position system, Set<Itinerary> partsBeforArrival) implements Location {

			/**
			 * Fresh orbiting location without any fleet parts before arrival.
			 */
			public Orbit(Position system) {
				this(system, Set.of());
			}

			@Override
			public Position current() {
				return this.system;
			}

		}

		record Itinerary(Position origin, Position destination, Optional<Position> previous, Position current,
				Round dispatchment, Speed speed) implements Location {

			/**
			 * Just leaving Itinerary location.
			 */
			public Itinerary(Position origin, Position destination, Round dispatchment, Speed speed) {
				this(origin, destination, Optional.empty(), origin, dispatchment, speed);
			}

			/**
			 * @param previous This is available as soon as the fleet left the orbit and
			 * has a position in space.
			 */
			@Override
			public Optional<Position> previous() {
				return this.previous;
			}

			public boolean justLeaving() {
				return this.current.equals(this.origin);
			}

			public boolean previousJustLeaving() {
				return this.previous.map(prev -> prev.equals(this.origin)).orElse(Boolean.FALSE);
			}
		}

	}

}
