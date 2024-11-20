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
		else if (new Position("9.973", "5.626").equals(system)) {
			return Optional.of(new Colony(new Empire(Player.YELLOW, Race.MYXALOR)));
		}
		else if (new Position("4.080", "8.226").equals(system)) {
			return Optional.of(new Colony(new Empire(Player.WHITE, Race.XELIPHARI)));
		}
		else {
			return Optional.empty();
		}
	}

}
