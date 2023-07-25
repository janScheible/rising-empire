package com.scheible.risingempire.game.impl.ship;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.api.view.ship.ShipSize;

/**
 *
 * @author sj
 */
public class ShipSizeWithBaseValuesTest {

	@Test
	void testBaseValueShipDesignMapping() {
		assertThat(Stream.of(ShipSize.values()).map(ShipSize::name).map(ShipSizeWithBaseValues::valueOf)
				.filter(Objects::nonNull).count()).isEqualTo(ShipSizeWithBaseValues.values().length);
	}
}
