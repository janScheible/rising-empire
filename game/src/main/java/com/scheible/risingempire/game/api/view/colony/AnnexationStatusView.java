package com.scheible.risingempire.game.api.view.colony;

import java.util.Optional;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusViewBuilder.SiegeRoundsStage;

/**
 * @author sj
 */
@StagedRecordBuilder
public record AnnexationStatusView(Optional<Integer> siegeRounds, Optional<Integer> roundsUntilAnnexable,
		Optional<Player> siegingPlayer, Optional<Race> siegingRace, boolean annexable, boolean annexationCommand) {

	public AnnexationStatusView {
		boolean siegeState = siegeRounds.isPresent() && roundsUntilAnnexable.isPresent() && siegingPlayer.isPresent()
				&& !annexable && !annexationCommand;
		boolean annexableState = siegeRounds.isPresent() && roundsUntilAnnexable.isPresent()
				&& siegingPlayer.isPresent() && annexable;

		if (!siegeState && !annexableState) {
			throw new IllegalArgumentException("Must be either siege or annexable state!");
		}
	}

	public static SiegeRoundsStage builder() {
		return AnnexationStatusViewBuilder.builder();
	}

	public Optional<Integer> progress() {
		if (this.siegeRounds.isPresent() && this.roundsUntilAnnexable.isPresent()) {
			return Optional.of(Math.round((float) this.siegeRounds().get()
					/ (this.siegeRounds().get() + this.roundsUntilAnnexable().get()) * 100.0f));
		}
		else {
			return Optional.empty();
		}
	}

}
