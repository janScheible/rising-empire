package com.scheible.risingempire.game.impl2.universe;

import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.impl2.apiinternal.Position;

/**
 * @author sj
 */
public record Star(String name, Position position, StarType type, boolean small) {

}
