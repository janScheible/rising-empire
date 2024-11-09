package com.scheible.risingempire.game.api.view.fleet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author sj
 */
class FleetIdTest {

	@Test
	void testInvalidId() {
		assertThatThrownBy(() -> new FleetId("ab")).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Id must be a valid hex number in the interval");
	}

	@Test
	void testRandomIdValidity() {
		FleetId random = FleetId.createRandom();
		assertThat(new FleetId(random.value())).isEqualTo(random);
	}

	@Test
	void testGame2Ids() {
		FleetId deployedFleetId = new FleetId("f1.234x5.678->1.222x3.444@42w/9.134");
		assertThat(deployedFleetId).isNotNull();

		FleetId orbitingFleetId = new FleetId("f1.234x5.678");
		assertThat(orbitingFleetId).isNotNull();
	}

}
