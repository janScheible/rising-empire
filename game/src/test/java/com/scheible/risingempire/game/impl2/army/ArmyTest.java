package com.scheible.risingempire.game.impl2.army;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.army.Army.Annex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

/**
 * @author sj
 */
public class ArmyTest {

	@Test
	public void testAnnexSystems() {
		Position system = new Position("6.000", "8.00");
		Player player = Player.BLUE;
		Player otherPlayer = Player.YELLOW;

		SiegedSystemsProvider siegedSystemsProvider = Mockito.mock(SiegedSystemsProvider.class);
		doReturn(Set.of(new SiegedSystem(system, otherPlayer, player))).when(siegedSystemsProvider).siegedSystems();

		Army army = new Army(siegedSystemsProvider);

		assertThat(army.annexationStatus(player, system)).isEmpty();
		assertThat(army.annexationStatus(otherPlayer, system)).isEmpty();

		Round round = new Round(1);
		for (int i = 0; i < 5; i++) {
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

		SiegedSystemsProvider siegedSystemsProvider = Mockito.mock(SiegedSystemsProvider.class);
		doReturn(Set.of(new SiegedSystem(system, Player.YELLOW, player))).when(siegedSystemsProvider).siegedSystems();

		Army army = new Army(siegedSystemsProvider);

		Round round = new Round(1);
		for (int i = 0; i < 3; i++) {
			army.annexSystems(round = round.next(), List.of());

			assertThat(army.annexationStatus(player, system).map(AnnexationStatus::annexable)).contains(false);
			assertThat(army.annexationStatus(player, system).map(AnnexationStatus::annexationCommand)).contains(false);
		}

		doReturn(Set.of()).when(siegedSystemsProvider).siegedSystems();
		army.annexSystems(round = round.next(), List.of());
		assertThat(army.annexationStatus(player, system)).isEmpty();

		doReturn(Set.of(new SiegedSystem(system, Player.YELLOW, player))).when(siegedSystemsProvider).siegedSystems();
		army.annexSystems(round = round.next(), List.of());
		assertThat(army.annexationStatus(player, system).flatMap(AnnexationStatus::siegeRounds))
			.contains(new Rounds(0));
	}

}