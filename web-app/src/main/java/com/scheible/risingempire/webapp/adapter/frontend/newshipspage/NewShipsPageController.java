package com.scheible.risingempire.webapp.adapter.frontend.newshipspage;

import java.util.Comparator;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.newshipspage.NewShipsPageDto.NewShipDto;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author sj
 */
@FrontendController
public class NewShipsPageController {

	@GetMapping(path = "/new-ships-page")
	EntityModel<NewShipsPageDto> selectTechPage(@ModelAttribute FrontendContext context,
			@RequestParam String selectedStarId) {
		GameView gameView = context.getGameView();

		return new EntityModel<>(new NewShipsPageDto(gameView.player(), gameView.round(),
				gameView.newShips()
					.entrySet()
					.stream()
					.map(e -> new NewShipDto(e.getKey().name(), e.getKey().size(), e.getValue()))
					.sorted(Comparator.comparing(NewShipDto::name))
					.toList()))
			.with(Action.get("continue", context.toFrontendUri("main-page"))
				.with(new ActionField("selectedStarId", selectedStarId)));
	}

}
