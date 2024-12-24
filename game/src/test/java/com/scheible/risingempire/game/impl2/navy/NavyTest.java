package com.scheible.risingempire.game.impl2.navy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
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

			if (!scoutArrived && navy.fleets().stream().anyMatch(fleet -> fleet.location().asOrbit().isPresent())) {
				scoutArrived = true;

				assertThat(navy.fleets().stream().filter(f -> f.location().asOrbit().isPresent()))
					.containsOnly(new Fleet(this.player,
							new Orbit(this.destination, Set.of(new Itinerary(this.otherDestination, this.destination,
									Optional.of(new Position("2.000", "2.000")), new Position("2.670", "0.659"),
									new Round(1), this.shipMovementSpecsProvider.speed(this.player, this.scout)))),
							ships(this.scout, 1)));
			}
		}
		while (navy.fleets().stream().anyMatch(fleet -> fleet.location().asItinerary().isPresent()));

		// this is the final fleet arrived at destination (the scout has no
		// `partsBeforeArrival` anymore because it arrived earlier)
		assertThat(navy.fleets()).containsOnly(new Fleet(this.player,
				new Orbit(this.destination,
						Set.of(new Itinerary(this.origin, this.destination, Optional.of(new Position("1.998", "0.000")),
								new Position("2.997", "0.000"), new Round(3),
								this.shipMovementSpecsProvider.speed(this.player, this.enterprise)))),
				ships(this.enterprise, 1, this.scout, 1)));
	}

	private Navy createNavy(List<Fleet> fleets) {
		return new Navy(fleets, this.shipMovementSpecsProvider, this.newShipsProvider);
	}

	private Fleet justLeavingFleet(Position origin, Position destination, Round dispatchment, Ships ships) {
		return new Fleet(this.player, new Itinerary(origin, destination, dispatchment,
				this.shipMovementSpecsProvider.effectiveSpeed(this.player, ships.counts().keySet())), ships);
	}

}
