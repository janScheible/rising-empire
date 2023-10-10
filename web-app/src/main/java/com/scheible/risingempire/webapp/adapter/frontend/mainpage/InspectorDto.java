package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.util.jdk.Objects2;
import com.scheible.risingempire.webapp.adapter.frontend.dto.AllocationsDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.RaceDto;
import com.scheible.risingempire.webapp.hypermedia.EntityModel;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
class InspectorDto {

	static class SystemNameDto {

		final String name;

		SystemNameDto(final String name) {
			this.name = name;
		}
	}

	static class SystemDetailsDto {

		final SystemNameDto systemName;
		final HabitabilityDto habitability;
		@Nullable
		final ColonyDto colony;
		@Nullable
		final EntityModel<AllocationsDto> allocations;
		@Nullable
		final EntityModel<BuildQueueDto> buildQueue;
		@Nullable
		final Integer range;

		SystemDetailsDto(final String name, final HabitabilityDto habitability, final Optional<ColonyDto> colony,
				final Optional<EntityModel<AllocationsDto>> allocations,
				final Optional<EntityModel<BuildQueueDto>> buildQueue, final Optional<Integer> range) {
			this.systemName = new SystemNameDto(name);
			this.habitability = habitability;
			this.colony = colony.orElse(null);
			this.allocations = allocations.orElse(null);
			this.buildQueue = buildQueue.orElse(null);
			this.range = range.orElse(null);
		}
	}

	static class FleetDeploymentDto {

		final PlayerDto playerColor;
		final Integer eta;
		final Integer outOfRangeBy;
		final boolean deployable;
		final List<ShipsDto> ships;

		FleetDeploymentDto(final Player player, final Integer eta, final Integer outOfRangeBy, final boolean deployable,
				final List<ShipsDto> ships) {
			this.playerColor = PlayerDto.fromPlayer(player);
			this.eta = eta;
			this.outOfRangeBy = outOfRangeBy;
			this.deployable = deployable;
			this.ships = unmodifiableList(ships);
		}
	}

	static class FleetViewDto {

		final PlayerDto playerColor;
		final RaceDto race;
		final Integer eta;
		final List<ShipsDto> ships;

		FleetViewDto(final Player player, final Race race, final Integer eta, final List<ShipsDto> ships) {
			this.playerColor = PlayerDto.fromPlayer(player);
			this.race = RaceDto.fromRace(race);
			this.eta = eta;
			this.ships = ships;
		}
	}

	static class ShipsDto {

		final String id;
		final String name;
		final ShipSize size;
		final int count;
		final Integer maxCount;

		ShipsDto(final String id, final String name, final ShipSize size, final int count, final Integer maxCount) {
			this.id = id;
			this.name = name;
			this.size = size;
			this.count = count;
			this.maxCount = maxCount;
		}

		@Override
		@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
		public boolean equals(final Object obj) {
			return Objects2.equals(this, obj,
					other -> Objects.equals(id, other.id) && Objects.equals(name, other.name));
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, name);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static class ExplorationDto {

		final SystemNameDto systemName;
		final HabitabilityDto habitability;

		ExplorationDto(final String systemName, final HabitabilityDto habitability) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
		}
	}

	static class ColonizationDto {

		final SystemNameDto systemName;
		final HabitabilityDto habitability;
		final Boolean colonizeCommand;

		ColonizationDto(final String systemName, final HabitabilityDto habitability, final Boolean colonizeCommand) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
			this.colonizeCommand = colonizeCommand;
		}
	}

	static class AnnexationDto {

		final SystemNameDto systemName;
		final HabitabilityDto habitability;
		final Boolean annexCommand;

		AnnexationDto(final String systemName, final HabitabilityDto habitability, final Boolean annexCommand) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
			this.annexCommand = annexCommand;
		}
	}

	static class SpaceCombatSystem {

		final SystemNameDto systemName;
		final HabitabilityDto habitability;

		SpaceCombatSystem(final String systemName, final HabitabilityDto habitability) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
		}
	}

	static class SpaceCombatDto {

		@Nullable
		final SystemNameDto systemName;
		@Nullable
		final HabitabilityDto habitability;

		final RaceDto attackerRace;
		final PlayerDto attackerColor;

		final RaceDto defenderRace;
		final PlayerDto defenderColor;

		SpaceCombatDto(final Optional<SpaceCombatSystem> spaceCombatSystem, final Race attackerRace,
				final Player attackerPlayer, final Race defenderRace, final Player defenderPlayer) {
			this.systemName = spaceCombatSystem.map(scs -> scs.systemName).orElse(null);
			this.habitability = spaceCombatSystem.map(scs -> scs.habitability).orElse(null);

			this.attackerRace = RaceDto.fromRace(attackerRace);
			this.attackerColor = PlayerDto.fromPlayer(attackerPlayer);

			this.defenderRace = RaceDto.fromRace(defenderRace);
			this.defenderColor = PlayerDto.fromPlayer(defenderPlayer);
		}
	}

	static class UnexploredDto {

		final StarType starType;
		final int range;

		UnexploredDto(final StarType starType, final int range) {
			this.starType = starType;
			this.range = range;
		}
	}

	static class BuildQueueDto {

		final String name;
		final ShipSize size;
		final PlayerDto playerColor;
		final int count;

		BuildQueueDto(final String name, final ShipSize size, final Player player, final int count) {
			this.name = name;
			this.size = size;
			this.playerColor = PlayerDto.fromPlayer(player);
			this.count = count;
		}
	}

	static class HabitabilityDto {

		final PlanetType type;
		final PlanetSpecial special;
		final int maxPopulation;

		HabitabilityDto(final PlanetType type, final PlanetSpecial special, final int maxPopulation) {
			this.type = type;
			this.special = special;
			this.maxPopulation = maxPopulation;
		}
	}

	static class ProductionDto {
		final int net;
		final int gross;

		ProductionDto(final int net, final int gross) {
			this.net = net;
			this.gross = gross;
		}
	}

	static class ForeignColonyOwner {

		final Race race;
		final Player playerColor;

		ForeignColonyOwner(final Race race, final Player player) {
			this.race = race;
			this.playerColor = player;
		}
	}

	static class ColonyDto {

		@Nullable
		final RaceDto race;
		@Nullable
		final PlayerDto playerColor;
		final int population;
		final int bases = 0;
		final Optional<ProductionDto> production;

		ColonyDto(final Optional<ForeignColonyOwner> foreignColonyOwner, final int population,
				final Optional<ProductionDto> production) {
			this.playerColor = foreignColonyOwner.map(co -> PlayerDto.fromPlayer(co.playerColor)).orElse(null);
			this.race = foreignColonyOwner.map(co -> RaceDto.fromRace(co.race)).orElse(null);
			this.population = population;
			this.production = production;
		}
	}

	SystemDetailsDto systemDetails;
	EntityModel<ExplorationDto> exploration;
	EntityModel<ColonizationDto> colonization;
	EntityModel<AnnexationDto> annexation;
	EntityModel<FleetDeploymentDto> fleetDeployment;
	EntityModel<FleetViewDto> fleetView;
	EntityModel<SpaceCombatDto> spaceCombat;
	UnexploredDto unexplored;
}
