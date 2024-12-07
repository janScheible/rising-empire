package com.scheible.risingempire.game.impl2.game;

import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.navy.eta.BasePositionsProvider;
import com.scheible.risingempire.game.impl2.navy.eta.ShipMovementSpecsProvider;
import com.scheible.risingempire.game.impl2.technology.Technology;

/**
 * Contains all adapters use in the game implementation. Adapters might do some
 * translation work and/or provide a delegate mechanism to break up interface cyclic
 * dependencies.
 *
 * @author sj
 */
public final class Adapters {

	private Adapters() {
	}

	public static class BasePositionsProviderAdapter implements BasePositionsProvider {

		private Colonization delegate;

		public void delegate(Colonization delegate) {
			this.delegate = delegate;
		}

		@Override
		public Set<Position> positions(Player player) {
			return this.delegate.colonies(player).stream().map(Colony::position).collect(Collectors.toSet());
		}

	}

	public static class ShipMovementSpecsProviderAdapter implements ShipMovementSpecsProvider {

		private Technology delegate;

		public void delegate(Technology delegate) {
			this.delegate = delegate;
		}

		@Override
		public Parsec range(Player player, ShipClassId ShipClassId) {
			return this.delegate.range(player, ShipClassId);
		}

		@Override
		public Parsec range(Player player) {
			return this.delegate.range(player);
		}

		@Override
		public Parsec extendedRange(Player player) {
			return this.delegate.extendedRange(player);
		}

		@Override
		public Speed speed(Player player, ShipClassId shipClassId) {
			return this.delegate.speed(player, shipClassId);
		}

	}

}
