package com.scheible.risingempire.game.impl2.navy;

import java.util.Map;

import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author sj
 */
public class ShipsTest {

	@Test
	void testFailOnNegativeCount() {
		assertThatThrownBy(() -> new Ships(Map.of(new ShipClassId("scout"), -1)))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void testIgnoreZeroCount() {
		assertThat(new Ships(Map.of(new ShipClassId("scout"), 0, new ShipClassId("enterprise"), 1)).counts())
			.isEqualTo(Map.of(new ShipClassId("enterprise"), 1));
	}

}