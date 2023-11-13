package com.scheible.risingempire.webapp.adapter.frontend.dto;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.universe.Player;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class PlayerDtoTest {

	@Test
	public void testMappingCompletness() {
		assertThat(Stream.of(Player.values()).map(PlayerDto::fromPlayer).collect(Collectors.toSet()))
			.containsOnly(PlayerDto.values());
	}

}
