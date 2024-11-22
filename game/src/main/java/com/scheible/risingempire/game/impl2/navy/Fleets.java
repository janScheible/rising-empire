package com.scheible.risingempire.game.impl2.navy;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;

/**
 * @author sj
 */
class Fleets {

	private final List<Fleet> fleets;

	private final ShipMovementSpecsProvider shipMovementSpecsProvider;

	Fleets(List<Fleet> fleets, ShipMovementSpecsProvider shipMovementSpecsProvider) {
		this.fleets = fleets;
		this.shipMovementSpecsProvider = shipMovementSpecsProvider;
	}

	void add(Fleet fleet) {
		this.fleets.add(fleet);
	}

	void remove(Fleet fleet) {
		this.fleets.remove(fleet);
	}

	Fleet get(int index) {
		return this.fleets.get(index);
	}

	void set(int index, Fleet fleet) {
		this.fleets.set(index, fleet);
	}

	int size() {
		return this.fleets.size();
	}

	List<Fleet> fleets() {
		return this.fleets;
	}

	ShipMovementSpecsProvider shipMovementSpecsProvider() {
		return this.shipMovementSpecsProvider;
	}

	Optional<Fleet> findOrbiting(Player player, Position system) {
		return this.fleets.stream()
			.filter(fleet -> player.equals(fleet.player())
					&& system.equals(fleet.location().asOrbit().map(Orbit::system).orElse(null)))
			.findFirst();
	}

	Optional<Fleet> findJustLeaving(Player player, Position origin, Position destination, Speed speed) {
		return this.fleets.stream()
			.filter(Predicate.not(Fleet::colonistTransport))
			.filter(fleet -> justLeavingMatch(fleet, player, origin, destination)
					&& effectiveSpeed(player, fleet.ships()).equals(speed))
			.findFirst();
	}

	Optional<Fleet> findJustLeavingTransporter(Player player, Position origin, Position destination) {
		return this.fleets.stream()
			.filter(Fleet::colonistTransport)
			.filter(fleet -> justLeavingMatch(fleet, player, origin, destination))
			.findFirst();
	}

	private boolean justLeavingMatch(Fleet fleet, Player player, Position origin, Position destination) {
		return player.equals(fleet.player())
				&& Boolean.TRUE.equals(fleet.location().asItinerary().map(Itinerary::justLeaving).orElse(Boolean.FALSE))
				&& origin.equals(fleet.location().asItinerary().map(Itinerary::origin).orElse(null))
				&& destination.equals(fleet.location().asItinerary().map(Itinerary::destination).orElse(null));
	}

	Optional<Fleet> findDispatched(Player player, Position origin, Position destination, Round dispatchment,
			Speed speed) {
		return this.fleets.stream()
			.filter(fleet -> fleet.player().equals(player)
					&& origin.equals(fleet.location().asItinerary().map(Itinerary::origin).orElse(null))
					&& destination.equals(fleet.location().asItinerary().map(Itinerary::destination).orElse(null))
					&& dispatchment.equals(fleet.location().asItinerary().map(Itinerary::dispatchment).orElse(null))
					&& speed.equals(fleet.location().asItinerary().map(Itinerary::speed).orElse(null)))
			.findFirst();
	}

	Speed effectiveSpeed(Player player, Ships ships) {
		return this.shipMovementSpecsProvider.effectiveSpeed(player, ships.counts().keySet());
	}

	Parsec effectiveRange(Player player, Ships ships) {
		return this.shipMovementSpecsProvider.effectiveRange(player, ships.counts().keySet());
	}

}
