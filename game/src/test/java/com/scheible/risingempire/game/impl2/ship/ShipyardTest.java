package com.scheible.risingempire.game.impl2.ship;

import java.util.ArrayList;
import java.util.List;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ShipyardTest {

	@Test
	public void testNextShipClass() {
		Shipyard shipyard = new Shipyard((Player _, Position _) -> new Credit(0));

		List<ShipClassId> shipClassIds = new ArrayList<>();
		ShipClassId first = new ShipClassId("scout");
		ShipClassId next = first;
		do {
			shipClassIds.add(next);
			next = shipyard.nextShipClass(next);
		}
		while (!next.equals(first));

		assertThat(shipClassIds).extracting(ShipClassId::value)
			.containsExactly("scout", "colony-ship", "fighter", "destroyer", "cruiser");
	}

}