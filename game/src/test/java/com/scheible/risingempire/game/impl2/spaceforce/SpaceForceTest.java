package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SpaceForceTest {

	private final Position conflictSystem = new Position(5.0, 5.0);

	private final ShipClassId shipClass = new ShipClassId("class");

	@Test
	void testRetreatingFleets() {
		SpaceForce spaceForce = new SpaceForce(() -> Map.of(this.conflictSystem,
				List.of(new EncounteringFleet(Player.BLUE, Map.of(this.shipClass, 1), Optional.empty()),
						new EncounteringFleet(Player.YELLOW, Map.of(this.shipClass, 1), Optional.of(0.5)),
						new EncounteringFleet(Player.GREEN, Map.of(this.shipClass, 1), Optional.of(0.4)))));

		spaceForce.resolveSpaceCombats();
		assertThat(spaceForce.retreatingFleets()).containsOnly(new RetreatingFleet(Player.YELLOW, this.conflictSystem),
				new RetreatingFleet(Player.GREEN, this.conflictSystem));
	}

	@Test
	void testCompareEncounteringFleetsByArrivalRoundFraction() {
		List<EncounteringFleet> encounteringFleets = new ArrayList<>(
				List.of(new EncounteringFleet(Player.YELLOW, Map.of(this.shipClass, 1), Optional.of(0.5)),
						new EncounteringFleet(Player.BLUE, Map.of(this.shipClass, 1), Optional.empty()),
						new EncounteringFleet(Player.GREEN, Map.of(this.shipClass, 1), Optional.of(0.2))));

		assertThat(encounteringFleets.stream().sorted(SpaceForce::compareEncounteringFleetsByArrivalRoundFraction))
			.extracting(EncounteringFleet::arrivalRoundFraction)
			.containsExactly(Optional.empty(), Optional.of(0.2), Optional.of(0.5));

	}

}
