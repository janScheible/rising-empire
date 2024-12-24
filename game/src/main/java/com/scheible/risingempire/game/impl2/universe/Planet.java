package com.scheible.risingempire.game.impl2.universe;

import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.impl2.apiinternal.Population;

/**
 * @author sj
 */
public record Planet(PlanetType type, PlanetSpecial special, Population max) {

}
