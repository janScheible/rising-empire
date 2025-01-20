package com.scheible.risingempire.game.impl2.colonization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
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
		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId _) -> new Credit(110));
		colonization.updateColonies(List
			.of(new SpaceDockShipClass(Player.BLUE, new Position("6.173", "5.026"), new ShipClassId("colony-ship"))));

		assertThat(colonization.colony(Player.BLUE, new Position("6.173", "5.026")).orElseThrow())
			.extracting(Colony::spaceDock)
			.extracting(SpaceDock::current)
			.extracting(ShipClassId::value)
			.isEqualTo("colony-ship");
	}

	@Test
	public void testBuildShips() {
		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId shipClassId) -> shipClassId.equals(new ShipClassId("scout")) ? new Credit(400)
						: new Credit(1600));

		colonization.buildShips();
		assertThat(colonization.newShips().get(new Position("6.173", "5.026")))
			.containsOnly(Map.entry(new ShipClassId("scout"), 3));
		colonization.buildShips();
		assertThat(colonization.newShips().get(new Position("6.173", "5.026")))
			.containsOnly(Map.entry(new ShipClassId("scout"), 4));

		colonization.updateColonies(List
			.of(new SpaceDockShipClass(Player.YELLOW, new Position("6.173", "5.026"), new ShipClassId("enterprise"))));
		colonization.buildShips();
		assertThat(colonization.newShips().get(new Position("6.173", "5.026"))).isNull();
		colonization.buildShips();
		assertThat(colonization.newShips().get(new Position("6.173", "5.026"))).isNotNull();
		assertThat(colonization.newShips().get(new Position("6.173", "5.026")))
			.containsOnly(Map.entry(new ShipClassId("enterprise"), 1));
	}

}