package com.scheible.risingempire.game.impl2.intelligence.fleet;

import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public interface ScanAreasProvider {

	Set<ScanArea> scanAreas(Player player);

	record ScanArea(Position position, Parsec radius) {

	}

}
