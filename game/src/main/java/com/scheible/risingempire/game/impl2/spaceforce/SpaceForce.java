package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
public class SpaceForce {

	private final EncounteringFleetShipsProvider encounteringFleetShipsProvider;

	private final List<RetreatingFleet> retreatingFleets = new ArrayList<>();

	public SpaceForce(EncounteringFleetShipsProvider encounteringFleetShipsProvider) {
		this.encounteringFleetShipsProvider = encounteringFleetShipsProvider;
	}

	public void resolveSpaceCombats() {
		this.retreatingFleets.clear();

		Map<Position, Map<Player, EncounteringFleet>> encounteringFleetShips = this.encounteringFleetShipsProvider
			.encounteringFleetShips();
		for (Position system : encounteringFleetShips.keySet()) {
			this.retreatingFleets.addAll(encounteringFleetShips.get(system)
				.entrySet()
				.stream()
				.filter(e -> e.getValue().arriving())
				.map(e -> new RetreatingFleet(e.getKey(), system))
				.toList());
		}
	}

	public boolean retreating(Player player, Position fleet) {
		return this.retreatingFleets.stream().anyMatch(rf -> rf.player().equals(player) && rf.position().equals(fleet));
	}

	public List<RetreatingFleet> retreatingFleets() {
		return unmodifiableList(this.retreatingFleets);
	}

}
