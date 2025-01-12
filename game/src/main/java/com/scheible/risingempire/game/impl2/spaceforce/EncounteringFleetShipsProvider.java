package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public interface EncounteringFleetShipsProvider {

	Map<Position, List<EncounteringFleet>> encounteringFleetShips();

	record EncounteringFleet(Player player, Map<ShipClassId, Integer> counts, Optional<Double> arrivalRoundFraction) {

		public boolean arriving() {
			return this.arrivalRoundFraction.isPresent();
		}

	}

}
