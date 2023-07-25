package com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.scheible.risingempire.game.api.view.spacecombat.CombatantShipSpecsView;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.webapp.adapter.frontend.annotation.FrontendController;
import com.scheible.risingempire.webapp.adapter.frontend.context.FrontendContext;
import com.scheible.risingempire.webapp.adapter.frontend.mainpage.TurnSteps;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.CombatOutcomeDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.CombatOutcomeDto.OutcomeDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.CombatantShipSpecsDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.FireExchangeDto;
import com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage.SpaceCombatPageDto.ShipsDto;
import com.scheible.risingempire.webapp.hypermedia.ActionField;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

/**
 *
 * @author sj
 */
@FrontendController
class SpaceCombatPageController {

	@GetMapping(path = "/space-combat-page/{currentSpaceCombatSystemId}")
	EntityModel<SpaceCombatPageDto> spaceCombatPage(@ModelAttribute final FrontendContext context,
			@PathVariable final String currentSpaceCombatSystemId,
			@RequestParam(name = "spaceCombatSystemId") final Optional<List<String>> spaceCombatSystemIds,
			@RequestParam(name = "exploredSystemId") final Optional<List<String>> exploredSystemIds,
			@RequestParam(name = "colonizableSystemId") final Optional<List<String>> colonizableSystemIds,
			@RequestParam(name = "notificationSystemId") final Optional<List<String>> notificationSystemIds) {
		final SpaceCombatView spaceCombatView = context.getGameView().getSpaceCombats().stream()
				.filter(sc -> sc.getSystemId().getValue().equals(currentSpaceCombatSystemId)).findAny().orElseThrow();

		return new EntityModel<>(new SpaceCombatPageDto(
				context.getGameView().getSystem(new SystemId(currentSpaceCombatSystemId)).getStarName().orElseThrow(),
				spaceCombatView.getAttacker(),
				toCombatantShipSpecsDtos(spaceCombatView.getAttackerPlayer(), spaceCombatView.getAttackerShipSpecs()),
				spaceCombatView.getDefender(),
				toCombatantShipSpecsDtos(spaceCombatView.getDefenderPlayer(), spaceCombatView.getDefenderShipSpecs()),
				spaceCombatView.getFireExchangeCount(),
				new CombatOutcomeDto(OutcomeDto.toOutcomeDto(context.getPlayer(), spaceCombatView.getAttackerPlayer(),
						spaceCombatView.getDefenderPlayer(), spaceCombatView.getOutcome())))).with(TurnSteps
								.getMainPageAction(context, "continue", context.getSelectedStarId().get(),
										spaceCombatSystemIds, exploredSystemIds, colonizableSystemIds,
										notificationSystemIds, !context.getGameView().getSelectTechs().isEmpty())
								.with(new ActionField("currentSpaceCombatSystemId", currentSpaceCombatSystemId)));
	}

	List<CombatantShipSpecsDto> toCombatantShipSpecsDtos(final Player player,
			final Collection<CombatantShipSpecsView> combatantShipSpecs) {
		return combatantShipSpecs.stream().map(css -> {
			return new CombatantShipSpecsDto(css.getId().getValue(), css.getName(), css.getShield().orElse(null),
					css.getBeamDefence().orElse(null), css.getAttackLevel().orElse(null),
					css.getMissleDefence().orElse(null), css.getHits().orElse(null), css.getSpeed().orElse(null),
					css.getEquipment(), new ShipsDto(css.getCount(), css.getPreviousCount(), css.getSize(), player),
					css.getFireExchanges().stream().collect(Collectors.toMap(fe -> fe.getRound(),
							fe -> new FireExchangeDto(fe.getLostHitPoints(), fe.getDamage(), fe.getCount()))));
		}).collect(Collectors.toList());
	}
}