package com.scheible.risingempire.game.impl2.apiinternal;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

	@Test
	void testInterpolate() {
		Position from = new Position(0, 0);
		Position to = new Position(new Parsec(3), new Parsec(4));

		List<Position> positions = new ArrayList<>();
		Position current = from;
		while (!current.equals(to)) {
			positions.add(current);
			current = Position.interpolate(from, to, current, new Parsec(1));
		}
		positions.add(current);

		assertThat(positions).containsExactly(new Position(new Parsec("0.000"), new Parsec("0.000")),
				new Position(new Parsec("0.600"), new Parsec("0.800")),
				new Position(new Parsec("1.200"), new Parsec("1.600")),
				new Position(new Parsec("1.800"), new Parsec("2.400")),
				new Position(new Parsec("2.400"), new Parsec("3.200")),
				new Position(new Parsec("3.000"), new Parsec("4.000")));
	}

	@Test
	void testToPlainString() {
		assertThat(new Position(new Parsec("1.234"), new Parsec("5.678")).toPlainString()).isEqualTo("1234x5678");
	}

	@Test
	void testFromPlainString() {
		assertThat(Position.fromPlainString("1234x5678"))
			.isEqualTo(new Position(new Parsec("1.234"), new Parsec("5.678")));
	}

}
