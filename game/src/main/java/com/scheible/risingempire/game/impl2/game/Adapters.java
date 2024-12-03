package com.scheible.risingempire.game.impl2.game;

import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.colonization.Colonization;
import com.scheible.risingempire.game.impl2.colonization.Colony;
import com.scheible.risingempire.game.impl2.navy.eta.BasePositionsProvider;

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

}
