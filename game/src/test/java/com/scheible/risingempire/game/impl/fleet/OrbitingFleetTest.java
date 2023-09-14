package com.scheible.risingempire.game.impl.fleet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.System;

/**
 *
 * @author sj
 */
class OrbitingFleetTest {

	@Test
	void testDetachAllShips() {
		final System system = Mockito.mock(System.class);
		doReturn(new Location(42, 42)).when(system).getLocation();

		final OrbitingFleet fleet = new OrbitingFleet(FleetId.createRandom(), Player.BLUE, Map.of(DesignSlot.FIRST, 2),
				system, 1);
		fleet.detach(Map.of(DesignSlot.FIRST, 2));

		assertThat(fleet.hasShips()).isFalse();
	}
}
