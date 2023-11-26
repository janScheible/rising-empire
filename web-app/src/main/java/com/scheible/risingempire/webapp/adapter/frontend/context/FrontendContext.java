package com.scheible.risingempire.webapp.adapter.frontend.context;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.hypermedia.Action;
import org.springframework.http.HttpMethod;

/**
 * @author sj
 */
public class FrontendContext {

	private final String gameId;

	private final Player player;

	private final PlayerGame playerGame;

	private final GameView gameView;

	private final Optional<SystemId> selectedStarId;

	private final Optional<FleetId> selectedFleetId;

	protected FrontendContext(String gameId, Player player, PlayerGame playerView, GameView gameView,
			Optional<SystemId> selectedStarId, Optional<FleetId> selectedFleetId) {
		this.gameId = gameId;
		this.player = player;
		this.playerGame = playerView;
		this.gameView = gameView;
		this.selectedStarId = selectedStarId;
		this.selectedFleetId = selectedFleetId;
	}

	static FrontendContext createForGame(String gameId, Player player, PlayerGame playerView, GameView gameView,
			Optional<SystemId> selectedStarId, Optional<FleetId> selectedFleetId) {
		return new FrontendContext(gameId, player, playerView, gameView, selectedStarId, selectedFleetId);
	}

	static FrontendContext createEmpty(String gameId, Player player) {
		return new EmptyFrontendContext(gameId, player);
	}

	public FrontendContext withSelectedStar(String selectedStarId) {
		return withSelectedStar(new SystemId(selectedStarId));
	}

	public FrontendContext withSelectedStar(SystemId selectedStarId) {
		return new FrontendContext(this.gameId, this.player, this.playerGame, this.gameView,
				Optional.of(selectedStarId), this.selectedFleetId);
	}

	public String getGameId() {
		return this.gameId;
	}

	public Player getPlayer() {
		return this.player;
	}

	public PlayerGame getPlayerGame() {
		return this.playerGame;
	}

	public GameView getGameView() {
		return this.gameView;
	}

	public Optional<SystemId> getSelectedStarId() {
		return this.selectedStarId;
	}

	public Optional<FleetId> getSelectedFleetId() {
		return this.selectedFleetId;
	}

	private Stream<String> concatPathSegments(String... pathSegments) {
		return Stream.concat(Arrays.stream(new String[] { "game", "games", this.gameId, this.player.name() }),
				Arrays.stream(pathSegments));
	}

	public String[] toFrontendUri(String... pathSegments) {
		return concatPathSegments(pathSegments).toArray(String[]::new);
	}

	public Action toNamedAction(String name, HttpMethod httpMethod, boolean includeStarId, boolean includeFleetId,
			String... pathSegments) {
		return (httpMethod == HttpMethod.GET ? Action.get(name, toFrontendUri(pathSegments))
				: Action.jsonPost(name, toFrontendUri(pathSegments)))
			.with(includeStarId && this.selectedStarId.isPresent(), "selectedStarId",
					() -> this.selectedStarId.get().getValue())
			.with(includeFleetId && this.selectedFleetId.isPresent(), "selectedFleetId",
					() -> this.selectedFleetId.get().getValue());
	}

	public Action toAction(HttpMethod httpMethod, String... pathSegments) {
		return toNamedAction("from-frontend-context", httpMethod, true, true, pathSegments);
	}

	public boolean isEmpty() {
		return false;
	}

	private static class EmptyFrontendContext extends FrontendContext {

		EmptyFrontendContext(String gameId, Player player) {
			super(gameId, player, null, null, Optional.empty(), Optional.empty());
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

}
