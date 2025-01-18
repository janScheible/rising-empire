package com.scheible.risingempire.game.impl2.colonization;

import java.util.List;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.colonization.Colonization.SpaceDockShipClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ColonizationTest {

	@Test
	public void testUpdateColonies() {
		Colonization colonization = new Colonization((Player _) -> Set.of());
		colonization.updateColonies(List
			.of(new SpaceDockShipClass(Player.BLUE, new Position("6.173", "5.026"), new ShipClassId("colony-ship"))));

		assertThat(colonization.colony(Player.BLUE, new Position("6.173", "5.026")).orElseThrow())
			.extracting(Colony::spaceDockShipClass)
			.extracting(ShipClassId::value)
			.isEqualTo("colony-ship");
	}

}