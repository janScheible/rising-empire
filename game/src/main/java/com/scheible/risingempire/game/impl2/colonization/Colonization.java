package com.scheible.risingempire.game.impl2.colonization;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.empire.Empire;

/**
 * @author sj
 */
public class Colonization {

	public Optional<Colony> colony(Position system) {
		if (new Position("6.173", "5.026").equals(system)) {
			return Optional.of(new Colony(new Empire(Player.BLUE, Race.LUMERISKS)));
		}
		else {
			return Optional.empty();
		}
	}

}
