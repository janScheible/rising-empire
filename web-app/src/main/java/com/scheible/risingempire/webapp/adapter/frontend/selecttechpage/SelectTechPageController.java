package com.scheible.risingempire.webapp.adapter.frontend.selecttechpage;

import static java.util.Collections.emptyList;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.notification.SystemNotificationView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.TurnSteps;
import com.scheible.risingempire.webapp.adapter.frontend.selecttechpage.SelectTechPageDto.TechDto;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
@FrontendController
class SelectTechPageController {

	@GetMapping(path = "/select-tech-page")
	SelectTechPageDto selectTechPage(@ModelAttribute final FrontendContext context) {
		final Set<TechGroupView> selectTechs = context.getGameView().getSelectTechs();
		final GameView gameView = context.getGameView();

		List<EntityModel<TechDto>> selectTechsEntities = emptyList();
		if (!selectTechs.isEmpty()) {
			selectTechsEntities = selectTechs.iterator().next().stream()
					.map(tech -> new EntityModel<>(new TechDto(tech.getId().getValue(), tech.getName())).with(Action
							.jsonPost("select", context.toFrontendUri("select-tech-page", "selects"))
							.with("technologyId", tech.getId().getValue()) //
							.with("selectedStarId", context.getSelectedStarId().get().getValue()) // CPD-OFF
							.with(gameView.getJustExploredSystemIds().stream()
									.map(esId -> new ActionField("exploredSystemId", esId.getValue())))
							.with(gameView.getColonizableSystemIds().stream()
									.map(csId -> new ActionField("colonizableSystemId", csId.getValue())))
							.with(gameView.getAnnexableSystemIds().stream()
									.map(asId -> new ActionField("annexableSystemId", asId.getValue())))
							.with(gameView.getSystemNotifications().stream().map(SystemNotificationView::getSystemId)
									.map(nsId -> new ActionField("notificationSystemId", nsId.getValue()))))) // CPD-ON
					.collect(Collectors.toList());
		}

		return new SelectTechPageDto(selectTechsEntities);
	}

	static class SelectTechBodyDto {

		TechId technologyId;
		SystemId selectedStarId;
		Optional<List<String>> exploredSystemId = Optional.empty();
		Optional<List<String>> colonizableSystemId = Optional.empty();
		Optional<List<String>> annexableSystemId = Optional.empty();
		Optional<List<String>> notificationSystemId = Optional.empty();
	}

	@PostMapping(path = "/select-tech-page/selects", consumes = APPLICATION_JSON_VALUE)
	@SuppressFBWarnings(value = "SPRING_FILE_DISCLOSURE", justification = "Controlled redirect... ;-)")
	ResponseEntity<Void> selectTech(@ModelAttribute final FrontendContext context,
			@RequestBody final SelectTechBodyDto body) {
		context.getPlayerGame().selectTech(body.technologyId);

		return TurnSteps.getMainPageRedirect(context, body.selectedStarId, Optional.empty(), body.exploredSystemId,
				body.colonizableSystemId, body.annexableSystemId, body.notificationSystemId,
				!context.getPlayerGame().getView().getSelectTechs().isEmpty());
	}
}
