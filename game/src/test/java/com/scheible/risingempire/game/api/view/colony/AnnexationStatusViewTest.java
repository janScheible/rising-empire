package com.scheible.risingempire.game.api.view.colony;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
class AnnexationStatusViewTest {

	@Test
	void testProgress() {
		AnnexationStatusView annexationStatusView = AnnexationStatusView.builder()
			.siegeRounds(Optional.of(2))
			.roundsUntilAnnexable(Optional.of(3))
			.siegingPlayer(Optional.of(Player.BLUE))
			.siegingRace(Optional.of(Race.LUMERISKS))
			.annexable(false)
			.annexationCommand(false)
			.build();

		assertThat(annexationStatusView.progress()).contains(40);
	}

}