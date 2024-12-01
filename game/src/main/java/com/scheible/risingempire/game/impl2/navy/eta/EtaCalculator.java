package com.scheible.risingempire.game.impl2.navy.eta;

import java.util.Optional;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Rounds;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.ShipMovementSpecsProvider;
import com.scheible.risingempire.game.impl2.navy.Ships;

/**
 * @author sj
 */
public class EtaCalculator {

	private final ShipMovementSpecsProvider shipMovementSpecsProvider;

	public EtaCalculator(ShipMovementSpecsProvider shipMovementSpecsProvider) {
		this.shipMovementSpecsProvider = shipMovementSpecsProvider;
	}

	public Optional<Rounds> calc(Player player, Position origin, Position destination, Ships ships,
			Set<Position> bases) {
		Parsec distance = destination.subtract(origin).length();

		if (!ships.empty() && distance
			.compareTo(this.shipMovementSpecsProvider.effectiveRange(player, ships.counts().keySet())) <= 0) {
			Speed speed = this.shipMovementSpecsProvider.effectiveSpeed(player, ships.counts().keySet());
			Rounds rounds = new Rounds((int) Math.ceil(distance.divide(speed.distance()).quantity().doubleValue()));
			return Optional.of(rounds);
		}
		else {
			return Optional.empty();
		}
	}

}
