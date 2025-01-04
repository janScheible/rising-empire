package com.scheible.risingempire.game.impl2.intelligence.system;

import java.util.Optional;

import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.intelligence.system.ColonyIntelProvider.ColonyIntel;

/**
 * @author sj
 */
public record SystemIntel(Round firstSeen, Round lastSeen, Optional<ColonyIntel> colonyIntel) {

}
