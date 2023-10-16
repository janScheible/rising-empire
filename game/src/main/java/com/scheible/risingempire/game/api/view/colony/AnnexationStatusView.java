package com.scheible.risingempire.game.api.view.colony;

import com.scheible.risingempire.game.api.view.universe.Player;
import java.util.Optional;

/**
 *
 * @author sj
 */
public record AnnexationStatusView(Optional<Integer> siegeRounds, Optional<Integer> roundsUntilAnnexable,
		Optional<Player> siegingPlayer,	Optional<Boolean> annexable, Optional<Boolean> annexCommand) {

	public AnnexationStatusView {
		final boolean siegeState = siegeRounds.isPresent() && roundsUntilAnnexable.isPresent() 
				&& siegingPlayer.isPresent() && annexable.isEmpty() && annexCommand.isEmpty();
		final boolean annexableState = siegeRounds.isPresent() && roundsUntilAnnexable.isPresent() 
				&& siegingPlayer.isPresent() && annexable.isPresent() && annexCommand.isPresent();

		if(!siegeState && !annexableState) {
			throw new IllegalArgumentException("Must be either siege or annexable state!");
		}
	}
}
