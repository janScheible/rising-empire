package com.scheible.risingempire.game.impl2.empire;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class Empires {

	private final Map<Player, Race> empires;

	private final Set<Player> players;

	public Empires(List<Empire> empires) {
		this.empires = empires.stream().collect(Collectors.toMap(Empire::player, Empire::race));
		this.players = unmodifiableSet(this.empires.keySet());
	}

	public Race race(Player player) {
		return this.empires.get(player);
	}

	public Set<Player> players() {
		return this.players;
	}

}
