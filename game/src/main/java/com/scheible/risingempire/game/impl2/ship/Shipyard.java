package com.scheible.risingempire.game.impl2.ship;

import java.util.List;
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

	private static final List<ShipClassId> SHIP_CLASS_IDS = List.of(new ShipClassId("scout"),
			new ShipClassId("colony-ship"), new ShipClassId("fighter"), new ShipClassId("destroyer"),
			new ShipClassId("cruiser"));

	private final BuildCapacityProvider buildCapacityProvider;

	public Shipyard(BuildCapacityProvider buildCapacityProvider) {
		this.buildCapacityProvider = buildCapacityProvider;
		this.buildCapacityProvider.hashCode(); // to make PMD happy for now...
	}

	public ShipDesign design(Player player, ShipClassId shipClassId) {
		if (shipClassId.value().equals("scout")) {
			return new ShipDesign(new ShipClassId("scout"), 0, "Scout", ShipSize.SMALL, 0);
		}
		else if (shipClassId.value().equals("colony-ship")) {
			return new ShipDesign(new ShipClassId("colony-ship"), 0, "Colony Ship", ShipSize.LARGE, 0);
		}
		else if (shipClassId.value().equals("fighter")) {
			return new ShipDesign(new ShipClassId("fighter"), 0, "Fighter", ShipSize.SMALL, 0);
		}
		else if (shipClassId.value().equals("destroyer")) {
			return new ShipDesign(new ShipClassId("destroyer"), 0, "Destroyer", ShipSize.MEDIUM, 0);
		}
		else if (shipClassId.value().equals("cruiser")) {
			return new ShipDesign(new ShipClassId("cruiser"), 0, "Cruiser", ShipSize.LARGE, 0);
		}
		else {
			throw new IllegalArgumentException("Unknown ship class '" + shipClassId + "'!");
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

	public ShipClassId nextShipClass(ShipClassId shipClass) {
		int i = SHIP_CLASS_IDS.indexOf(shipClass);

		if (i + 1 < SHIP_CLASS_IDS.size()) {
			return SHIP_CLASS_IDS.get(i + 1);
		}
		else {
			return SHIP_CLASS_IDS.get(0);
		}

	}

}
