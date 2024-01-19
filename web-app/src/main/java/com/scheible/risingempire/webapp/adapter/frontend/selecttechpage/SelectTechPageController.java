package com.scheible.risingempire.webapp.adapter.frontend.selecttechpage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechGroupView;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.selecttechpage.SelectTechPageDto.TechDto;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sj
 */
@FrontendController
class SelectTechPageController {

	@GetMapping(path = "/select-tech-page")
	SelectTechPageDto selectTechPage(@ModelAttribute FrontendContext context) {
		Set<TechGroupView> selectTechs = context.getGameView().getSelectTechs();

		List<EntityModel<TechDto>> selectTechsEntities = List.of();
		if (!selectTechs.isEmpty()) {
			selectTechsEntities = selectTechs.iterator()
				.next()
				.stream()
				.map(tech -> new EntityModel<>(new TechDto(tech.getId().getValue(), tech.getName()))
					.with(Action.jsonPost("select", context.toFrontendUri("select-tech-page", "selects"))
						.with("technologyId", tech.getId().getValue()) //
						.with("selectedStarId", context.getSelectedStarId().get().getValue())))
				.collect(Collectors.toList());
		}

		return new SelectTechPageDto(selectTechsEntities);
	}

	@PostMapping(path = "/select-tech-page/selects", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> selectTech(@ModelAttribute FrontendContext context, @RequestBody SelectTechBodyDto body) {
		context.getPlayerGame().selectTech(body.technologyId);

		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.withSelectedStar(body.selectedStarId).toAction(HttpMethod.GET, "main-page").toGetUri())
			.build();
	}

	static class SelectTechBodyDto {

		TechId technologyId;

		SystemId selectedStarId;

	}

}
