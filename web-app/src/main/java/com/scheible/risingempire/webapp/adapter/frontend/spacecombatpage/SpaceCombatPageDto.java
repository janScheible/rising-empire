package com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.RaceDto;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @author sj
 */
class SpaceCombatPageDto {

	static class FireExchangeDto {

		final int lostHitPoints;

		final int damage;

		final int count;

		FireExchangeDto(final int lostHitPoints, final int damage, final int count) {
			this.lostHitPoints = lostHitPoints;
			this.damage = damage;
			this.count = count;
		}

	}

	static class CombatantShipSpecsDto {

		final String id;

		final String name;

		@Nullable
		final Integer shield;

		@Nullable
		final Integer beamDefence;

		@Nullable
		final Integer attackLevel;

		@Nullable
		final Integer damage = 0; // will be updated in the fire exchanges

		@Nullable
		final Integer missleDefence;

		@Nullable
		final Integer hits;

		@Nullable
		final Integer speed;

		@Nullable
		final List<String> equipment;

		final ShipsDto ships;

		final Map<Integer, FireExchangeDto> fireExchanges;

		CombatantShipSpecsDto(final String id, final String name, @Nullable final Integer shield,
				@Nullable final Integer beamDefence, @Nullable final Integer attackLevel,
				@Nullable final Integer missleDefence, @Nullable final Integer hits, @Nullable final Integer speed,
				@Nullable final List<String> equipment, final ShipsDto ships,
				final Map<Integer, FireExchangeDto> fireExchanges) {
			this.id = id;
			this.name = name;

			this.shield = shield;
			this.beamDefence = beamDefence;
			this.attackLevel = attackLevel;
			this.missleDefence = missleDefence;
			this.hits = hits;
			this.speed = speed;

			this.equipment = equipment;

			this.ships = ships;
			this.fireExchanges = fireExchanges;
		}

	}

	static class ShipsDto {

		final int count;

		final int previousCount;

		final ShipSize size;

		final PlayerDto playerColor;

		ShipsDto(final int count, final int previousCount, final ShipSize size, final Player player) {
			this.count = count;
			this.previousCount = previousCount;
			this.size = size;
			this.playerColor = PlayerDto.fromPlayer(player);
		}

	}

	static class CombatOutcomeDto {

		enum OutcomeDto {

			VICTORY, DEFEAT, RETREAT;

			static OutcomeDto toOutcomeDto(final Player player, final Player attackerPlayer,
					final Player defenderPlayer, final SpaceCombatView.Outcome outcome) {
				if (player == attackerPlayer) {
					if (outcome == SpaceCombatView.Outcome.ATTACKER_WON) {
						return OutcomeDto.VICTORY;
					}
					else if (outcome == SpaceCombatView.Outcome.DEFENDER_WON) {
						return OutcomeDto.DEFEAT;
					}
					else if (outcome == SpaceCombatView.Outcome.ATTACKER_RETREATED) {
						return OutcomeDto.RETREAT;
					}
				}
				else if (player == defenderPlayer) {
					if (outcome == SpaceCombatView.Outcome.ATTACKER_WON) {
						return OutcomeDto.DEFEAT;
					}
					else if (outcome == SpaceCombatView.Outcome.DEFENDER_WON
							|| outcome == SpaceCombatView.Outcome.ATTACKER_RETREATED) {
						return OutcomeDto.VICTORY;
					}
				}

				throw new IllegalStateException("Player must either be attacker or defender!");
			}

		}

		final OutcomeDto outcome;

		CombatOutcomeDto(final OutcomeDto outcome) {
			this.outcome = outcome;
		}

	}

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final String systemName;

	final RaceDto attacker;

	final List<CombatantShipSpecsDto> attackerShipSpecs;

	final RaceDto defender;

	final List<CombatantShipSpecsDto> defenderShipSpecs;

	final int fireExchangeCount;

	final CombatOutcomeDto combatOutcome;

	SpaceCombatPageDto(final String systemName, final Race attacker,
			final List<CombatantShipSpecsDto> attackerShipSpecs, final Race defender,
			final List<CombatantShipSpecsDto> defenderShipSpecs, final int fireExchangeCount,
			final CombatOutcomeDto combatOutcome) {
		this.systemName = systemName;

		this.attacker = RaceDto.fromRace(attacker);
		this.attackerShipSpecs = attackerShipSpecs;

		this.defender = RaceDto.fromRace(defender);
		this.defenderShipSpecs = defenderShipSpecs;

		this.fireExchangeCount = fireExchangeCount;

		this.combatOutcome = combatOutcome;
	}

}
