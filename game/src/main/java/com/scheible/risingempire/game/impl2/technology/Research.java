package com.scheible.risingempire.game.impl2.technology;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;

/**
 * @author sj
 */
public record Research(Player player, Optional<Tech> tech, ResearchPoint progress) {

}
