package com.scheible.risingempire.game.impl.fleet;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.system.SystemOrb;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author sj
 */
class FleetFormerTest {

	@Test
	void testDeploymentFromOrbitingWithNoDeployedYet() {
		// given
		FleetFinder fleetFinder = mock(FleetFinder.class);
		doReturn(Optional.empty()).when(fleetFinder).getJustLeavingFleets(any(), any(), any(), anyInt());
		doReturn(Optional.empty()).when(fleetFinder).getOrbitingFleet(any(), any());

		JourneyCalculator journeyCalculator = mock(JourneyCalculator.class);
		doReturn(40).when(journeyCalculator).calcFleetSpeed(any(), any());

		FleetFormer manager = new FleetFormer(createFleetIdGeneratorMock(), fleetFinder, journeyCalculator);

		SystemOrb firstSystem = createSystemOrbMock("first", new Location(20, 20));
		SystemOrb secondSystem = createSystemOrbMock("sys123", new Location(60, 60));

		// when
		FleetChanges changes = manager.deployFleet(Player.BLUE,
				new OrbitingFleet(FleetId.createRandom(), Player.BLUE, Map.of(DesignSlot.FIRST, 1), firstSystem, 1),
				firstSystem, secondSystem, Map.of(DesignSlot.FIRST, 1), 1);

		// then
		assertThat(new ArrayList<>(changes.getAdded()).get(0)).isInstanceOf(DeployedFleet.class);
		assertThat(new ArrayList<>(changes.getRemoved()).get(0)).isInstanceOf(OrbitingFleet.class);
	}

	private static SystemOrb createSystemOrbMock(String systemId, Location location) {
		SystemOrb systemOrbMock = mock(SystemOrb.class);
		doReturn(new SystemId(systemId)).when(systemOrbMock).getId();
		doReturn(location).when(systemOrbMock).getLocation();
		return systemOrbMock;
	}

	private static FleetIdGenerator createFleetIdGeneratorMock() {
		FleetIdGenerator fleetIdGenerator = mock(FleetIdGenerator.class);
		doAnswer(invocation -> FleetId.createRandom()).when(fleetIdGenerator).createRandom();
		return fleetIdGenerator;
	}

}
