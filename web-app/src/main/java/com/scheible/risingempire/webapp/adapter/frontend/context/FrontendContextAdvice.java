package com.scheible.risingempire.webapp.adapter.frontend.context;

import java.util.Optional;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.game.GameHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author sj
 */
@ControllerAdvice(annotations = FrontendController.class)
class FrontendContextAdvice {

	private final GameHolder gameHolder;

	FrontendContextAdvice(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	@ModelAttribute
	FrontendContext createFrontendContext(@PathVariable String gameId, @PathVariable Player player,
			@RequestParam Optional<SystemId> selectedStarId, @RequestParam Optional<FleetId> selectedFleetId) {
		Optional<Game> game = this.gameHolder.get(gameId);

		return game
			.map(g -> FrontendContext.createForGame(gameId, player, g.forPlayer(player), g.forPlayer(player).getView(),
					selectedStarId, selectedFleetId))
			.orElseGet(() -> FrontendContext.createEmpty(gameId, player));

	}

}
