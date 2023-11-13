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
		final FleetId random = FleetId.createRandom();
		assertThat(new FleetId(random.getValue())).isEqualTo(random);
	}

}
