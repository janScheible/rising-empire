package com.scheible.risingempire.webapp.adapter.frontend.mainpage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
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

	EntityModel<TransportsDto> transports;

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

		final Optional<ColonyDto> colony;

		final Optional<EntityModel<AllocationsDto>> allocations;

		final Optional<EntityModel<BuildQueueDto>> buildQueue;

		final Optional<EntityModel<TransferColonistsDto>> transferColonists;

		final Optional<EntityModel<RelocateShipsDto>> relocateShips;

		final Optional<Integer> range;

		SystemDetailsDto(String name, HabitabilityDto habitability, Optional<ColonyDto> colony,
				Optional<EntityModel<AllocationsDto>> allocations, Optional<EntityModel<BuildQueueDto>> buildQueue,
				Optional<EntityModel<TransferColonistsDto>> transferColonists,
				Optional<EntityModel<RelocateShipsDto>> relocateShips, Optional<Integer> range) {
			this.systemName = new SystemNameDto(name);
			this.habitability = habitability;
			this.colony = colony;
			this.allocations = allocations;
			this.buildQueue = buildQueue;
			this.transferColonists = transferColonists;
			this.relocateShips = relocateShips;
			this.range = range;
		}

	}

	static class FleetDeploymentDto {

		final String fleetId;

		final int round;

		final PlayerDto playerColor;

		final Optional<Integer> eta;

		final Optional<Integer> outOfRangeBy;

		final boolean deployable;

		final List<ShipsDto> ships;

		FleetDeploymentDto(FleetId fleetId, int round, Player player, Optional<Integer> eta,
				Optional<Integer> outOfRangeBy, boolean deployable, List<ShipsDto> ships) {
			this.fleetId = fleetId.value();
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

		final Optional<Integer> eta;

		final List<ShipsDto> ships;

		FleetViewDto(Player player, Race race, Optional<Integer> eta, List<ShipsDto> ships) {
			this.playerColor = PlayerDto.fromPlayer(player);
			this.race = RaceDto.fromRace(race);
			this.eta = eta;
			this.ships = ships;
		}

	}

	static class TransportsDto {

		final PlayerDto playerColor;

		final RaceDto race;

		final int transports;

		final Optional<Integer> eta;

		TransportsDto(Player player, Race race, int transports, Optional<Integer> eta) {
			this.playerColor = PlayerDto.fromPlayer(player);
			this.race = RaceDto.fromRace(race);
			this.transports = transports;
			this.eta = eta;
		}

	}

	static class ShipsDto {

		final String id;

		final String name;

		final ShipSize size;

		final int count;

		final Optional<Integer> maxCount;

		ShipsDto(String id, String name, ShipSize size, int count, Optional<Integer> maxCount) {
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

		final Optional<SystemNameDto> systemName;

		final Optional<HabitabilityDto> habitability;

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

	static class TransferColonistsDto {

		final int colonists;

		final int maxColonists;

		final int warningThreshold;

		final Optional<Integer> eta;

		TransferColonistsDto(int colonists, int maxColonists, int warningThreshold, Optional<Integer> eta) {
			this.colonists = colonists;
			this.maxColonists = maxColonists;
			this.warningThreshold = warningThreshold;
			this.eta = eta;
		}

	}

	static class RelocateShipsDto {

		final Optional<Integer> delay;

		RelocateShipsDto(Optional<Integer> delay) {
			this.delay = delay;
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

		final int net = 0; // not yet implemented

		final int gross = 0; // not yet implemented

	}

	static class ForeignColonyOwner {

		final Race race;

		final PlayerDto playerColor;

		ForeignColonyOwner(Race race, Player player) {
			this.race = race;
			this.playerColor = PlayerDto.fromPlayer(player);
		}

	}

	static class ColonyDto {

		final Optional<RaceDto> race;

		final Optional<PlayerDto> playerColor;

		final int population;

		final boolean outdated;

		final int bases = 0; // not yet implemented

		final Optional<ProductionDto> production;

		final Optional<Integer> roundsUntilAnnexable;

		final Optional<PlayerDto> siegePlayerColor;

		final Optional<RaceDto> siegeRace;

		ColonyDto(Optional<ForeignColonyOwner> foreignColonyOwner, int population, boolean outdated,
				Optional<ProductionDto> production, Optional<Integer> roundsUntilAnnexable,
				Optional<Player> siegePlayer, Optional<Race> siegeRace) {
			this.playerColor = foreignColonyOwner.map(fco -> fco.playerColor);
			this.race = foreignColonyOwner.map(co -> RaceDto.fromRace(co.race));
			this.population = population;
			this.outdated = outdated;
			this.production = production;

			this.roundsUntilAnnexable = roundsUntilAnnexable;
			this.siegePlayerColor = siegePlayer.map(PlayerDto::fromPlayer);
			this.siegeRace = siegeRace.map(RaceDto::fromRace);
		}

	}

}
