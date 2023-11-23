package com.scheible.risingempire.game.impl.colony;

import java.util.EnumMap;
import java.util.Map;

import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class ColonyTest {

	@Test
	void testIncreaseByReduceHighestWithOneEnough() {
		Map<ProductionArea, Integer> ratios;
		Colony.adjustRationInternal(ProductionArea.DEFENCE, 30,
				ratios = new EnumMap<>(Map.of(ProductionArea.SHIP, 25, ProductionArea.DEFENCE, 20,
						ProductionArea.INDUSTRY, 15, ProductionArea.ECOLOGY, 20, ProductionArea.TECHNOLOGY, 20)));
		assertThat(ratios).isEqualTo(new EnumMap<>(Map.of(ProductionArea.SHIP, 15, ProductionArea.DEFENCE, 30,
				ProductionArea.INDUSTRY, 15, ProductionArea.ECOLOGY, 20, ProductionArea.TECHNOLOGY, 20)));
	}

	@Test
	void testIncreaseByReduceHighestWithTwoNeeded() {
		Map<ProductionArea, Integer> ratios;
		Colony.adjustRationInternal(ProductionArea.DEFENCE, 60,
				ratios = new EnumMap<>(Map.of(ProductionArea.SHIP, 25, ProductionArea.DEFENCE, 20,
						ProductionArea.INDUSTRY, 15, ProductionArea.ECOLOGY, 25, ProductionArea.TECHNOLOGY, 15)));
		assertThat(ratios).isEqualTo(new EnumMap<>(Map.of(ProductionArea.SHIP, 0, ProductionArea.DEFENCE, 60,
				ProductionArea.INDUSTRY, 15, ProductionArea.ECOLOGY, 10, ProductionArea.TECHNOLOGY, 15)));
	}

	@Test
	void testDecreaseByRaiseLowest() {
		Map<ProductionArea, Integer> ratios;
		Colony.adjustRationInternal(ProductionArea.DEFENCE, 10,
				ratios = new EnumMap<>(Map.of(ProductionArea.SHIP, 25, ProductionArea.DEFENCE, 20,
						ProductionArea.INDUSTRY, 10, ProductionArea.ECOLOGY, 30, ProductionArea.TECHNOLOGY, 15)));
		assertThat(ratios).isEqualTo(new EnumMap<>(Map.of(ProductionArea.SHIP, 25, ProductionArea.DEFENCE, 10,
				ProductionArea.INDUSTRY, 20, ProductionArea.ECOLOGY, 30, ProductionArea.TECHNOLOGY, 15)));
	}

}
