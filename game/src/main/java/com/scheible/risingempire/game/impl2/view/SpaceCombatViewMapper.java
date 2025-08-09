package com.scheible.risingempire.game.impl2.view;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.spacecombat.CombatantShipSpecsView;
import com.scheible.risingempire.game.api.view.spacecombat.FireExchangeView;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.shipyard.ShipDesign;
import com.scheible.risingempire.game.impl2.spaceforce.combat.FireExchange;

/**
 * @author sj
 */
public class SpaceCombatViewMapper {

	public static CombatantShipSpecsView toCombatantShipSpecsView(Player player, ShipClassId shipClassId, int count,
			int previousCount, List<FireExchange> fireExchanges, ShipDesign design, boolean shipSpecsAvailable) {
		return CombatantShipSpecsView.builder()
			.id(new ShipTypeId(shipClassId.value()))
			.name(design.name())
			.count(count)
			.previousCount(previousCount)
			.size(design.size())
			.shield(shipSpecsAvailable ? Optional.of(design.shield().level()) : Optional.empty())
			.beamDefence(shipSpecsAvailable ? Optional.of(design.beamDefence()) : Optional.empty())
			.attackLevel(shipSpecsAvailable ? Optional.of(design.attackLevel()) : Optional.empty())
			.warp(shipSpecsAvailable ? Optional.of(design.engine().warp()) : Optional.empty())
			.missleDefence(shipSpecsAvailable ? Optional.of(design.missileDefence()) : Optional.empty())
			.hits(shipSpecsAvailable ? Optional.of(design.hitPoints()) : Optional.empty())
			.speed(shipSpecsAvailable ? Optional.of(design.combatSpeed()) : Optional.empty())
			.equipment(design.specials()
				.stream()
				.map(s -> Stream.of(s.getClass().getSimpleName().split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
					.collect(Collectors.joining(" ")))
				.toList())
			.fireExchanges(fireExchanges.stream()
				.map(fireExchange -> FireExchangeView.builder()
					.round(fireExchange.round())
					.lostHitPoints(fireExchange.lostHitPoints())
					.damage(fireExchange.damage())
					.count(fireExchange.shipCount())
					.build())
				.toList())
			.build();
	}

}
