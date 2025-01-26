package com.scheible.risingempire.game.impl2.colonization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.colonization.Colonization.SpaceDockShipClass;
import com.scheible.risingempire.game.impl2.colonization.SpaceDock.ConstructionProgress;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ColonizationTest {

	@Test
	void testSpaceDockNewShips() {
		ShipClassId first = new ShipClassId("first");
		ShipClassId second = new ShipClassId("second");
		Map<ShipClassId, Credit> shipCosts = Map.of(first, new Credit(2000), second, new Credit(600));

		Position colonySystem = new Position("6.173", "5.026");

		// build capacity per round = 1500 Credits
		Colonization colonization = new Colonization((Player _) -> Set.of(),
				(Player _, ShipClassId shipClassId) -> shipCosts.get(shipClassId));

		colonization.initialize(first);
		assertSpaceDock(colonization.colony(colonySystem), 0, new Rounds(2),
				new ConstructionProgress(first, new Credit(0)));

		colonization.updateColonies(List.of());
		colonization.buildShips();
		assertThat(colonization.newShips().get(colonySystem)).isNull();
		assertSpaceDock(colonization.colony(colonySystem), 1, new Rounds(1),
				new ConstructionProgress(first, new Credit(1500)));

		colonization.updateColonies(List.of());
		colonization.buildShips();
		assertThat(colonization.newShips().get(colonySystem)).isEqualTo(Map.of(first, 1));
		assertSpaceDock(colonization.colony(colonySystem), 1, new Rounds(1),
				new ConstructionProgress(first, new Credit(1000)));

		assertSpaceDock(colonization.apply(List.of(new SpaceDockShipClass(Player.BLUE, colonySystem, second)))
			.colony(colonySystem), 2, new Rounds(1), new ConstructionProgress(first, new Credit(1000)));
		colonization.updateColonies(List.of(new SpaceDockShipClass(Player.BLUE, colonySystem, second)));
		colonization.buildShips();
		assertThat(colonization.newShips().get(colonySystem)).isEqualTo(Map.of(second, 2));
		assertSpaceDock(colonization.colony(colonySystem), 3, new Rounds(1),
				new ConstructionProgress(second, new Credit(300)));
	}

	private static ObjectAssert<SpaceDock> assertSpaceDock(Optional<Colony> colony, int nextRoundCount, Rounds duration,
			ConstructionProgress constructionProgress) {
		return assertThat(colony.orElseThrow().spaceDock()).satisfies(spaceDock -> {
			assertThat(spaceDock.output().nextRoundCount()).isEqualTo(nextRoundCount);
			assertThat(spaceDock.output().duration()).isEqualTo(duration);
			assertThat(spaceDock.progress()).isEqualTo(constructionProgress);
		});
	}

}