package com.scheible.risingempire.game.impl2.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.colonization.ColonyFleetProvider;
import com.scheible.risingempire.game.impl2.intelligence.fleet.FleetItinearySegmentProvider;
import com.scheible.risingempire.game.impl2.intelligence.fleet.ScanAreasProvider;
import com.scheible.risingempire.game.impl2.intelligence.fleet.ShipScannerSpecsProvider;
import com.scheible.risingempire.game.impl2.intelligence.system.ColonyIntelProvider;
import com.scheible.risingempire.game.impl2.intelligence.system.OrbitingFleetsProvider;
import com.scheible.risingempire.game.impl2.military.ControlledSystemProvider;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Navy;
import com.scheible.risingempire.game.impl2.navy.NewShipsProvider;
import com.scheible.risingempire.game.impl2.navy.eta.ColoniesProvider;
import com.scheible.risingempire.game.impl2.navy.eta.ShipMovementSpecsProvider;
import com.scheible.risingempire.game.impl2.ship.BuildCapacityProvider;
import com.scheible.risingempire.game.impl2.ship.Shipyard;
import com.scheible.risingempire.game.impl2.spacecombat.EncounteringFleetShipsProvider;
import com.scheible.risingempire.game.impl2.technology.ResearchPointProvider;
import com.scheible.risingempire.game.impl2.technology.ShipScannerCapability;
import com.scheible.risingempire.game.impl2.technology.Technology;

/**
 * Contains all adapters use in the game implementation. Adapters provide a delegate
 * mechanism to break up interface cyclic dependencies. They have to be as simple as
 * possible. All more complex logic must reside in the sub-modules. Every adapter that
 * needs more than one sub-module must use a `Function<...>` that is implemented in the
 * game implementation.
 *
 * @author sj
 */
public final class Adapters {

	private Adapters() {
	}

	public static class ColoniesProviderAdapter implements ColoniesProvider {

		private Colonization delegate;

		public void delegate(Colonization delegate) {
			this.delegate = delegate;
		}

		@Override
		public Set<Position> colonies(Player player) {
			return this.delegate.colonies(player).stream().map(Colony::position).collect(Collectors.toSet());
		}

	}

	public static class ShipMovementSpecsProviderAdapter implements ShipMovementSpecsProvider {

		private Technology delegate;

		public void delegate(Technology delegate) {
			this.delegate = delegate;
		}

		@Override
		public Parsec range(Player player, ShipClassId ShipClassId) {
			return this.delegate.range(player, ShipClassId);
		}

		@Override
		public Parsec range(Player player) {
			return this.delegate.range(player);
		}

		@Override
		public Parsec extendedRange(Player player) {
			return this.delegate.extendedRange(player);
		}

		@Override
		public Speed speed(Player player, ShipClassId shipClassId) {
			return this.delegate.speed(player, shipClassId);
		}

	}

	public static class ColonyFleetProviderAdapter implements ColonyFleetProvider {

		private Function<Player, Set<Position>> delegate;

		public void delegate(Function<Player, Set<Position>> delegate) {
			this.delegate = delegate;
		}

		@Override
		public Set<Position> colonizableSystems(Player player) {
			return this.delegate.apply(player);
		}

	}

	public static class ControlledSystemProviderAdapter implements ControlledSystemProvider {

		private Function<Player, Set<Position>> delegate;

		public void delegate(Function<Player, Set<Position>> delegate) {
			this.delegate = delegate;
		}

		@Override
		public Set<Position> controlledSystems(Player player) {
			return this.delegate.apply(player);
		}

	}

	public static class BuildCapacityProviderAdapter implements BuildCapacityProvider {

		private Colonization delegate;

		public void delegate(Colonization delegate) {
			this.delegate = delegate;
		}

		@Override
		public Credit buildCapacity(Player player, Position system) {
			return this.delegate.buildCapacity(player, system);
		}

	}

	public static class ResearchPointProviderAdapter implements ResearchPointProvider {

