package com.scheible.risingempire.webapp.adapter.frontend.victorydefeatpage;

import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author sj
 */
@FrontendController
public class VictoryDefeatPageController {

	@GetMapping(path = "/victory-defeat-page")
	VictoryDefeatPageDto selectTechPage(@ModelAttribute FrontendContext context) {
		return new VictoryDefeatPageDto(context.getGameView().victory());
	}

}
