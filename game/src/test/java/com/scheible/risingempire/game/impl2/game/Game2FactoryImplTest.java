package com.scheible.risingempire.game.impl2.game;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.scheible.risingempire.util.SeededRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class Game2FactoryImplTest {

	private enum TestEnum {

		FIRST, SECOND, THIRD;

	}

	@Test
	void testPercentagewiseRandom() {
		int totalCount = 1_000_000;
		SeededRandom random = new SeededRandom();

		Map<TestEnum, Integer> counts = new HashMap<>();
		for (int i = 0; i < totalCount; i++) {

			TestEnum enumConst = Game2FactoryImpl.percentagewiseRandom(
					List.of(TestEnum.FIRST, TestEnum.SECOND, TestEnum.THIRD), List.of(25, 50, 25), random);

			counts.compute(enumConst, (k, v) -> (v == null) ? 1 : v + 1);
		}

		assertThat(counts.entrySet()
			.stream()
			.map(e -> Map.entry(e.getKey(), String.format(Locale.ROOT, "%.2f", e.getValue() / (double) totalCount)))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
			.isEqualTo(Map.of(TestEnum.FIRST, "0.25", TestEnum.SECOND, "0.50", TestEnum.THIRD, "0.25"));
	}

}