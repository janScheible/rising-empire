package com.scheible.risingempire.game.impl2.apiinternal;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class PopulationTest {

	@Test
	void test10PercentageGrowthAtHalfOfPopulation() {
		assertThat(new Population(50).grow(new Population(100)).quantity()).isCloseTo(55.0,
				Percentage.withPercentage(1));
	}

}