		private Colonization delegate;

		public void delegate(Colonization delegate) {
			this.delegate = delegate;
		}

		@Override
		public ResearchPoint researchPoints(Player player) {
			return this.delegate.researchPoints(player);
		}

	}

	public static class NewShipsProviderAdapter implements NewShipsProvider {

		private Shipyard delegate;

		public void delegate(Shipyard delegate) {
			this.delegate = delegate;
		}

		@Override
		public Map<Position, Map<ShipClassId, Integer>> newShips(Player player) {
			return this.delegate.newShips(player);
		}

	}

	public static class OrbitingFleetsProviderAdapter implements OrbitingFleetsProvider {

		private Navy delegate;

		public void delegate(Navy delegate) {
			this.delegate = delegate;
		}

		@Override
		public Map<Player, Set<Position>> orbitingFleets() {
			return this.delegate.fleets()
				.stream()
				.filter(Fleet::orbiting)
				.collect(Collectors
					.groupingBy(Fleet::player, Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new),
							fleets -> fleets.stream().map(f -> f.location().current()).collect(Collectors.toSet()))));
		}

	}

	public static class ColonyIntelProviderAdapter implements ColonyIntelProvider {

		private Colonization delegate;

		public void delegate(Colonization delegate) {
			this.delegate = delegate;
		}

		@Override
		public Optional<ColonyIntel> colony(Position system) {
			return this.delegate.colony(system).map(c -> new ColonyIntel(c.player(), new Population(50.0)));
		}

	}

	public static class ScanAreasProviderAdapter implements ScanAreasProvider {

		private Function<Player, Set<ScanArea>> delegate;

		public void delegate(Function<Player, Set<ScanArea>> delegate) {
			this.delegate = delegate;
		}

		@Override
		public Set<ScanArea> scanAreas(Player player) {
			return this.delegate.apply(player);
		}

	}

	public static class ShipScannerSpecsProviderAdapter implements ShipScannerSpecsProvider {

		private Technology delegate;

		public void delegate(Technology delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean shipScannerRevealesItineary(Player player) {
			return this.delegate.shipScannerCapability(player) == ShipScannerCapability.LOCATION_AND_ITINERARY;
		}

	}

	public static class FleetItinearySegmentProviderAdapter implements FleetItinearySegmentProvider {

		private Navy delegate;

		public void delegate(Navy delegate) {
			this.delegate = delegate;
		}

		@Override
		public Optional<FleetItinerarySegment> fleetItinerarySegment(Player player, Position fleet) {
			return this.delegate.fleets()
				.stream()
				.filter(f -> f.player().equals(player) && f.location().current().equals(fleet) && f.deployed())
				.findFirst()
				.flatMap(f -> f.location().asItinerary())
				.map(i -> new FleetItinerarySegment(i.origin(), i.destination()));
		}

	}

	public static class EncounteringFleetShipsProviderAdapter implements EncounteringFleetShipsProvider {

		private Navy delegate;

		public void delegate(Navy delegate) {
			this.delegate = delegate;
		}

		@Override
		public Map<Position, Map<Player, EncounteringFleet>> encounteringFleetShips() {
			Map<Position, List<Fleet>> systemFleetMap = this.delegate.fleets()
				.stream()
				.filter(Fleet::orbiting)
				.collect(Collectors.groupingBy(f -> f.location().current()));

			return systemFleetMap.entrySet().stream().filter(e -> e.getValue().size() > 1).map(e -> {
				Map<Player, EncounteringFleet> encounteringFleets = e.getValue()
					.stream()
					.collect(Collectors.toMap(Fleet::player,
							f -> new EncounteringFleet(f.ships().counts(),
									f.location()
										.asOrbit()
										.orElseThrow()
										.arrivalRoundFractions()
										.flatMap(fs -> fs.stream().min(Double::compare)))));
				return Map.entry(e.getKey(), encounteringFleets);
			}).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}

	}

}
