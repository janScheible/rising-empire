package com.scheible.risingempire.game.impl2.game;

import java.math.MathContext;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.navy.ShipMovementSpecsProvider;
import com.scheible.risingempire.game.impl2.technology.ColonyScanSpecsProvider;
import com.scheible.risingempire.game.impl2.universe.Planet;
import com.scheible.risingempire.game.impl2.universe.Star;

/**
 * @author sj
 */
class SystemViewMapper {

	static SystemView toSystemView(Player player, Star star, Planet planet, Optional<Colony> colony,
			ColonyScanSpecsProvider colonyScanSpecsProvider, ShipMovementSpecsProvider shipMovementSpecsProvider) {
		boolean ownColony = colony.filter(c -> c.empire().player() == player).isPresent();
		Parsec nearestColony = new Parsec(42);

		return SystemView.builder()
			.id(SystemIdMapper.toSystemId(star.position()))
			.justExplored(false)
			.location(LocationMapper.toLocation(star.position()))
			.starType(star.type())
			.small(star.small())
			.homeSystem(colony.isPresent())
			.nearestColony(Optional.ofNullable(nearestColony.quantity().intValue()))
			.planetType(Optional.of(planet.type()))
			.planetSpecial(Optional.of(planet.planetSpecial()))
			.seenInTurn(Optional.of(1))
			.starName(Optional.of(star.name()))
			.planetMaxPopulation(Optional.of(100))
			.colony(colony.map(c -> new ColonyView(SystemIdMapper.toSystemId(star.position()).toColonyId(),
					c.empire().player(), c.empire().race(), 50,
					Optional.of(new ShipTypeView(new ShipTypeId("ship"), 0, "Ship", ShipSize.MEDIUM, 0)),
					Optional.empty(), Optional.empty(), Map.of(), Optional.empty())))
			.fleetRange(Optional
				.ofNullable(ownColony ? LocationMapper.toLocationValue(shipMovementSpecsProvider.range(player)) : null))
			.extendedFleetRange(Optional.ofNullable(
					ownColony ? LocationMapper.toLocationValue(shipMovementSpecsProvider.extendedRange(player)) : null))
			.scannerRange(Optional.ofNullable(
					ownColony ? LocationMapper.toLocationValue(colonyScanSpecsProvider.colonyScanRange(player)) : null))
			.colonizable(false)
			.colonizeCommand(false)
			.notifications(Set.of())
			.build();
	}

}
