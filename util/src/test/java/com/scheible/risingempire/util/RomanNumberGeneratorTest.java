package com.scheible.risingempire.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class RomanNumberGeneratorTest {

	@Test
	void testSomeNumbers() {
		assertThat(RomanNumberGenerator.getNumber(1)).isEqualTo("I");
		assertThat(RomanNumberGenerator.getNumber(3)).isEqualTo("III");
		assertThat(RomanNumberGenerator.getNumber(6)).isEqualTo("VI");
		assertThat(RomanNumberGenerator.getNumber(42)).isEqualTo("XLII");
	}

}