package com.scheible.risingempire.game.impl2.game;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.common.Order;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sj
 */
public class PlayerTurnsTest {

	@Test
	public void testSomeMethod() {
		Round round = new Round(1);

		PlayerTurns turns = new PlayerTurns(Set.of(Player.BLUE, Player.WHITE));
		turns.finishTurn(Player.BLUE);
		turns.addOrder(Player.BLUE, new FirstOrder());
		assertThat(turns.roundFinished()).isFalse();
		turns.finishTurn(Player.WHITE);
		assertThat(turns.roundFinished()).isTrue();

		round = round.next();
		turns.beginNewRound(round);
		assertThat(turns.roundFinished()).isFalse();
		turns.finishTurn(Player.BLUE);
		turns.addOrder(Player.WHITE, new SecondOrder());
		turns.finishTurn(Player.WHITE);
		assertThat(turns.roundFinished()).isTrue();

		round = round.next();
		turns.beginNewRound(round);

		assertThat(turns.pastOrdersMapping()).containsOnly(
				Map.entry(new Round(1), Map.of(Player.BLUE, List.of(new FirstOrder()), Player.WHITE, List.of())),
				Map.entry(new Round(2), Map.of(Player.BLUE, List.of(), Player.WHITE, List.of(new SecondOrder()))));
	}

	private record FirstOrder() implements Order {

	}

	private record SecondOrder() implements Order {

	}

}