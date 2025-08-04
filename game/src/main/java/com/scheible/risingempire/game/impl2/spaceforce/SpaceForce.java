package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet;
import com.scheible.risingempire.game.impl2.spaceforce.combat.PredefinedSpaceCombatResolver;
import com.scheible.risingempire.game.impl2.spaceforce.combat.ResolvedSpaceCombat;
import com.scheible.risingempire.game.impl2.spaceforce.combat.SpaceCombatResolver;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
public class SpaceForce {

	private final EncounteringFleetShipsProvider encounteringFleetShipsProvider;

	private final List<SpaceCombatFleet> retreatingFleets = new ArrayList<>();

	private final List<SpaceCombatFleet> spaceCombatFleets = new ArrayList<>();

	private final List<SpaceCombat> spaceCombats = new ArrayList<>();

	private final SpaceCombatResolver spaceCombatResolver;

	public SpaceForce(EncounteringFleetShipsProvider encounteringFleetShipsProvider,
			SpaceCombatResolver spaceCombatResolver, Optional<Outcome> predefinedSpaceCombatOutcome) {
		this.encounteringFleetShipsProvider = encounteringFleetShipsProvider;
		this.spaceCombatResolver = predefinedSpaceCombatOutcome
			.<SpaceCombatResolver>map(outcome -> new PredefinedSpaceCombatResolver(outcome, spaceCombatResolver))
			.orElse(spaceCombatResolver);
	}

	public void resolveSpaceCombats() {
		this.retreatingFleets.clear();
		this.spaceCombatFleets.clear();
		this.spaceCombats.clear();

		Map<Position, List<EncounteringFleet>> allEncounteringFleetShips = this.encounteringFleetShipsProvider
			.encounteringFleetShips();
		for (Position system : allEncounteringFleetShips.keySet()) {
			// sort earlier arriving fleets first (with orbiting as very first)
			List<EncounteringFleet> encounteringFleetShips = allEncounteringFleetShips.get(system)
				.stream()
				.sorted(SpaceForce::compareEncounteringFleetsByArrivalRoundFraction)
				.collect(Collectors.toCollection(ArrayList::new));

			while (encounteringFleetShips.size() > 1) {
				EncounteringFleet defendingFleet = encounteringFleetShips.get(0);
				EncounteringFleet attackingFleet = encounteringFleetShips.get(1);

				ResolvedSpaceCombat resolvedSpaceCombat = this.spaceCombatResolver.resolve(attackingFleet.player(),
						attackingFleet.ships(), defendingFleet.player(), defendingFleet.ships());
				Ships attackerShips = resolvedSpaceCombat.attackerShips(attackingFleet.ships());
				Ships defenderShips = resolvedSpaceCombat.defenderShips(defendingFleet.ships());

				if (resolvedSpaceCombat.outcome().equals(Outcome.ATTACKER_RETREATED)) {
					encounteringFleetShips.remove(1);
					this.retreatingFleets.add(new SpaceCombatFleet(attackingFleet.player(), system, attackerShips));
				}
				else if (resolvedSpaceCombat.outcome().equals(Outcome.DEFENDER_WON)) {
					encounteringFleetShips.remove(1);
				}
				else { // ATTACKER_WON
					encounteringFleetShips.remove(0);
				}

				if (!attackingFleet.ships().equals(attackerShips)) {
					this.spaceCombatFleets
						.add(new SpaceCombatFleet(attackingFleet.player(), attackingFleet.system(), attackerShips));
				}

				if (!defendingFleet.ships().equals(defenderShips)) {
					this.spaceCombatFleets
						.add(new SpaceCombatFleet(defendingFleet.player(), defendingFleet.system(), defenderShips));
				}

				this.spaceCombats.add(SpaceCombat.builder()
					.system(system)
					.attacker(attackingFleet.player())
					.defender(defendingFleet.player())
					.outcome(resolvedSpaceCombat.outcome())
					.fireExchangeCount(resolvedSpaceCombat.fireExchangeCount())
					.previousAttackerShips(attackingFleet.ships())
					.attackerShips(attackerShips)
					.attackerFireExchanges(resolvedSpaceCombat.attackerFireExchanges()
						.entrySet()
						.stream()
						.collect(Collectors.toMap(e -> e.getKey().shipClassId(), Entry::getValue)))
					.attackerShipSpecsAvailable(resolvedSpaceCombat.attackerShipSpecsAvailable())
					.previousDefenderShips(defendingFleet.ships())
					.defenderShips(defenderShips)
					.defenderFireExchanges(resolvedSpaceCombat.defenderFireExchanges()
						.entrySet()
						.stream()
						.collect(Collectors.toMap(e -> e.getKey().shipClassId(), Entry::getValue)))
					.defenderShipSpecsAvailable(resolvedSpaceCombat.defenderShipSpecsAvailable())
					.build());
			}
		}
	}

	public boolean retreating(Player player, Position fleet) {
		return this.retreatingFleets.stream().anyMatch(rf -> rf.player().equals(player) && rf.position().equals(fleet));
	}

	public List<SpaceCombatFleet> retreatingFleets() {
		return unmodifiableList(this.retreatingFleets);
	}

	public List<SpaceCombatFleet> spaceCombatFleets() {
		return unmodifiableList(this.spaceCombatFleets);
	}

	public List<SpaceCombat> spaceCombats() {
		return unmodifiableList(this.spaceCombats);
	}

	public List<SpaceCombat> spaceCombat(Position system) {
		return this.spaceCombats.stream().filter(sc -> sc.system().equals(system)).toList();
	}

	static int compareEncounteringFleetsByArrivalRoundFraction(EncounteringFleet a, EncounteringFleet b) {
		if (a.arrivalRoundFraction().isEmpty()) {
			return -1;
		}
		else if (b.arrivalRoundFraction().isEmpty()) {
			return 1;
		}
		else {
			return Double.compare(a.arrivalRoundFraction().get(), b.arrivalRoundFraction().get());
		}
	}

}
