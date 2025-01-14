package com.scheible.risingempire.game.impl2.spaceforce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.spacecombat.SpaceCombatView.Outcome;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.spaceforce.EncounteringFleetShipsProvider.EncounteringFleet;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
public class SpaceForce {

	private final EncounteringFleetShipsProvider encounteringFleetShipsProvider;

	private final List<RetreatingFleet> retreatingFleets = new ArrayList<>();

	private final List<SpaceCombat> spaceCombats = new ArrayList<>();

	public SpaceForce(EncounteringFleetShipsProvider encounteringFleetShipsProvider) {
		this.encounteringFleetShipsProvider = encounteringFleetShipsProvider;
	}

	public void resolveSpaceCombats() {
		this.retreatingFleets.clear();
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
				defendingFleet.toString();
				EncounteringFleet attackingFleet = encounteringFleetShips.get(1);
				attackingFleet.toString();

				if ("ATTACKER_RETREATED".equals("ATTACKER_RETREATED")) {
					encounteringFleetShips.remove(1);
					this.retreatingFleets.add(new RetreatingFleet(attackingFleet.player(), system));
				}
				else if ("DEFENDER_WON".equals("DEFENDER_WON")) {
					encounteringFleetShips.remove(1);
				}
				else { // ATTACKER_WON
					encounteringFleetShips.remove(0);
				}

				this.spaceCombats.add(new SpaceCombat(system, attackingFleet.player(), defendingFleet.player(),
						Outcome.ATTACKER_RETREATED));
			}
		}
	}

	public boolean retreating(Player player, Position fleet) {
		return this.retreatingFleets.stream().anyMatch(rf -> rf.player().equals(player) && rf.position().equals(fleet));
	}

	public List<RetreatingFleet> retreatingFleets() {
		return unmodifiableList(this.retreatingFleets);
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
