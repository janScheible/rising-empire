package com.scheible.risingempire.game.impl2.game;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.colonization.Colony;
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
		return new Game2Impl(gameOptions.galaxySize(),
				List.of(new Empire(Player.BLUE, Race.LUMERISKS), new Empire(Player.YELLOW, Race.MYXALOR),
						new Empire(Player.WHITE, Race.XELIPHARI)),
				List.of(new Star("Sol", new Position("6.173", "5.026"), StarType.YELLOW, false),
						new Star("Alpha Centauri", new Position(7.680, 3.986), StarType.BLUE, true),
						new Star("Barnard's Star", new Position(5.173, 6.626), StarType.YELLOW, false),
						new Star("Deneb", new Position(7.680, 7.226), StarType.GREEN, false),
						new Star("Rigel", new Position(8.680, 2.133), StarType.GREEN, false),
						new Star("Vega", new Position("9.973", "5.626"), StarType.WHITE, false),
						new Star("Lalande", new Position(3.386, 2.133), StarType.YELLOW, true),
						new Star("Sirius", new Position("4.080", "8.226"), StarType.RED, true)),
				List.of(new Fleet(Player.BLUE, new Orbit(new Position("6.173", "5.026")),
						new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("enterprise"), 2))),
						new Fleet(Player.YELLOW, new Orbit(new Position("9.973", "5.626")),
								new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("enterprise"), 2))),
						new Fleet(Player.WHITE, new Orbit(new Position("4.080", "8.226")),
								new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("enterprise"), 2)))));
	}

	@Override
	public Game load(Object whatEver) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
