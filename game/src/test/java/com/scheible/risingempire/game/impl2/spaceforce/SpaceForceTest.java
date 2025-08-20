package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet.EncounteringFleetPart;
import com.scheible.risingempire.game.impl2.spaceforce.combat.FireExchange;
import com.scheible.risingempire.game.impl2.spaceforce.combat.ResolvedSpaceCombat;
import com.scheible.risingempire.game.impl2.spaceforce.combat.ShipCombatSpecs;
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
				() -> Map.of(this.conflictSystem, List.of(
						new EncounteringFleet(Player.BLUE, this.conflictSystem, Ships.NONE, Optional.empty(),
								Set.of(new EncounteringFleetPart(new Position(5.0, 0.0), new Position(5.0, 2.0),
										new Round(42), new Speed(new Parsec(3.0))))),
						new EncounteringFleet(Player.YELLOW, this.conflictSystem, Ships.NONE, Optional.of(0.5),
								Set.of(new EncounteringFleetPart(new Position(10.0, 5.0), new Position(7.0, 5.0),
										new Round(42), new Speed(new Parsec(3.0))))),
						new EncounteringFleet(Player.GREEN, this.conflictSystem, Ships.NONE, Optional.of(0.4),
								Set.of(new EncounteringFleetPart(new Position(0.0, 5.0), new Position(2.0, 5.0),
										new Round(42), new Speed(new Parsec(3.0))))))),
				(_, _, _, _) -> new DummyResolvedSpaceCombat(Outcome.ATTACKER_RETREATED, 0, Map.of(), false, Map.of(),
						false),
				Optional.empty());

		spaceForce.resolveSpaceCombats();
		assertThat(spaceForce.retreatingFleets()).containsOnly(
				new SpaceCombatFleet(Player.YELLOW, this.conflictSystem, Ships.NONE),
				new SpaceCombatFleet(Player.GREEN, this.conflictSystem, Ships.NONE));
	}

	@Test
	void testCompareEncounteringFleetsByArrivalRoundFraction() {
		List<EncounteringFleet> encounteringFleets = new ArrayList<>(List.of(
				new EncounteringFleet(Player.YELLOW, this.conflictSystem, Ships.NONE, Optional.of(0.5), Set.of()),
				new EncounteringFleet(Player.BLUE, this.conflictSystem, Ships.NONE, Optional.empty(), Set.of()),
				new EncounteringFleet(Player.GREEN, this.conflictSystem, Ships.NONE, Optional.of(0.2), Set.of())));

		assertThat(encounteringFleets.stream().sorted(SpaceForce::compareEncounteringFleetsByArrivalRoundFraction))
			.extracting(EncounteringFleet::arrivalRoundFraction)
			.containsExactly(Optional.empty(), Optional.of(0.2), Optional.of(0.5));

	}

	private record DummyResolvedSpaceCombat(Outcome outcome, int fireExchangeCount,
			Map<ShipCombatSpecs, List<FireExchange>> attackerFireExchanges, boolean attackerShipSpecsAvailable,
			Map<ShipCombatSpecs, List<FireExchange>> defenderFireExchanges,
			boolean defenderShipSpecsAvailable) implements ResolvedSpaceCombat {

		@Override
		public Ships attackerShips(Ships previousAttackerShips) {
			return previousAttackerShips;
		}

		@Override
		public Ships defenderShips(Ships previousDefenderShips) {
			return previousDefenderShips;
		}

	}

}
