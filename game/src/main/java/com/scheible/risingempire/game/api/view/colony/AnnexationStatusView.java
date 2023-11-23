package com.scheible.risingempire.game.api.view.colony;

import java.util.Optional;

import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

/**
 * @author sj
 */
public record AnnexationStatusView(Optional<Integer> siegeRounds, Optional<Integer> roundsUntilAnnexable,
		Optional<Player> siegingPlayer, Optional<Race> siegingRace, Optional<Boolean> annexable,
		Optional<Boolean> annexCommand) {

	public AnnexationStatusView {
		boolean siegeState = siegeRounds.isPresent() && roundsUntilAnnexable.isPresent() && siegingPlayer.isPresent()
				&& annexable.isEmpty() && annexCommand.isEmpty();
		boolean annexableState = siegeRounds.isPresent() && roundsUntilAnnexable.isPresent()
				&& siegingPlayer.isPresent() && annexable.isPresent() && annexCommand.isPresent();

		if (!siegeState && !annexableState) {
			throw new IllegalArgumentException("Must be either siege or annexable state!");
		}
	}
}
