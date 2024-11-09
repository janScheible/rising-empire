package com.scheible.risingempire.game.impl2.game;

import java.util.List;
import java.util.Map;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.empire.Empire;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.ship.ShipClassId;
import com.scheible.risingempire.game.impl2.universe.Star;

/**
 * @author sj
 */
public class Game2FactoryImpl implements GameFactory {

	@Override
	public Game create(GameOptions gameOptions) {
		return new Game2Impl(gameOptions.galaxySize(), List.of(new Empire(Player.BLUE, Race.HUMAN)),
				List.of(new Star("Sol", new Position(10.0, 10.0), StarType.YELLOW),
						new Star("Alpha Centauri", new Position(20.0, 10.0), StarType.RED),
						new Star("Barnard's Star", new Position(10.0, 20.0), StarType.GREEN),
						new Star("Luhman 16", new Position(20.0, 20.0), StarType.PURPLE)),
				List.of(new Fleet(Player.BLUE, new Orbit(new Position(10.0, 10.0)),
						new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("enterprise"), 2)))));
	}

	@Override
	public Game load(Object whatEver) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
