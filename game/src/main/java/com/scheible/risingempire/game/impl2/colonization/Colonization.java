package com.scheible.risingempire.game.impl2.colonization;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.empire.Empire;

/**
 * @author sj
 */
public class Colonization {

	private final Set<Colony> colonies = Set.of(
			new Colony(new Empire(Player.BLUE, Race.LUMERISKS), new Position("6.173", "5.026")),
			new Colony(new Empire(Player.YELLOW, Race.MYXALOR), new Position("9.973", "5.626")),
			new Colony(new Empire(Player.WHITE, Race.XELIPHARI), new Position("4.080", "8.226")));

	public Set<Colony> colonies(Player player) {
		return this.colonies.stream().filter(c -> c.empire().player().equals(player)).collect(Collectors.toSet());
	}

	public Optional<Colony> colony(Position system) {
		return this.colonies.stream().filter(c -> c.position().equals(system)).findFirst();
	}

}
