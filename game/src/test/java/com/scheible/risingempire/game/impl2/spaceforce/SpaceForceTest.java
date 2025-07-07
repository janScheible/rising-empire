package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet;
import com.scheible.risingempire.game.impl2.spaceforce.combat.ResolvedSpaceCombat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class SpaceForceTest {

	private final Position conflictSystem = new Position(5.0, 5.0);

	@Test
	void testRetreatingFleets() {
		SpaceForce spaceForce = new SpaceForce(
				() -> Map.of(this.conflictSystem,
						List.of(new EncounteringFleet(Player.BLUE, this.conflictSystem, Ships.NONE, Optional.empty()),
								new EncounteringFleet(Player.YELLOW, this.conflictSystem, Ships.NONE, Optional.of(0.5)),
								new EncounteringFleet(Player.GREEN, this.conflictSystem, Ships.NONE,
										Optional.of(0.4)))),
				(_, _, _, _) -> new ResolvedSpaceCombat(Outcome.ATTACKER_RETREATED, 0, Map.of(), false, Map.of(),
						false));

		spaceForce.resolveSpaceCombats();
		assertThat(spaceForce.retreatingFleets()).containsOnly(
				new SpaceCombatFleet(Player.YELLOW, this.conflictSystem, Ships.NONE),
				new SpaceCombatFleet(Player.GREEN, this.conflictSystem, Ships.NONE));
	}

	@Test
	void testCompareEncounteringFleetsByArrivalRoundFraction() {
		List<EncounteringFleet> encounteringFleets = new ArrayList<>(
				List.of(new EncounteringFleet(Player.YELLOW, this.conflictSystem, Ships.NONE, Optional.of(0.5)),
						new EncounteringFleet(Player.BLUE, this.conflictSystem, Ships.NONE, Optional.empty()),
						new EncounteringFleet(Player.GREEN, this.conflictSystem, Ships.NONE, Optional.of(0.2))));

		assertThat(encounteringFleets.stream().sorted(SpaceForce::compareEncounteringFleetsByArrivalRoundFraction))
			.extracting(EncounteringFleet::arrivalRoundFraction)
			.containsExactly(Optional.empty(), Optional.of(0.2), Optional.of(0.5));

	}

}
