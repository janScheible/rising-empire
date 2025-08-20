package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public interface EncounteringFleetShipsProvider {

	Map<Position, List<EncounteringFleet>> encounteringFleetShips();

	record EncounteringFleet(Player player, Position system, Ships ships, Optional<Double> arrivalRoundFraction,
			Set<EncounteringFleetPart> partsBeforeArrival) {

		public boolean arriving() {
			return this.arrivalRoundFraction.isPresent();
		}

		public record EncounteringFleetPart(Position origin, Position current, Round dispatchment, Speed speed) {

		}

	}

}
