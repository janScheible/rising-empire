package com.scheible.risingempire.game.impl2.view;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.intelligence.Intelligence;
import com.scheible.risingempire.game.impl2.intelligence.SystemReconReport;
import com.scheible.risingempire.game.impl2.intelligence.SystemReconReport.ColonyReconReport;
import com.scheible.risingempire.game.impl2.intelligence.SystemReconReport.PlanetReconReport;
import com.scheible.risingempire.game.impl2.military.Military;
import com.scheible.risingempire.game.impl2.technology.Technology;
import com.scheible.risingempire.game.impl2.universe.Planet;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.Universe;

/**
 * @author sj
 */
public class SystemViewMapper {

	public static SystemView toSystemView(Player player, Star star, Planet planet, Technology technology,
			Universe universe, Colonization colonization, Intelligence intelligence, Military military) {
		Optional<Colony> colony = colonization.colony(star.position());
		Predicate<Star> starHasOwnColony = s -> colonization.colony(s.position())
			.filter(c -> c.empire().player() == player)
			.isPresent();
		boolean ownColony = starHasOwnColony.test(star);

		Optional<Integer> closestColony = Optional.ofNullable(universe.closest(star.position(), starHasOwnColony))
			.map(closest -> universe.distance(star, closest).roundUp());

		SystemReconReport systemReport = intelligence.systemReconReport(star.position());
		Optional<PlanetReconReport> planetReport = systemReport.planetReconReport(star.name(), planet.type(),
				planet.special(), planet.max());

		return SystemView.builder()
			.id(SystemIdMapper.toSystemId(star.position()))
			.justExplored(intelligence.justExplored(star.position()))
			.location(LocationMapper.toLocation(star.position()))
			.starType(star.type())
			.small(star.small())
			.homeSystem(colonization.homeSystem(player, star.position()))
			.closestColony(closestColony)
			.planetType(planetReport.map(PlanetReconReport::planetType))
			.planetSpecial(planetReport.map(PlanetReconReport::planetSpecial))
			.starName(planetReport.map(PlanetReconReport::starName))
			.planetMaxPopulation(
					planetReport.map(PlanetReconReport::planetMapPopulation).map(mp -> (int) mp.quantity()))
			.colony(colony.filter(c -> starHasOwnColony.test(star) || systemReport.colonyReconReport().isPresent())
				.map(c -> ColonyView.builder()
					.id(SystemIdMapper.toSystemId(star.position()).toColonyId())
					.player(systemReport.colonyReconReport().map(ColonyReconReport::player).orElse(c.empire().player()))
					.race(systemReport.colonyReconReport().map(ColonyReconReport::race).orElse(c.empire().race()))
					.population((int) systemReport.colonyReconReport()
						.map(ColonyReconReport::population)
						.orElse(new Population(50))
						.quantity())
					.spaceDock(starHasOwnColony.test(star) ? Optional.of(ShipTypeView.builder()
						.id(new ShipTypeId("ship"))
						.index(0)
						.name("Ship")
						.size(ShipSize.MEDIUM)
						.look(0)
						.build()) : Optional.empty())
					.ratios(starHasOwnColony.test(star) ? Optional.of(Map.of(//
							ProductionArea.DEFENCE, 20, //
							ProductionArea.ECOLOGY, 20, //
							ProductionArea.INDUSTRY, 20, //
							ProductionArea.SHIP, 20, //
							ProductionArea.TECHNOLOGY, 20)) : Optional.empty())
					.annexationStatus(military.annexationStatus(star.position()))
					.colonistTransfers(Map.of())
					.relocationTarget(Optional.empty())
					.build()))
			.fleetRange(
					Optional.ofNullable(ownColony ? LocationMapper.toLocationValue(technology.range(player)) : null))
			.extendedFleetRange(Optional
				.ofNullable(ownColony ? LocationMapper.toLocationValue(technology.extendedRange(player)) : null))
			.scannerRange(Optional
				.ofNullable(ownColony ? LocationMapper.toLocationValue(technology.colonyScanRange(player)) : null))
			.colonizable(colonization.colonizable(player, star.position()))
			.colonizeCommand(colonization.colonizeCommand(player, star.position()))
			.notifications(Set.of())
			.build();
	}

}
