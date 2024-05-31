package com.scheible.risingempire.game.api._testgame;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;

/**
 * Game scenario for blue player.
 * 
 * @author sj
 */
class TestScenarioImpl implements TestScenario {

	private final Optional<List<BiConsumer<PlayerGame, GameView>>> turnLogics;

	private Game game = null;

	/**
	 * Invoked with this constructor the game is created but the turn logics are just
	 * collected in the passed list and not run.
	 */
	TestScenarioImpl(List<BiConsumer<PlayerGame, GameView>> turnLogics) {
		this.turnLogics = Optional.of(turnLogics);
	}

	/**
	 * Invoked with this constructor the game is created and the turn logics are run with
	 * a subsequent finishTurn() right away.
	 */
	TestScenarioImpl() {
		this.turnLogics = Optional.empty();
	}

	@Override
	public GameTurn customize(Consumer<GameOptions> optionsCustomizer) {
		GameOptions gameOptions = GameOptions.forTestGame();

		optionsCustomizer.accept(gameOptions);

		this.game = GameFactory.get().create(gameOptions);
		this.game.registerAi(Player.WHITE);
		this.game.registerAi(Player.YELLOW);

		return this.turnLogics.isPresent() ? new RecordingGameTurn(this.turnLogics.get())
				: new ExecutingGameTurn(this.game);
	}

	@Override
	public Game getGame() {
		if (this.game == null) {
			throw new IllegalStateException("customize() must be called before the game can be accessed!");
		}

		return this.game;
	}

	@Override
	public void applyTurnLogic(Game game) {
		if (this.turnLogics.isEmpty()) {
			throw new IllegalStateException("Can't apply turn logic when running the logic immediately.");
		}

		if (!this.turnLogics.get().isEmpty()) {
			BiConsumer<PlayerGame, GameView> turnLogic = this.turnLogics.get().remove(0);
			PlayerGame blueGame = game.forPlayer(Player.BLUE);
			GameView blueView = blueGame.getView();

			turnLogic.accept(blueGame, blueView);
		}
	}

	private static class RecordingGameTurn implements GameTurn {

		private final List<BiConsumer<PlayerGame, GameView>> turnLogics;

		private RecordingGameTurn(List<BiConsumer<PlayerGame, GameView>> turnLogics) {
			this.turnLogics = turnLogics;
		}

		@Override
		public GameTurn turn(BiConsumer<PlayerGame, GameView> turnLogic) {
			this.turnLogics.add(turnLogic);
			return this;
		}

	}

	private static class ExecutingGameTurn implements GameTurn {

		private final Game game;

		private ExecutingGameTurn(Game game) {
			this.game = game;
		}

		@Override
		public GameTurn turn(BiConsumer<PlayerGame, GameView> turnLogic) {
			PlayerGame playerGame = this.game.forPlayer(Player.BLUE);
			GameView gameView = playerGame.getView();

			turnLogic.accept(playerGame, gameView);

			playerGame.finishTurn();

			return this;
		}

	}

}
