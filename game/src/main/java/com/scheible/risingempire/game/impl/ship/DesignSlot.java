package com.scheible.risingempire.game.impl.ship;

import java.util.Map;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.ship.ShipsView;

/**
 * The six available slots for ship designs.
 *
 * @author sj
 */
public enum DesignSlot {

	FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH;

	public static DesignSlot valueOf(ShipTypeId shipTypeId) {
		return DesignSlot.values()[Integer.parseInt(shipTypeId.value().split("@")[1])];
	}

	public ShipTypeView toShipType(ShipDesign design) {
		return ShipTypeView.builder()
			.id(new ShipTypeId(design.getName() + "@" + ordinal()))
			.name(design.getName())
			.size(design.getSize())
			.look(design.getLook())
			.build();
	}

	public static Map<DesignSlot, Integer> toSlotAndCounts(ShipsView ships) {
		return ships.typesWithCount()
			.stream()
			.filter(s -> s.getValue() > 0)
			.collect(Collectors.toMap(typeWithCount -> DesignSlot.valueOf(typeWithCount.getKey().id()),
					Map.Entry::getValue));
	}

}
