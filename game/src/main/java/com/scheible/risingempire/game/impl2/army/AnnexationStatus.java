package com.scheible.risingempire.game.impl2.army;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;

/**
 * @author sj
 */
public record AnnexationStatus(Optional<Rounds> siegeRounds, Optional<Rounds> roundsUntilAnnexable,
		Optional<Player> siegingPlayer, boolean annexable, boolean annexationCommand) {

}
