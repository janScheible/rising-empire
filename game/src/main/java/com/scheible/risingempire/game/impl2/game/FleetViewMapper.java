package com.scheible.risingempire.game.impl2.game;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetBeforeArrivalView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.fleet.FleetView;
import com.scheible.risingempire.game.api.view.fleet.FleetView.HorizontalDirection;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.empire.Empire;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.ship.ShipDesign;
import com.scheible.risingempire.game.impl2.ship.Shipyard;
import com.scheible.risingempire.game.impl2.technology.ShipScanSpecsProvider;
import com.scheible.risingempire.game.impl2.universe.Star;

/**
 * @author sj
 */
class FleetViewMapper {

	static FleetView toFleetView(Player player, Empire fleetEmpire, Fleet fleet, Star closest,
			ShipScanSpecsProvider shipScanSpecsProvider, Shipyard shipyard) {
		// Simply always group by orbiting system or current fleet position. This is in
		// contrast to the inital orbiting fleet at begin of turn as in the original game.
		// Should have the same effect and makes the whole parent-child fleet tracking
		// unnecessary.
		Optional<FleetId> parentId = Optional.of(FleetIdMapper.toFleetId(fleet.location().current()));

		Map<ShipTypeView, Integer> ships = fleet.ships().counts().entrySet().stream().map(e -> {
			ShipDesign design = shipyard.design(fleetEmpire.player(), e.getKey());
			return Map.entry(new ShipTypeView(new ShipTypeId(e.getKey().value()), design.index(), design.name(),
					design.size(), design.look()), e.getValue());
		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return switch (fleet.location()) {
			case Orbit(Position system, Set<Itinerary> partsBeforArrival) ->
				FleetView.create(FleetView.orbitingBuilder()
					.id(FleetIdMapper.toFleetId(system))
					.parentId(parentId)
					.player(fleet.player())
					.race(fleetEmpire.race())
					.ships(new ShipsView(ships))
					.orbiting(SystemIdMapper.toSystemId(system))
					.location(LocationMapper.toLocation(system))
					.fleetsBeforeArrival(partsBeforArrival.stream()
						.map(pba -> new FleetBeforeArrivalView(FleetIdMapper.toFleetId(pba), horizontalDirection(pba),
								LocationMapper.toLocationValue(pba.speed().distance()),
								LocationMapper.toLocation(pba.current()), pba.justLeaving()))
						.collect(Collectors.toSet()))
					.deployable(player.equals(fleet.player()))
					.scannerRange(Optional.of(LocationMapper.toLocationValue(shipScanSpecsProvider
						.effectiveScanRange(fleetEmpire.player(), fleet.ships().counts().keySet()))))
					.build());
			case Itinerary itinerary -> FleetView.create(FleetView.deployedBuilder()
				.id(FleetIdMapper.toFleetId(itinerary.origin(), itinerary.destination(), itinerary.dispatchment(),
						itinerary.speed()))
				.parentId(parentId)
				.player(fleet.player())
				.race(fleetEmpire.race())
				.ships(new ShipsView(ships))
				.source(Optional.of(SystemIdMapper.toSystemId(itinerary.origin())))
				.destination(Optional.of(SystemIdMapper.toSystemId(itinerary.destination())))
				.previousLocation(LocationMapper.toLocation(itinerary.previous().orElse(itinerary.current())))
				.previousJustLeaving(itinerary.previousJustLeaving())
				.location(LocationMapper.toLocation(itinerary.current()))
				.speed(LocationMapper.toLocationValue(itinerary.speed().distance()))
				.closest(SystemIdMapper.toSystemId(closest.position()))
				.orientation(horizontalDirection(itinerary))
				.deployable(itinerary.justLeaving())
				.scannerRange(Optional.of(LocationMapper.toLocationValue(shipScanSpecsProvider
					.effectiveScanRange(fleetEmpire.player(), fleet.ships().counts().keySet()))))
				.justLeaving(itinerary.justLeaving())
				.build());
		};
	}

	private static HorizontalDirection horizontalDirection(Itinerary itinerary) {
		return itinerary.current().x().compareTo(itinerary.destination().x()) < 0 ? HorizontalDirection.LEFT
				: HorizontalDirection.RIGHT;
	}

}
