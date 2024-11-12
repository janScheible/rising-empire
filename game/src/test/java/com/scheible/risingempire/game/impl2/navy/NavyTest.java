package com.scheible.risingempire.game.impl2.navy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Itinerary;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NavyTest extends AbstractNavyTest {

	@Test
	void testMoveFleetsAndAddNewShips() {
		Round round = new Round(1);
		Navy navy = createNavy(
				List.of(justLeavingFleet(this.origin, this.destination, round, ships(this.enterprise, 1)),
						justLeavingFleet(this.otherDestination, this.destination, round, ships(this.scout, 1))));
		ArrivedFleets arrivedFleets = navy.moveFleetsAndAddNewShips(round, List.of(), List.of());

		assertThat(arrivedFleets.fleets()).isEmpty();
		assertThat(navy.fleets()).extracting(Fleet::location).hasSize(2).allSatisfy(position -> {
			assertThat(position.asItinerary().map(Itinerary::destination).orElse(null)).isEqualTo(this.destination);
		});

		// move until the fleets arrive at their destinations
		while (navy.fleets().stream().anyMatch(fleet -> fleet.location() instanceof Itinerary)) {
			round = round.next();
			arrivedFleets = navy.moveFleetsAndAddNewShips(round, List.of(), List.of());
		}

		Fleet arrivedFleet = new Fleet(this.player,
				new Orbit(this.destination,
						Set.of(new Itinerary(this.origin, this.destination, Optional.of(new Position("1.998", "0.000")),
								new Position("2.997", "0.000"), new Round(3),
								this.shipSpecsProvider.speed(this.player, this.enterprise)),
								new Itinerary(this.otherDestination, this.destination,
										Optional.of(new Position("2.000", "2.000")), new Position("0.670", "-1.341"),
										new Round(1), this.shipSpecsProvider.speed(this.player, this.scout)))),
				ships(this.enterprise, 1, this.scout, 1));
		assertThat(arrivedFleets.fleets()).containsOnly(arrivedFleet);
		assertThat(navy.fleets()).containsOnly(arrivedFleet);
	}

	@Test
	void testEta() {
		Navy navy = createNavy(List.of());

		Optional<Rounds> eta = navy.calcEta(this.player, this.origin, this.destination, ships(this.scout, 1));

		assertThat(eta).contains(new Rounds(2));
	}

	private Navy createNavy(List<Fleet> fleets) {
		return new Navy(fleets, this.shipSpecsProvider);
	}

	private Fleet justLeavingFleet(Position origin, Position destination, Round dispatchment, Ships ships) {
		return new Fleet(this.player, new Itinerary(origin, destination, dispatchment,
				this.shipSpecsProvider.effectiveSpeed(this.player, ships.counts().keySet())), ships);
	}

}
