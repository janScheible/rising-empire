package com.scheible.risingempire.game.impl2.apiinternal;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

	@Test
	void testInterpolate() {
		Position from = new Position(3, 4);
		Position to = new Position(0, 0);

		List<Position> positions = new ArrayList<>();
		Position current = from;
		while (!current.equals(to)) {
			positions.add(current);
			current = Position.interpolate(from, to, current, new Parsec(1));
		}
		positions.add(current);

		assertThat(positions).containsExactly(new Position("3.000", "4.000"), new Position("2.400", "3.200"),
				new Position("1.800", "2.400"), new Position("1.200", "1.600"), new Position("0.600", "0.800"),
				new Position("0.000", "0.000"));
	}

	@Test
	void testToPlainString() {
		assertThat(new Position("1.234", "5.678").toPlainString()).isEqualTo("1234x5678");
	}

	@Test
	void testFromPlainString() {
		assertThat(Position.fromPlainString("1234x5678")).isEqualTo(new Position("1.234", "5.678"));
	}

}
