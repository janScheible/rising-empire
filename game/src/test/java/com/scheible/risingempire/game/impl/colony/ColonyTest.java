package com.scheible.risingempire.game.impl.colony;

import static com.scheible.risingempire.game.api.view.colony.ProductionArea.DEFENCE;
import static com.scheible.risingempire.game.api.view.colony.ProductionArea.ECOLOGY;
import static com.scheible.risingempire.game.api.view.colony.ProductionArea.INDUSTRY;
import static com.scheible.risingempire.game.api.view.colony.ProductionArea.SHIP;
import static com.scheible.risingempire.game.api.view.colony.ProductionArea.TECHNOLOGY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.api.view.colony.ProductionArea;

/**
 *
 * @author sj
 */
class ColonyTest {

	@Test
	void testIncreaseByReduceHighestWithOneEnough() {
		Map<ProductionArea, Integer> ratios;
		Colony.adjustRationInternal(DEFENCE, 30,
				ratios = new EnumMap<>(Map.of(SHIP, 25, DEFENCE, 20, INDUSTRY, 15, ECOLOGY, 20, TECHNOLOGY, 20)));
		assertThat(ratios)
				.isEqualTo(new EnumMap<>(Map.of(SHIP, 15, DEFENCE, 30, INDUSTRY, 15, ECOLOGY, 20, TECHNOLOGY, 20)));
	}

	@Test
	void testIncreaseByReduceHighestWithTwoNeeded() {
		Map<ProductionArea, Integer> ratios;
		Colony.adjustRationInternal(DEFENCE, 60,
				ratios = new EnumMap<>(Map.of(SHIP, 25, DEFENCE, 20, INDUSTRY, 15, ECOLOGY, 25, TECHNOLOGY, 15)));
		assertThat(ratios)
				.isEqualTo(new EnumMap<>(Map.of(SHIP, 0, DEFENCE, 60, INDUSTRY, 15, ECOLOGY, 10, TECHNOLOGY, 15)));
	}

	@Test
	void testDecreaseByRaiseLowest() {
		Map<ProductionArea, Integer> ratios;
		Colony.adjustRationInternal(DEFENCE, 10,
				ratios = new EnumMap<>(Map.of(SHIP, 25, DEFENCE, 20, INDUSTRY, 10, ECOLOGY, 30, TECHNOLOGY, 15)));
		assertThat(ratios)
				.isEqualTo(new EnumMap<>(Map.of(SHIP, 25, DEFENCE, 10, INDUSTRY, 20, ECOLOGY, 30, TECHNOLOGY, 15)));
	}
}
