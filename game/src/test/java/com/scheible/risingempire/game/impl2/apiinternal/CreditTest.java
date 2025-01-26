package com.scheible.risingempire.game.impl2.apiinternal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class CreditTest {

	@Test
	void testIntegerDivide() {
		assertThat(new Credit(100).integerDivide(new Credit(30))).isEqualTo(3);
	}

	@Test
	void testDivideRoundUp() {
		assertThat(new Credit(100).divideRoundUp(new Credit(30))).isEqualTo(4);
	}

	@Test
	void testModulo() {
		assertThat(new Credit(100).modulo(new Credit(30))).isEqualTo(new Credit(10));
	}

	@Test
	void testLessThan() {
		assertThat(new Credit(100).lessThan(new Credit(30))).isFalse();
		assertThat(new Credit(30).lessThan(new Credit(30))).isFalse();

		assertThat(new Credit(29).lessThan(new Credit(30))).isTrue();
	}

}