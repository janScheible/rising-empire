package com.scheible.risingempire.game.impl2.view;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.AllocationView;
import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.colony.SpaceDockView;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.system.SystemView;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.empire.Empires;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemIntelligence;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemReconReport;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemReconReport.ColonyReconReport;
import com.scheible.risingempire.game.impl2.intelligence.system.SystemReconReport.PlanetReconReport;
import com.scheible.risingempire.game.impl2.military.Military;
import com.scheible.risingempire.game.impl2.ship.Shipyard;
import com.scheible.risingempire.game.impl2.spaceforce.SpaceForce;
import com.scheible.risingempire.game.impl2.technology.Technology;
import com.scheible.risingempire.game.impl2.universe.Planet;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.Universe;

/**
 * @author sj
 */
public class SystemViewMapper {

	public static SystemView toSystemView(Round round, Player player, Star star, Planet planet, Technology technology,
			Universe universe, Colonization colonization, SystemIntelligence systemIntelligence, Military military,
			Empires empires, SpaceForce spaceForce, Shipyard shipyard) {
		Optional<Colony> colony = colonization.colony(star.position());
		Predicate<Star> starHasOwnColony = s -> colonization.colony(s.position())
			.filter(c -> c.player() == player)
			.isPresent();
		boolean ownColony = starHasOwnColony.test(star);

		Optional<Integer> closestColony = Optional.ofNullable(universe.closest(star.position(), starHasOwnColony))
			.map(closest -> universe.distance(star, closest).roundUp());

		SystemReconReport systemReport = ownColony ? new SystemReconReport(true, Optional.empty())
				: systemIntelligence.systemReconReport(player, round.previous(), star.position());
		Optional<PlanetReconReport> planetReport = systemReport.planetReconReport(star.name(), planet.type(),
				planet.special(), planet.max());

		boolean colonizable = colonization.colonizable(player, star.position());

		return SystemView.builder()
			.id(SystemIdMapper.toSystemId(star.position()))
			.justExplored(systemIntelligence.justExplored(player, round.previous(), star.position())
					&& spaceForce.spaceCombat(star.position()).isEmpty() && !colonizable)
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
					.player(systemReport.colonyReconReport().map(ColonyReconReport::player).orElse(c.player()))
					.race(systemReport.colonyReconReport()
						.map(crp -> empires.race(crp.player()))
						.orElse(empires.race(c.player())))
					.population((int) systemReport.colonyReconReport()
						.map(ColonyReconReport::population)
						.orElse(new Population(50))
						.quantity())
					.outdated(systemReport.colonyReconReport().map(ColonyReconReport::outdated).orElse(Boolean.FALSE))
					.spaceDock(starHasOwnColony.test(star)
							? Optional.of(shipyard.design(c.player(), c.spaceDock().current()))
								.map(design -> SpaceDockView.builder()
									.current(ShipTypeView.builder()
										.id(new ShipTypeId(design.id().value()))
										.index(design.index())
										.name(design.name())
										.size(design.size())
										.look(design.look())
										.build())
									.count(c.spaceDock().output().nextRoundCount())
									.build())
							: Optional.empty())
					.allocations(starHasOwnColony.test(star) ? Optional.of(Map.of( //
							ProductionArea.DEFENCE, new AllocationView(0, "None"), //
							ProductionArea.ECOLOGY, new AllocationView(25, "Clean"), //
							ProductionArea.INDUSTRY, new AllocationView(0, "None"), //
							ProductionArea.SHIP,
							new AllocationView(75, c.spaceDock().output().duration().quantity() + " r"), //
							ProductionArea.TECHNOLOGY, new AllocationView(0, "0 RP"))) : Optional.empty())
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
			.colonizable(colonizable)
			.colonizeCommand(colonization.colonizeCommand(player, star.position()))
			.notifications(Set.of())
			.build();
	}

}
