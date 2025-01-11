package com.scheible.risingempire.game.impl2.spacecombat;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.spacecombat.EncounteringFleetShipsProvider.EncounteringFleet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class SpaceCombatTest {

	private final Position conflictSystem = new Position(5.0, 5.0);

	private final ShipClassId shipClass = new ShipClassId("class");

	@Test
	public void testRetreatingFleets() {
		SpaceCombat spaceCombat = new SpaceCombat(() -> Map.of(this.conflictSystem,
				Map.of(Player.BLUE, new EncounteringFleet(Map.of(this.shipClass, 1), Optional.empty()), Player.YELLOW,
						new EncounteringFleet(Map.of(this.shipClass, 1), Optional.of(0.5)), Player.GREEN,
						new EncounteringFleet(Map.of(this.shipClass, 1), Optional.of(0.5)))));

		spaceCombat.resolve();
		assertThat(spaceCombat.retreatingFleets()).containsOnly(new RetreatingFleet(Player.YELLOW, this.conflictSystem),
				new RetreatingFleet(Player.GREEN, this.conflictSystem));
	}

}