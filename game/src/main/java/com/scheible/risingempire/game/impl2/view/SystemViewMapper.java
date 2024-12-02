package com.scheible.risingempire.game.impl2.view;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.technology.Technology;
import com.scheible.risingempire.game.impl2.universe.Planet;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.Universe;

/**
 * @author sj
 */
public class SystemViewMapper {

	public static SystemView toSystemView(Player player, Star star, Planet planet, Technology technology,
			Universe universe, Colonization colonization) {
		Optional<Colony> colony = colonization.colony(star.position());
		Predicate<Star> starHasOwnColony = s -> colonization.colony(s.position())
			.filter(c -> c.empire().player() == player)
			.isPresent();
		boolean ownColony = starHasOwnColony.test(star);

		Optional<Integer> closestColony = Optional.ofNullable(universe.closest(star.position(), starHasOwnColony))
			.map(closest -> universe.distance(star, closest).roundUp());

		return SystemView.builder()
			.id(SystemIdMapper.toSystemId(star.position()))
			.justExplored(false)
			.location(LocationMapper.toLocation(star.position()))
			.starType(star.type())
			.small(star.small())
			.homeSystem(colony.isPresent())
			.closestColony(closestColony)
			.planetType(Optional.of(planet.type()))
			.planetSpecial(Optional.of(planet.planetSpecial()))
			.seenInTurn(Optional.of(1))
			.starName(Optional.of(star.name()))
			.planetMaxPopulation(Optional.of(100))
			.colony(colony.map(c -> new ColonyView(SystemIdMapper.toSystemId(star.position()).toColonyId(),
					c.empire().player(), c.empire().race(), 50,
					Optional.of(new ShipTypeView(new ShipTypeId("ship"), 0, "Ship", ShipSize.MEDIUM, 0)),
					Optional.empty(), Optional.empty(), Map.of(), Optional.empty())))
			.fleetRange(
					Optional.ofNullable(ownColony ? LocationMapper.toLocationValue(technology.range(player)) : null))
			.extendedFleetRange(Optional
				.ofNullable(ownColony ? LocationMapper.toLocationValue(technology.extendedRange(player)) : null))
			.scannerRange(Optional
				.ofNullable(ownColony ? LocationMapper.toLocationValue(technology.colonyScanRange(player)) : null))
			.colonizable(false)
			.colonizeCommand(false)
			.notifications(Set.of())
			.build();
	}

}
