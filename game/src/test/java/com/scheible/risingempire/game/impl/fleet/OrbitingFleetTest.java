package com.scheible.risingempire.game.impl.fleet;

import java.util.Map;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.System;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * @author sj
 */
class OrbitingFleetTest {

	@Test
	void testDetachAllShips() {
		System system = Mockito.mock(System.class);
		doReturn(new Location(42, 42)).when(system).getLocation();

		OrbitingFleet fleet = new OrbitingFleet(FleetId.createRandom(), Player.BLUE, Map.of(DesignSlot.FIRST, 2),
				system, 1);
		fleet.detach(Map.of(DesignSlot.FIRST, 2));

		assertThat(fleet.hasShips()).isFalse();
	}

}
