package com.scheible.risingempire.game.impl2.view;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public class LocationMapper {

	private static final Parsec FACTOR = new Parsec("75.0");

	public static int toLocationValue(Parsec parse) {
		return parse.multiply(FACTOR).quantity().intValue();
	}

	public static Parsec fromLocationValue(int width) {
		return new Parsec(width).divide(FACTOR);
	}

	public static Location toLocation(Position position) {
		return new Location(toLocationValue(position.x()), toLocationValue(position.y()));
	}

}
