package com.scheible.risingempire.game.impl2.view;

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
import com.scheible.risingempire.game.impl2.empire.Empires;
import com.scheible.risingempire.game.impl2.intelligence.fleet.FleetIntelligence;
import com.scheible.risingempire.game.impl2.intelligence.fleet.FleetReconReport;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.ship.ShipDesign;
import com.scheible.risingempire.game.impl2.ship.Shipyard;
import com.scheible.risingempire.game.impl2.spaceforce.SpaceForce;
import com.scheible.risingempire.game.impl2.technology.Technology;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.Universe;

/**
 * @author sj
 */
public class FleetViewMapper {

	public static Optional<FleetView> toFleetView(Player player, Fleet fleet, Universe universe, Technology technology,
			Shipyard shipyard, FleetIntelligence fleetIntelligence, SpaceForce spaceForce, Empires empires) {
		// Simply always group by orbiting system or current fleet position. This is in
		// contrast to the inital orbiting fleet at begin of turn as in the original game.
		// Should have the same effect and makes the whole parent-child fleet tracking
		// unnecessary.
		Optional<FleetId> parentId = Optional.of(FleetIdMapper.toFleetId(fleet.location().current()));

		Star closestStar = universe.closest(fleet.location().current(), (Star _) -> true);

		FleetReconReport reconReport = fleetIntelligence.fleetReconReport(player, fleet.location().current());

		if (fleet.player() != player && !reconReport.scanned()) {
			return Optional.empty();
		}

		Map<ShipTypeView, Integer> ships = fleet.ships().counts().entrySet().stream().map(e -> {
			ShipDesign design = shipyard.design(fleet.player(), e.getKey());
			return Map.entry(new ShipTypeView(new ShipTypeId(e.getKey().value()), design.index(), design.name(),
					design.size(), design.look()), e.getValue());
		}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		return Optional.of(switch (fleet.location()) {
			case Orbit(Position system, Set<Itinerary> partsBeforArrival) -> FleetView.create(
					FleetView.orbitingBuilder()
						.id(FleetIdMapper.toFleetId(system))
						.parentId(parentId)
						.player(fleet.player())
						.race(empires.race(fleet.player()))
						.ships(new ShipsView(ships))
						.orbiting(SystemIdMapper.toSystemId(system))
						.location(LocationMapper.toLocation(system))
						.fleetsBeforeArrival(partsBeforArrival.stream()
							.map(pba -> new FleetBeforeArrivalView(FleetIdMapper.toFleetId(pba),
									horizontalDirection(pba), LocationMapper.toLocationValue(pba.speed().distance()),
									LocationMapper.toLocation(pba.current()), pba.justLeaving()))
							.collect(Collectors.toSet()))
						.deployable(player.equals(fleet.player()))
						.scannerRange(Optional.of(LocationMapper.toLocationValue(
								technology.effectiveScanRange(fleet.player(), fleet.ships().counts().keySet()))))
						.build());
			case Itinerary itinerary -> FleetView.create(FleetView.deployedBuilder()
				.id(FleetIdMapper.toFleetId(itinerary.origin(), itinerary.destination(), itinerary.dispatchment(),
						itinerary.speed()))
				.parentId(parentId)
				.player(fleet.player())
				.race(empires.race(fleet.player()))
				.ships(new ShipsView(ships))
				.source(Optional
					.ofNullable(reconReport.itineraryRevealed() ? SystemIdMapper.toSystemId(itinerary.origin()) : null))
				.destination(Optional.ofNullable(
						reconReport.itineraryRevealed() ? SystemIdMapper.toSystemId(itinerary.destination()) : null))
				.previousLocation(LocationMapper.toLocation(itinerary.previous().orElse(itinerary.current())))
				.previousJustLeaving(itinerary.previousJustLeaving())
				.location(LocationMapper.toLocation(itinerary.current()))
				.speed(LocationMapper.toLocationValue(itinerary.speed().distance()))
				.closest(SystemIdMapper.toSystemId(closestStar.position()))
				.orientation(horizontalDirection(itinerary))
				.deployable(player.equals(fleet.player()) && itinerary.justLeaving()
						&& !spaceForce.retreating(fleet.player(), fleet.location().current()))
				.scannerRange(Optional.of(LocationMapper
					.toLocationValue(technology.effectiveScanRange(fleet.player(), fleet.ships().counts().keySet()))))
				.justLeaving(itinerary.justLeaving())
				.build());
		});
	}

	private static HorizontalDirection horizontalDirection(Itinerary itinerary) {
		return itinerary.current().x().compareTo(itinerary.destination().x()) > 0 ? HorizontalDirection.LEFT
				: HorizontalDirection.RIGHT;
	}

}
