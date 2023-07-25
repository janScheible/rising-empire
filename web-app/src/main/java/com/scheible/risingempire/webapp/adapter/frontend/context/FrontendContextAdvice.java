package com.scheible.risingempire.webapp.adapter.frontend.context;

import java.util.Optional;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.game.GameHolder;

/**
 *
 * @author sj
 */
@ControllerAdvice(annotations = FrontendController.class)
class FrontendContextAdvice {

	private final GameHolder gameHolder;

	FrontendContextAdvice(final GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	@ModelAttribute
	public FrontendContext createFrontendContext(@PathVariable final String gameId, @PathVariable final Player player,
			@RequestParam final Optional<SystemId> selectedStarId,
			@RequestParam final Optional<FleetId> selectedFleetId) {
		final Optional<Game> game = gameHolder.get(gameId);

		return game
				.map(g -> FrontendContext.createForGame(gameId, player, g.forPlayer(player),
						g.forPlayer(player).getView(), selectedStarId, selectedFleetId))
				.orElseGet(() -> FrontendContext.createEmpty(gameId, player));

	}
}
