package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

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

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
class InspectorDto {

	SystemDetailsDto systemDetails;

	EntityModel<ExplorationDto> exploration;

	EntityModel<ColonizationDto> colonization;

	EntityModel<AnnexationDto> annexation;

	EntityModel<FleetDeploymentDto> fleetDeployment;

	EntityModel<FleetViewDto> fleetView;

	EntityModel<SpaceCombatDto> spaceCombat;

	UnexploredDto unexplored;

	static class SystemNameDto {

		final String name;

		SystemNameDto(String name) {
			this.name = name;
		}

	}

	static class SystemDetailsDto {

		final SystemNameDto systemName;

		final HabitabilityDto habitability;

		Optional<ColonyDto> colony;

		Optional<EntityModel<AllocationsDto>> allocations;

		Optional<EntityModel<BuildQueueDto>> buildQueue;

		Optional<Integer> range;

		SystemDetailsDto(String name, HabitabilityDto habitability, Optional<ColonyDto> colony,
				Optional<EntityModel<AllocationsDto>> allocations, Optional<EntityModel<BuildQueueDto>> buildQueue,
				Optional<Integer> range) {
			this.systemName = new SystemNameDto(name);
			this.habitability = habitability;
			this.colony = colony;
			this.allocations = allocations;
			this.buildQueue = buildQueue;
			this.range = range;
		}

	}

	static class FleetDeploymentDto {

		final String fleetId;

		final int round;

		final PlayerDto playerColor;

		final Integer eta;

		final Integer outOfRangeBy;

		final boolean deployable;

		final List<ShipsDto> ships;

		FleetDeploymentDto(String fleetId, int round, Player player, Integer eta, Integer outOfRangeBy,
				boolean deployable, List<ShipsDto> ships) {
			this.fleetId = fleetId;
			this.round = round;
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

		FleetViewDto(Player player, Race race, Integer eta, List<ShipsDto> ships) {
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

		ShipsDto(String id, String name, ShipSize size, int count, Integer maxCount) {
			this.id = id;
			this.name = name;
			this.size = size;
			this.count = count;
			this.maxCount = maxCount;
		}

		@Override
		public boolean equals(Object obj) {
			return Objects2.equals(this, obj,
					other -> Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name));
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.id, this.name);
		}

		@Override
		public String toString() {
			return this.name;
		}

	}

	static class ExplorationDto {

		final SystemNameDto systemName;

		final HabitabilityDto habitability;

		ExplorationDto(String systemName, HabitabilityDto habitability) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
		}

	}

	static class ColonizationDto {

		final SystemNameDto systemName;

		final HabitabilityDto habitability;

		final Optional<Boolean> colonizeCommand;

		ColonizationDto(String systemName, HabitabilityDto habitability, Optional<Boolean> colonizeCommand) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
			this.colonizeCommand = colonizeCommand;
		}

	}

	static class AnnexationDto {

		final SystemNameDto systemName;

		final HabitabilityDto habitability;

		final Optional<Boolean> annexCommand;

		AnnexationDto(String systemName, HabitabilityDto habitability, Optional<Boolean> annexCommand) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
			this.annexCommand = annexCommand;
		}

	}

	static class SpaceCombatSystem {

		final SystemNameDto systemName;

		final HabitabilityDto habitability;

		SpaceCombatSystem(String systemName, HabitabilityDto habitability) {
			this.systemName = new SystemNameDto(systemName);
			this.habitability = habitability;
		}

	}

	static class SpaceCombatDto {

		Optional<SystemNameDto> systemName;

		Optional<HabitabilityDto> habitability;

		final RaceDto attackerRace;

		final PlayerDto attackerColor;

		final RaceDto defenderRace;

		final PlayerDto defenderColor;

		SpaceCombatDto(Optional<SpaceCombatSystem> spaceCombatSystem, Race attackerRace, Player attackerPlayer,
				Race defenderRace, Player defenderPlayer) {
			this.systemName = spaceCombatSystem.map(scs -> scs.systemName);
			this.habitability = spaceCombatSystem.map(scs -> scs.habitability);

			this.attackerRace = RaceDto.fromRace(attackerRace);
			this.attackerColor = PlayerDto.fromPlayer(attackerPlayer);

			this.defenderRace = RaceDto.fromRace(defenderRace);
			this.defenderColor = PlayerDto.fromPlayer(defenderPlayer);
		}

	}

	static class UnexploredDto {

		final StarType starType;

		final int range;

		UnexploredDto(StarType starType, int range) {
			this.starType = starType;
			this.range = range;
		}

	}

	static class BuildQueueDto {

		final String name;

		final ShipSize size;

		final PlayerDto playerColor;

		final int count;

		BuildQueueDto(String name, ShipSize size, Player player, int count) {
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

		HabitabilityDto(PlanetType type, PlanetSpecial special, int maxPopulation) {
			this.type = type;
			this.special = special;
			this.maxPopulation = maxPopulation;
		}

	}

	static class ProductionDto {

		final int net;

		final int gross;

		ProductionDto(int net, int gross) {
			this.net = net;
			this.gross = gross;
		}

	}

	static class ForeignColonyOwner {

		final Race race;

		final Player playerColor;

		ForeignColonyOwner(Race race, Player player) {
			this.race = race;
			this.playerColor = player;
		}

	}

	static class ColonyDto {

		Optional<RaceDto> race;

		Optional<PlayerDto> playerColor;

		final int population;

		final int bases = 0;

		Optional<ProductionDto> production;

		Optional<Integer> roundsUntilAnnexable;

		Optional<PlayerDto> siegePlayerColor;

		Optional<RaceDto> siegeRace;

		ColonyDto(Optional<ForeignColonyOwner> foreignColonyOwner, int population, Optional<ProductionDto> production,
				Optional<Integer> roundsUntilAnnexable, Optional<Player> siegePlayer, Optional<Race> siegeRace) {
			this.playerColor = foreignColonyOwner.map(co -> PlayerDto.fromPlayer(co.playerColor));
			this.race = foreignColonyOwner.map(co -> RaceDto.fromRace(co.race));
			this.population = population;
			this.production = production;

			this.roundsUntilAnnexable = roundsUntilAnnexable;
			this.siegePlayerColor = siegePlayer.map(PlayerDto::fromPlayer);
			this.siegeRace = siegeRace.map(RaceDto::fromRace);
		}

	}

}
