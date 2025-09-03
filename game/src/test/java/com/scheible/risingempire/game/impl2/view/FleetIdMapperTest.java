package com.scheible.risingempire.game.impl2.view;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.view.FleetIdMapper.DeployedFleetId;
import com.scheible.risingempire.game.impl2.view.FleetIdMapper.OrbitingFleetId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class FleetIdMapperTest {

	@Test
	void testOrbitingFleetId() {
		Player player = Player.BLUE;
		Position position = new Position("1.234", "5.678");

		assertThat(FleetIdMapper.fromFleetId(FleetIdMapper.toFleetId(player, position, false)))
			.isEqualTo(new OrbitingFleetId(position));
		assertThat(FleetIdMapper.fromFleetId(FleetIdMapper.toFleetId(player, position, true)))
			.isEqualTo(new OrbitingFleetId(position));
	}

	@Test
	void testDeployedFleetId() {
		Player player = Player.BLUE;
		Position origin = new Position("1.234", "5.678");
		Position destination = new Position("1.222", "3.444");
		Round departure = new Round(42);
		Speed speed = new Speed("9.134");

		assertThat(FleetIdMapper
			.fromFleetId(FleetIdMapper.toFleetId(player, origin, destination, departure, speed, false)))
			.isEqualTo(new DeployedFleetId(origin, destination, departure, speed));
		assertThat(
				FleetIdMapper.fromFleetId(FleetIdMapper.toFleetId(player, origin, destination, departure, speed, true)))
			.isEqualTo(new DeployedFleetId(origin, destination, departure, speed));
	}

}
