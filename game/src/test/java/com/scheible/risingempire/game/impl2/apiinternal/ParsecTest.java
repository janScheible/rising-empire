package com.scheible.risingempire.game.impl2.apiinternal;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParsecTest {

	@Test
	void testNormalization() {
		Parsec fourPointFiveParsecs = new Parsec(BigDecimal.valueOf(4500, 3));

		assertThat(Parsec.fromMilliparsec(4500)).isEqualTo(fourPointFiveParsecs);
		assertThat(new Parsec(4.5)).isEqualTo(fourPointFiveParsecs);
		assertThat(new Parsec("4.5")).isEqualTo(fourPointFiveParsecs);
		assertThat(new Parsec(new BigDecimal(4.5))).isEqualTo(fourPointFiveParsecs);

		// more precision than milliparsec is ignored
		assertThat(new Parsec("4.5009")).isEqualTo(fourPointFiveParsecs);
	}

	@Test
	void testToPlainString() {
		assertThat(new Parsec("1.234").toPlainString()).isEqualTo("1234");
	}

	@Test
	void testRoundUp() {
		assertThat(new Parsec("0.999").roundUp()).isEqualTo(1);
		assertThat(new Parsec("1.0").roundUp()).isEqualTo(1);
		assertThat(new Parsec("1.001").roundUp()).isEqualTo(2);
	}

}
