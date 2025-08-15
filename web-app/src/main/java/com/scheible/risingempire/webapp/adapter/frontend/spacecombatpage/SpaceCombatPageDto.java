package com.scheible.risingempire.webapp.adapter.frontend.spacecombatpage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView;
import com.scheible.risingempire.webapp.adapter.frontend.dto.PlayerDto;
import com.scheible.risingempire.webapp.adapter.frontend.dto.RaceDto;

/**
 * @author sj
 */
class SpaceCombatPageDto {

	@JsonProperty(value = "@type")
	final String type = getClass().getSimpleName();

	final String systemName;

	final RaceDto attacker;

	final List<CombatantShipSpecsDto> attackerShipSpecs;

	final PlayerDto attackerPlayerColor;

	final RaceDto defender;

	final List<CombatantShipSpecsDto> defenderShipSpecs;

	final PlayerDto defenderPlayerColor;

	final int fireExchangeCount;

	final CombatOutcomeDto combatOutcome;

	SpaceCombatPageDto(String systemName, Race attacker, Player attackerPlayer,
			List<CombatantShipSpecsDto> attackerShipSpecs, Race defender, Player defenderPlayer,
			List<CombatantShipSpecsDto> defenderShipSpecs, int fireExchangeCount, CombatOutcomeDto combatOutcome) {
		this.systemName = systemName;

		this.attacker = RaceDto.fromRace(attacker);
		this.attackerShipSpecs = attackerShipSpecs;
		this.attackerPlayerColor = PlayerDto.fromPlayer(attackerPlayer);

		this.defender = RaceDto.fromRace(defender);
		this.defenderShipSpecs = defenderShipSpecs;
		this.defenderPlayerColor = PlayerDto.fromPlayer(defenderPlayer);

		this.fireExchangeCount = fireExchangeCount;

		this.combatOutcome = combatOutcome;
	}

	static class FireExchangeDto {

		final int lostHitPoints;

		final int damage;

		final int count;

		FireExchangeDto(int lostHitPoints, int damage, int count) {
			this.lostHitPoints = lostHitPoints;
			this.damage = damage;
			this.count = count;
		}

	}

	static class CombatantShipSpecsDto {

		final String id;

		final String name;

		Optional<Integer> shield;

		Optional<Integer> beamDefence;

		Optional<Integer> attackLevel;

		Optional<Integer> damage = Optional.of(0); // will be updated in the fire
													// exchanges

		Optional<Integer> missleDefence;

		Optional<Integer> hits;

		Optional<Integer> speed;

		List<String> equipment;

		final ShipsDto ships;

		final Map<Integer, FireExchangeDto> fireExchanges;

		CombatantShipSpecsDto(String id, String name, Optional<Integer> shield, Optional<Integer> beamDefence,
				Optional<Integer> attackLevel, Optional<Integer> missleDefence, Optional<Integer> hits,
				Optional<Integer> speed, List<String> equipment, ShipsDto ships,
				Map<Integer, FireExchangeDto> fireExchanges) {
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

		ShipsDto(int count, int previousCount, ShipSize size, Player player) {
			this.count = count;
			this.previousCount = previousCount;
			this.size = size;
			this.playerColor = PlayerDto.fromPlayer(player);
		}

	}

	static class CombatOutcomeDto {

		enum OutcomeDto {

			VICTORY, DEFEAT, RETREAT;

			static OutcomeDto toOutcomeDto(Player player, Player attackerPlayer, Player defenderPlayer,
					SpaceCombatView.Outcome outcome) {
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

		CombatOutcomeDto(OutcomeDto outcome) {
			this.outcome = outcome;
		}

	}

}
