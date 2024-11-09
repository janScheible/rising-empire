package com.scheible.risingempire.game.impl2.game;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.api.view.system.SystemViewBuilder;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.intelligence.ColonyScanSpecsProvider;
import com.scheible.risingempire.game.impl2.navy.ShipSpecsProvider;
import com.scheible.risingempire.game.impl2.universe.Planet;
import com.scheible.risingempire.game.impl2.universe.Star;

/**
 * @author sj
 */
class SystemViewMapper {

	static SystemView toSystemView(Player player, Star star, Planet planet, Optional<Colony> colony,
			ColonyScanSpecsProvider colonyScanSpecsProvider, ShipSpecsProvider shipSpecsProvider) {
		return SystemViewBuilder.builder()
			.id(SystemIdMapper.toSystemId(star.position()))
			.justExplored(false)
			.location(LocationMapper.toLocation(star.position()))
			.starType(star.type())
			.small(star.name().contains("a"))
			.homeSystem(colony.isPresent())
			.range(Optional.ofNullable(colony.isPresent()
					? LocationMapper.toLocationValue(colonyScanSpecsProvider.scanRange(player)) : null))
			.planetType(Optional.of(planet.type()))
			.planetSpecial(Optional.of(planet.planetSpecial()))
			.seenInTurn(Optional.of(1))
			.starName(Optional.of(star.name()))
			.planetMaxPopulation(Optional.of(100))
			.colony(colony.map(c -> new ColonyView(SystemIdMapper.toSystemId(star.position()).toColonyId(),
					c.empire().player(), c.empire().race(), 50,
					Optional.of(new ShipTypeView(new ShipTypeId("ship"), 0, "Ship", ShipSize.MEDIUM, 0)),
					Optional.empty(), Optional.empty(), Map.of(), Optional.empty())))
			.fleetRange(Optional.ofNullable(
					colony.isPresent() ? LocationMapper.toLocationValue(shipSpecsProvider.range(player)) : null))
			.extendedFleetRange(Optional.ofNullable(colony.isPresent()
					? LocationMapper.toLocationValue(shipSpecsProvider.extendedRange(player)) : null))
			.scannerRange(Optional.ofNullable(colony.isPresent()
					? LocationMapper.toLocationValue(colonyScanSpecsProvider.scanRange(player)) : null))
			.colonizable(Optional.of(false))
			.colonizeCommand(Optional.of(false))
			.build();
	}

}
