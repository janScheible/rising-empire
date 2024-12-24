package com.scheible.risingempire.game.impl2.ship;

import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;

/**
 * @author sj
 */
public class Shipyard {

	private final BuildCapacityProvider buildCapacityProvider;

	public Shipyard(BuildCapacityProvider buildCapacityProvider) {
		this.buildCapacityProvider = buildCapacityProvider;
		this.buildCapacityProvider.hashCode(); // to make PMD happy for now...
	}

	public ShipDesign design(Player player, ShipClassId shipClassId) {
		if (shipClassId.value().equals("enterprise")) {
			return new ShipDesign(0, "Enterprise", ShipSize.HUGE, 0);
		}
		else {
			return new ShipDesign(0, "Scout", ShipSize.SMALL, 0);
		}
	}

	public boolean colonizable(Player player, Set<ShipClassId> shipClassIds, PlanetType planetType) {
		return false;
	}

	public void buildShips() {
	}

	public Map<Position, Map<ShipClassId, Integer>> newShips(Player player) {
		return Map.of();
	}

}
