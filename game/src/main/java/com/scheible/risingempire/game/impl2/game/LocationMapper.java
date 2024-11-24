package com.scheible.risingempire.game.impl2.game;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
class LocationMapper {

	private static final Parsec FACTOR = new Parsec("75.0");

	static int toLocationValue(Parsec parse) {
		return parse.multiply(FACTOR).quantity().intValue();
	}

	static Parsec fromLocationValue(int width) {
		return new Parsec(width).divide(FACTOR);
	}

	static Location toLocation(Position position) {
		return new Location(toLocationValue(position.x()), toLocationValue(position.y()));
	}

}
