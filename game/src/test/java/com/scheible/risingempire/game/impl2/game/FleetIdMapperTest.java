package com.scheible.risingempire.game.impl2.game;

import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.game.FleetIdMapper.DeployedFleetId;
import com.scheible.risingempire.game.impl2.game.FleetIdMapper.OrbitingFleetId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class FleetIdMapperTest {

	@Test
	void testOrbitingFleetId() {
		Position position = new Position("1.234", "5.678");

		assertThat(FleetIdMapper.fromFleetId(FleetIdMapper.toFleetId(position)))
			.isEqualTo(new OrbitingFleetId(position));
	}

	@Test
	void testDeployedFleetId() {
		Position origin = new Position("1.234", "5.678");
		Position destination = new Position("1.222", "3.444");
		Round departure = new Round(42);
		Speed speed = new Speed("9.134");

		assertThat(FleetIdMapper.fromFleetId(FleetIdMapper.toFleetId(origin, destination, departure, speed)))
			.isEqualTo(new DeployedFleetId(origin, destination, departure, speed));
	}

}
