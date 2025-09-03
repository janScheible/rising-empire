package com.scheible.risingempire.game.impl2.army;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.army.Army.Annex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class ArmyTest {

	@Test
	public void testAnnexSystems() {
		Position system = new Position("6.000", "8.00");
		Player player = Player.BLUE;
		Player otherPlayer = Player.YELLOW;

		Army army = new Army(() -> Set.of(new SiegedSystem(system, otherPlayer, player)), 5);

		assertThat(army.annexationStatus(player, system)).isEmpty();
		assertThat(army.annexationStatus(otherPlayer, system)).isEmpty();

		Round round = new Round(1);
		army.annexSystems(round = round.next(), List.of());
		for (int i = 0; i < 4; i++) {
			army.annexSystems(round = round.next(), List.of());

			assertThat(army.annexationStatus(player, system).map(AnnexationStatus::annexable)).contains(false);
			assertThat(army.annexationStatus(player, system).map(AnnexationStatus::annexationCommand)).contains(false);

			assertThat(army.annexationStatus(otherPlayer, system).map(AnnexationStatus::annexable)).contains(false);
			assertThat(army.annexationStatus(otherPlayer, system).map(AnnexationStatus::annexationCommand))
				.contains(false);
		}

		army.annexSystems(round = round.next(), List.of(new Annex(Player.BLUE, system, false)));
		assertThat(army.annexedSystems()).containsOnly(Map.entry(player, system));
	}

	@Test
	public void testInterruptedSiege() {
		Position system = new Position("6.000", "8.00");
		Player player = Player.BLUE;

		Set<SiegedSystem> siegedSystems = new HashSet<>(Set.of(new SiegedSystem(system, Player.YELLOW, player)));

		Army army = new Army(() -> siegedSystems, 5);

		Round round = new Round(1);
		army.annexSystems(round = round.next(), List.of());
		for (int i = 0; i < 3; i++) {
			army.annexSystems(round = round.next(), List.of());

			assertThat(army.annexationStatus(player, system).map(AnnexationStatus::annexable)).contains(false);
			assertThat(army.annexationStatus(player, system).map(AnnexationStatus::annexationCommand)).contains(false);
		}

		siegedSystems.clear();
		army.annexSystems(round = round.next(), List.of());
		assertThat(army.annexationStatus(player, system)).isEmpty();

		siegedSystems.addAll(Set.of(new SiegedSystem(system, Player.YELLOW, player)));
		army.annexSystems(round = round.next(), List.of());
		assertThat(army.annexationStatus(player, system).flatMap(AnnexationStatus::siegeRounds)).isEmpty();
	}

}