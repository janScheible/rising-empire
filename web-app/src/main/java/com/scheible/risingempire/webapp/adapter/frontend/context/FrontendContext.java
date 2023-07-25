package com.scheible.risingempire.webapp.adapter.frontend.context;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.http.HttpMethod;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.hypermedia.Action;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 *
 * @author sj
 */
public class FrontendContext {

	private static class EmptyFrontendContext extends FrontendContext {

		public EmptyFrontendContext(final String gameId, final Player player) {
			super(gameId, player, null, null, null, (FleetId) null);
		}

		@Override
		public GameView getGameView() {
			throw new IllegalStateException("Frontend context is empty!");
		}

		@Override
		public PlayerGame getPlayerGame() {
			throw new IllegalStateException("Frontend context is empty!");
		}

		@Override
		public Optional<FleetId> getSelectedFleetId() {
			throw new IllegalStateException("Frontend context is empty!");
		}

		@Override
		public Optional<SystemId> getSelectedStarId() {
			throw new IllegalStateException("Frontend context is empty!");
		}

		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	private final String gameId;
	private final Player player;
	private final PlayerGame playerGame;
	private final GameView gameView;

	@Nullable
	private final SystemId selectedStarId;
	@Nullable
	private final FleetId selectedFleetId;

	protected FrontendContext(final String gameId, final Player player, final PlayerGame playerView,
			final GameView gameView, @Nullable final SystemId selectedStarId, @Nullable final FleetId selectedFleetId) {
		this.gameId = gameId;
		this.player = player;
		this.playerGame = playerView;
		this.gameView = gameView;
		this.selectedStarId = selectedStarId;
		this.selectedFleetId = selectedFleetId;
	}

	static FrontendContext createForGame(final String gameId, final Player player, final PlayerGame playerView,
			final GameView gameView, final Optional<SystemId> selectedStarId, final Optional<FleetId> selectedFleetId) {
		return new FrontendContext(gameId, player, playerView, gameView, selectedStarId.orElse(null),
				selectedFleetId.orElse(null));
	}

	static FrontendContext createEmpty(final String gameId, final Player player) {
		return new EmptyFrontendContext(gameId, player);
	}

	public FrontendContext withSelectedStar(final String selectedStarId) {
		return withSelectedStar(new SystemId(selectedStarId));
	}

	public FrontendContext withSelectedStar(final SystemId selectedStarId) {
		return new FrontendContext(gameId, player, playerGame, gameView, selectedStarId, selectedFleetId);
	}

	public String getGameId() {
		return gameId;
	}

	public Player getPlayer() {
		return player;
	}

	public PlayerGame getPlayerGame() {
		return playerGame;
	}

	public GameView getGameView() {
		return gameView;
	}

	public Optional<SystemId> getSelectedStarId() {
		return Optional.ofNullable(selectedStarId);
	}

	public Optional<FleetId> getSelectedFleetId() {
		return Optional.ofNullable(selectedFleetId);
	}

	private Stream<String> concatPathSegments(final String... pathSegments) {
		return Stream.concat(Arrays.stream(new String[] { "frontend", gameId, player.name() }),
				Arrays.stream(pathSegments));
	}

	public String[] toFrontendUri(final String... pathSegments) {
		return concatPathSegments(pathSegments).toArray(String[]::new);
	}

	public Action toNamedAction(final String name, final HttpMethod httpMethod, final boolean includeStarId,
			final boolean includeFleetId, final String... pathSegments) {
		return (httpMethod == HttpMethod.GET ? Action.get(name, toFrontendUri(pathSegments))
				: Action.jsonPost(name, toFrontendUri(pathSegments)))
						.with(includeStarId && selectedStarId != null, "selectedStarId",
								() -> selectedStarId.getValue())
						.with(includeFleetId && selectedFleetId != null, "selectedFleetId",
								() -> selectedFleetId.getValue());
	}

	public Action toAction(final HttpMethod httpMethod, final String... pathSegments) {
		return toNamedAction("from-frontend-context", httpMethod, true, true, pathSegments);
	}

	public boolean isEmpty() {
		return false;
	}
}
