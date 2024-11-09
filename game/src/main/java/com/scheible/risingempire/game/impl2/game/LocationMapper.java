package com.scheible.risingempire.game.impl2.game;

import java.math.BigDecimal;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
class LocationMapper {

	static int toLocationValue(Parsec parse) {
		return parse.quantity().multiply(BigDecimal.TEN).intValue();
	}

	static Location toLocation(Position position) {
		return new Location(toLocationValue(position.x()), toLocationValue(position.y()));
	}

}
