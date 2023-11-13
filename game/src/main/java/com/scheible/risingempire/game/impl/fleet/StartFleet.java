package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.Map;

import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.System;

/**
 * @author sj
 */
public class StartFleet {

	private final Player player;

	private final System system;

	private final Map<DesignSlot, Integer> ships;

	public StartFleet(final Player player, final System system, final Map<DesignSlot, Integer> ships) {
		this.player = player;
		this.system = system;
		this.ships = Collections.unmodifiableMap(ships);
	}

	public Player getPlayer() {
		return player;
	}

	public System getSystem() {
		return system;
	}

	public Map<DesignSlot, Integer> getShips() {
		return ships;
	}

}
