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
import org.springframework.web.bind.annotation.RequestParam;

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
	SelectTechPageDto selectTechPage(@ModelAttribute final FrontendContext context,
			@RequestParam(name = "exploredSystemId") final Optional<List<String>> exploredSystemIds,
			@RequestParam(name = "colonizableSystemId") final Optional<List<String>> colonizableSystemIds,
			@RequestParam(name = "notificationSystemId") final Optional<List<String>> notificationSystemIds) {
		final Set<TechGroupView> selectTechs = context.getGameView().getSelectTechs();

		List<EntityModel<TechDto>> selectTechsEntities = emptyList();
		if (!selectTechs.isEmpty()) {
			selectTechsEntities = selectTechs.iterator().next().stream()
					.map(tech -> new EntityModel<>(new TechDto(tech.getId().getValue(), tech.getName()))
							.with(Action.jsonPost("select", context.toFrontendUri("select-tech-page", "selects"))
									.with("technologyId", tech.getId().getValue()) //
									.with("selectedStarId", context.getSelectedStarId().get().getValue())
									.with(exploredSystemIds.orElseGet(() -> emptyList()).stream()
											.map(esId -> new ActionField("exploredSystemId", esId)))
									.with(colonizableSystemIds.orElseGet(() -> emptyList()).stream()
											.map(csId -> new ActionField("colonizableSystemId", csId)))
									.with(notificationSystemIds.orElseGet(() -> emptyList()).stream()
											.map(nsId -> new ActionField("notificationSystemId", nsId)))))
					.collect(Collectors.toList());
		}

		return new SelectTechPageDto(selectTechsEntities);
	}

	static class SelectTechBodyDto {

		TechId technologyId;
		SystemId selectedStarId;
		Optional<List<String>> exploredSystemId = Optional.empty();
		Optional<List<String>> colonizableSystemId = Optional.empty();
		Optional<List<String>> notificationSystemId = Optional.empty();
	}

	@PostMapping(path = "/select-tech-page/selects", consumes = APPLICATION_JSON_VALUE)
	@SuppressFBWarnings(value = "SPRING_FILE_DISCLOSURE", justification = "Controlled redirect... ;-)")
	ResponseEntity<Void> selectTech(@ModelAttribute final FrontendContext context,
			@RequestBody final SelectTechBodyDto body) {
		context.getPlayerGame().selectTech(body.technologyId);

		return TurnSteps.getMainPageRedirect(context, body.selectedStarId, Optional.empty(), body.exploredSystemId,
				body.colonizableSystemId, body.notificationSystemId,
				!context.getPlayerGame().getView().getSelectTechs().isEmpty());
	}
}
