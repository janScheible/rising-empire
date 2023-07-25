package com.scheible.risingempire.game.impl.ship;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;

/**
 * The six available slots for ship designs.
 * 
 * @author sj
 */
public enum DesignSlot {

	FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH;

	public static DesignSlot valueOf(final ShipTypeId shipTypeId) {
		return DesignSlot.values()[Integer.parseInt(shipTypeId.getValue().split("@")[1])];
	}

	public ShipTypeView toShipType(final ShipDesign design) {
		return new ShipTypeView(new ShipTypeId(design.getName() + "@" + ordinal()), ordinal(), design.getName(),
				design.getSize(), design.getLook());
	}

	public static Map<DesignSlot, Integer> toSlotAndCounts(final Set<Entry<ShipTypeId, Integer>> apiShips) {
		return apiShips.stream().filter(s -> s.getValue() > 0).collect(
				Collectors.toMap(typeWithCount -> DesignSlot.valueOf(typeWithCount.getKey()), Map.Entry::getValue));
	}
}