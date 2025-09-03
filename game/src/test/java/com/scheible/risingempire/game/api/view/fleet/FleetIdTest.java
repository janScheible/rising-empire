package com.scheible.risingempire.game.api.view.fleet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author sj
 */
class FleetIdTest {

	@Test
	void testInvalid() {
		assertThatThrownBy(() -> new FleetId("ab")).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("is invalid!");
	}

	@Test
	void testValid() {
		assertThat(new FleetId("fB1.234x5.678->1.222x3.444@42w/9.134")).isNotNull();
		assertThat(new FleetId("tB1.234x5.678->1.222x3.444@42w/9.134")).isNotNull();

		assertThat(new FleetId("fB1.234x5.678")).isNotNull();
		assertThat(new FleetId("tB1.234x5.678")).isNotNull();

	}

}
