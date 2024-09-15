package com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.CombatantShipSpecsView;
import com.scheible.risingempire.game.api.view.spacecombat.FireExchangeView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.CombatOutcomeDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.CombatOutcomeDto.OutcomeDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.CombatantShipSpecsDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.FireExchangeDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.ShipsDto;
import com.scheible.risingempire.webapp.hypermedia.Action;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author sj
 */
@FrontendController
class SpaceCombatPageController {

	@GetMapping(path = "/space-combat-page/{currentSpaceCombatSystemId}")
	EntityModel<SpaceCombatPageDto> spaceCombatPage(@ModelAttribute FrontendContext context,
			@PathVariable String currentSpaceCombatSystemId, @RequestParam String selectedStarId) {
		SpaceCombatView spaceCombatView = context.getGameView()
			.spaceCombats()
			.stream()
			.filter(sc -> sc.systemId().value().equals(currentSpaceCombatSystemId))
			.findAny()
			.orElseThrow();

		return new EntityModel<>(new SpaceCombatPageDto(
				context.getGameView().system(new SystemId(currentSpaceCombatSystemId)).starName().orElseThrow(),
				spaceCombatView.attacker(),
				toCombatantShipSpecsDtos(spaceCombatView.attackerPlayer(), spaceCombatView.attackerShipSpecs()),
				spaceCombatView.defender(),
				toCombatantShipSpecsDtos(spaceCombatView.defenderPlayer(), spaceCombatView.defenderShipSpecs()),
				spaceCombatView.fireExchangeCount(),
				new CombatOutcomeDto(OutcomeDto.toOutcomeDto(context.getPlayer(), spaceCombatView.attackerPlayer(),
						spaceCombatView.defenderPlayer(), spaceCombatView.outcome()))))
			.with(Action.get("continue", context.toFrontendUri("main-page"))
				.with(new ActionField("selectedStarId", selectedStarId)));
	}

	List<CombatantShipSpecsDto> toCombatantShipSpecsDtos(Player player,
			Collection<CombatantShipSpecsView> combatantShipSpecs) {
		return combatantShipSpecs.stream().map(css -> {
			return new CombatantShipSpecsDto(css.id().value(), css.name(), css.shield(), css.beamDefence(),
					css.attackLevel(), css.missleDefence(), css.hits(), css.speed(), css.equipment(),
					new ShipsDto(css.count(), css.previousCount(), css.size(), player),
					css.fireExchanges()
						.stream()
						.collect(Collectors.toMap(FireExchangeView::round,
								fe -> new FireExchangeDto(fe.lostHitPoints(), fe.damage(), fe.count()))));
		}).collect(Collectors.toList());
	}

}
