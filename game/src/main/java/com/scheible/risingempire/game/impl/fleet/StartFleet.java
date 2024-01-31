package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Map;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.System;

/**
 * @author sj
 */
public class StartFleet {

	private final Player player;

	private final System system;

	private final Map<DesignSlot, Integer> ships;

	public StartFleet(Player player, System system, Map<DesignSlot, Integer> ships) {
		this.player = player;
		this.system = system;
		this.ships = Collections.unmodifiableMap(ships);
	}

	public Player getPlayer() {
		return this.player;
	}

	public System getSystem() {
		return this.system;
	}

	public Map<DesignSlot, Integer> getShips() {
		return this.ships;
	}

}
