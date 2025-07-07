package com.scheible.risingempire.game.impl2.navy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.navy.DepartingColonistTransportsProvider.DepartingColonistTransport;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.NewColoniesProvider.NewColony;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NavyTest extends AbstractNavyTest {

	@Test
	void testMoveFleets() {
		Round round = new Round(1);
		Navy navy = createNavy(
				List.of(justLeavingFleet(this.origin, this.destination, round, ships(this.enterprise, 1)),
						justLeavingFleet(this.otherDestination, this.destination, round, ships(this.scout, 1))));
		boolean scoutArrived = false;
		do {
			navy.moveFleets(round, List.of());
			round = round.next();

			if (!scoutArrived && navy.fleets().stream().anyMatch(fleet -> fleet.orbiting())) {
				scoutArrived = true;

				assertThat(navy.fleets().stream().filter(Fleet::orbiting))
					.containsOnly(new Fleet(this.player,
							new Orbit(this.destination, Set.of(new Itinerary(this.otherDestination, this.destination,
									Optional.of(new Position("2.000", "2.000")), new Position("2.670", "0.659"),
									new Round(1), this.shipMovementSpecsProvider.speed(this.player, this.scout)))),
							ships(this.scout, 1)));
			}
		}
		while (navy.fleets().stream().anyMatch(Fleet::deployed));

		// this is the final fleet arrived at destination (the scout has no
		// `partsBeforeArrival` anymore because it arrived earlier)
		assertThat(navy.fleets()).containsOnly(new Fleet(this.player,
				new Orbit(this.destination,
						Set.of(new Itinerary(this.origin, this.destination, Optional.of(new Position("1.998", "0.000")),
								new Position("2.997", "0.000"), new Round(3),
								this.shipMovementSpecsProvider.speed(this.player, this.enterprise)))),
				ships(this.enterprise, 1, this.scout, 1)));
	}

	@Test
	void testTwoFleetsOfDifferentPlayersOrbitingSameSystem() {
		Round round = new Round(1);
		Navy navy = createNavy(
				List.of(justLeavingFleet(this.origin, this.destination, round, ships(this.enterprise, 1)),
						orbitingFleet(this.destination, ships(this.scout, 1), Player.YELLOW)));
		do {
			navy.moveFleets(round, List.of());
			round = round.next();
		}
		while (navy.fleets().stream().anyMatch(Fleet::deployed));

		// as an intermediate state it is okay to have two fleets of different players
		// orbiting the same system --> sapce combat will resolve that later
		List<Fleet> orbitingFleets = navy.fleets().stream().filter(Fleet::orbiting).toList();
		assertThat(orbitingFleets).hasSize(2);
		assertThat(orbitingFleets.stream().map(f -> f.location().current())).containsOnly(this.destination);
	}

	@Test
	void testCommissionNewShips() {
		Navy navy = createNavy(List.of(orbitingFleet(this.origin, ships(this.enterprise, 1))),
				(Player _) -> Map.of(this.origin, Map.of(this.enterprise, 1), //
						this.destination, Map.of(this.scout, 1)));

		navy.commissionNewShips();

		assertThat(navy.findOrbiting(this.player, this.origin).orElseThrow().ships())
			.isEqualTo(ships(this.enterprise, 2));
		assertThat(navy.findOrbiting(this.player, this.destination).orElseThrow().ships())
			.isEqualTo(ships(this.scout, 1));
	}

	@Test
	void testRemoveUsedColonyShips() {
		Navy navy = new Navy(List.of(orbitingFleet(this.origin, new Ships(Map.of(this.enterprise, 1, this.colony, 2)))),
				this.shipMovementSpecsProvider, (Player _) -> Map.of(),
				() -> Set.of(new NewColony(this.player, this.origin)), shipClassId -> this.colony.equals(shipClassId),
				() -> Set.of(), () -> Set.of());

		navy.removeUsedColonyShips();

		assertThat(navy.fleets())
			.containsOnly(orbitingFleet(this.origin, new Ships(Map.of(this.enterprise, 1, this.colony, 1))));
	}

	@Test
	void testSendColonistTransports() {
		Navy navy = new Navy(List.of(), this.shipMovementSpecsProvider, (Player _) -> Map.of(), () -> Set.of(),
				shipClassId -> false,
				() -> Set.of(new DepartingColonistTransport(this.player, this.origin, this.destination, 42)),
				() -> Set.of());

		Round round = new Round(1);
		navy.sendColonistTransports(round);
		assertThat(navy.fleets())
			.containsOnly(justLeavingFleet(this.origin, this.destination, round, Ships.transporters(42)));

		do {
			navy.moveFleets(round, List.of());
			round = round.next();
		}
		while (navy.fleets().stream().anyMatch(Fleet::deployed));

		assertThat(navy.fleets()).isEmpty();
		assertThat(navy.arrivedColonistTransports())
			.containsOnly(new ArrivedColonistTransport(this.player, this.destination, 42));
	}

	private Navy createNavy(List<Fleet> fleets) {
		return new Navy(fleets, this.shipMovementSpecsProvider, (Player _) -> Map.of(), () -> Set.of(),
				shipClassId -> false, () -> Set.of(), () -> Set.of());
	}

	private Navy createNavy(List<Fleet> fleets, NewShipsProvider newShipsProvider) {
		return new Navy(fleets, this.shipMovementSpecsProvider, newShipsProvider, () -> Set.of(), shipClassId -> false,
				() -> Set.of(), () -> Set.of());
	}

	private Fleet justLeavingFleet(Position origin, Position destination, Round dispatchment, Ships ships) {
		return new Fleet(this.player, new Itinerary(origin, destination, dispatchment,
				this.shipMovementSpecsProvider.effectiveSpeed(this.player, ships.counts().keySet())), ships);
	}

}
