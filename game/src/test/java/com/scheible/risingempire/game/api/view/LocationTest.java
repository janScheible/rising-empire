package com.scheible.risingempire.game.api.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.scheible.risingempire.game.api.view.universe.Location;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class LocationTest {

	@Test
	void testDistance() {
		assertThat(new Location(50, 50).getDistance(new Location(100, 100)) - 70.7).isLessThan(0.1);
	}

	@Test
	void testMoveAlong() {
		final List<Location> stops = new ArrayList<>();
		final Location destination = new Location(250, 250);

		Location location = new Location(50, 50);
		for (int i = 0; i < 8; i++) {
			stops.add(location);
			location = location.moveAlong(destination, 50);
		}

		assertThat(stops).isEqualTo(Arrays.asList(new Location(50, 50), new Location(85, 85), new Location(120, 120),
				new Location(155, 155), new Location(190, 190), new Location(225, 225), new Location(250, 250),
				new Location(250, 250)));
	}

}
