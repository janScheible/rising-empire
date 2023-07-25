package com.scheible.risingempire.webapp.adapter.frontend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.scheible.risingempire.game.api.view.universe.Race;

/**
 *
 * @author sj
 */
public class RaceDtoTest {

	@Test
	public void testMappingCompletness() {
		assertThat(Stream.of(Race.values()).map(RaceDto::fromRace).collect(Collectors.toSet()))
				.containsOnly(RaceDto.values());
	}
}
