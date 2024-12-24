package com.scheible.risingempire.game.impl2.navy;

import java.util.Map;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Parsec;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.eta.ShipMovementSpecsProvider;

/**
 * @author sj
 */
public abstract class AbstractNavyTest {

	protected final Player player = Player.BLUE;

	protected final ShipClassId scout = new ShipClassId("scout");

	protected final ShipClassId enterprise = new ShipClassId("enterprise");

	protected final ShipMovementSpecsProvider shipMovementSpecsProvider = new ShipMovementSpecsProvider() {

		private final Map<ShipClassId, Speed> shipSpeeds = Map.of(//
				AbstractNavyTest.this.scout, new Speed(1.5), //
				AbstractNavyTest.this.enterprise, new Speed(1.0), //
				ShipClassId.COLONISTS_TRANSPORTER, new Speed(1.0));

		@Override
		public Speed speed(Player player, ShipClassId shipClassId) {
			return this.shipSpeeds.get(shipClassId);
		}

		@Override
		public Parsec range(Player player, ShipClassId shipClassId) {
			return new Parsec(4.0);
		}

		@Override
		public Parsec range(Player player) {
			return new Parsec(4.0);
		}

		@Override
		public Parsec extendedRange(Player player) {
			return new Parsec(4.0);
		}
	};

	protected final NewShipsProvider newShipsProvider = new NewShipsProvider() {
		public Map<Position, Map<ShipClassId, Integer>> newShips(Player player) {
			return Map.of();
		}
	};

	protected Position origin = new Position(0.0, 0.0);

	protected Position destination = new Position(3.0, 0.0);

	protected Position otherDestination = new Position(2.0, 2.0);

	protected Fleet orbitingFleet(Position system, Ships ships) {
		return new Fleet(this.player, new Orbit(system), ships);
	}

	protected Navy.DeployOrbiting deployOrbiting(Position origin, Ships ships, Position destination) {
		return new Navy.DeployOrbiting(this.player, origin, destination, ships);
	}

	protected Navy.DeployJustLeaving deployJustLeaving(Position origin, Position previousDestination, Speed speed,
			Ships ships, Position destination) {
		return new Navy.DeployJustLeaving(this.player, origin, previousDestination, speed, destination, ships);
	}

	protected Navy.TransferColonists transferColonists(Position origin, int transporterCount, Position destination) {
		return new Navy.TransferColonists(this.player, origin, destination, transporterCount);
	}

	protected static Ships ships(ShipClassId shipClassId1, int count1) {
		return new Ships(Map.of(shipClassId1, count1));
	}

	protected static Ships ships(ShipClassId shipClassId1, int count1, ShipClassId shipClassId2, int count2) {
		return new Ships(Map.of(shipClassId1, count1, shipClassId2, count2));
	}

}
