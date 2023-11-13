package com.scheible.risingempire.webapp.adapter.frontend.techpage;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.dto.AllocationCategoryDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.AllocationsDto;
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
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sj
 */
@FrontendController
class TechPageController {

	@GetMapping(path = "/tech-page")
	EntityModel<TechPageDto> techPage(@ModelAttribute final FrontendContext context,
			@RequestParam final Optional<String> lockedCategory) {
		return new EntityModel<>(new TechPageDto(new EntityModel<>(new AllocationsDto(Map.of( //
				"computers", new AllocationCategoryDto(16, "1111RP"), //
				"construction", new AllocationCategoryDto(17, "2222RP"), //
				"force-fields", new AllocationCategoryDto(16, "3333RP"), //
				"planetology", new AllocationCategoryDto(17, "4444RP"), //
				"propulsion", new AllocationCategoryDto(17, "5555RP"), //
				"weapons", new AllocationCategoryDto(17, "6666RP")), lockedCategory))
			.with(Action.jsonPost("allocate-research", context.toFrontendUri("tech-page", "allocations"))
				.with(context.getSelectedStarId().isPresent(), "selectedStarId",
						() -> context.getSelectedStarId().get().getValue()))))
			.with(Action.get("close", context.toFrontendUri("main-page")) //
				.with(context.getSelectedStarId().isPresent(), "selectedStarId",
						() -> context.getSelectedStarId().get().getValue()));
	}

	static class ResearchAllocationsBodyDto {

		Optional<SystemId> selectedStarId = Optional.empty();

		Optional<String> locked = Optional.empty();

		Optional<Integer> ship = Optional.empty();

		Optional<Integer> defence = Optional.empty();

		Optional<Integer> industry = Optional.empty();

		Optional<Integer> ecology = Optional.empty();

		Optional<Integer> technology = Optional.empty();

	}

	@PostMapping(path = "/tech-page/allocations", consumes = APPLICATION_JSON_VALUE)
	ResponseEntity<Void> allocateResearch(@ModelAttribute final FrontendContext context,
			@RequestBody final ResearchAllocationsBodyDto body) {
		return ResponseEntity.status(HttpStatus.SEE_OTHER)
			.header(HttpHeaders.LOCATION,
					context.toAction(HttpMethod.GET, "tech-page")
						.with(body.locked.isPresent(), "lockedCategory", () -> body.locked.get())
						.with(body.selectedStarId.isPresent(), "selectedStarId",
								() -> body.selectedStarId.get().getValue())
						.toGetUri())
			.build();
	}

}
