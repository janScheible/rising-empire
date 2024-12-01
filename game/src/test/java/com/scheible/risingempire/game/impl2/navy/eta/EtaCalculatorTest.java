package com.scheible.risingempire.game.impl2.navy.eta;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.navy.AbstractNavyTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class EtaCalculatorTest extends AbstractNavyTest {

	@Test
	void testEta() {
		EtaCalculator etaCalculator = new EtaCalculator(this.shipMovementSpecsProvider);

		Optional<Rounds> eta = etaCalculator.calc(this.player, this.origin, this.destination, ships(this.scout, 1),
				Set.of());

		assertThat(eta).contains(new Rounds(2));
	}

}
