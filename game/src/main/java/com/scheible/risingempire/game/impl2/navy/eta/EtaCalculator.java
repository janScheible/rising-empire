package com.scheible.risingempire.game.impl2.navy.eta;

import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public class EtaCalculator {

	private final ShipMovementSpecsProvider shipMovementSpecsProvider;

	private final ColoniesProvider basePositionsProvider;

	public EtaCalculator(ShipMovementSpecsProvider shipMovementSpecsProvider, ColoniesProvider coloniesProvider) {
		this.shipMovementSpecsProvider = shipMovementSpecsProvider;
		this.basePositionsProvider = coloniesProvider;
	}

	public Optional<Rounds> calc(Player player, Position origin, Position destination, Ships ships) {
		if (ships.empty()) {
			return Optional.empty();
		}

		boolean inRange = false;
		for (Position base : this.basePositionsProvider.colonies(player)) {
			Parsec baseDistance = destination.subtract(base).length();

			if (baseDistance
				.compareTo(this.shipMovementSpecsProvider.effectiveRange(player, ships.counts().keySet())) <= 0) {
				inRange = true;
				break;
			}
		}

		if (inRange) {
			Parsec distance = destination.subtract(origin).length();
			Speed speed = this.shipMovementSpecsProvider.effectiveSpeed(player, ships.counts().keySet());
			Rounds rounds = new Rounds((int) Math.ceil(distance.divide(speed.distance()).quantity().doubleValue()));
			return Optional.of(rounds);
		}
		else {
			return Optional.empty();
		}
	}

}
