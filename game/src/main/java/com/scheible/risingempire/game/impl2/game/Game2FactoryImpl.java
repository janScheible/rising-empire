package com.scheible.risingempire.game.impl2.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.empire.Empire;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.bigbang.BigBang;
import com.scheible.risingempire.util.SeededRandom;

/**
 * @author sj
 */
public class Game2FactoryImpl implements GameFactory {

	@Override
	public Game create(GameOptions gameOptions) {
		return createInternal(gameOptions, ThreadLocalRandom.current().nextLong());
	}

	private Game2Impl createInternal(GameOptions gameOptions, long seed) {
		SeededRandom random = new SeededRandom(seed);

		List<Star> stars = gameOptions.testGame()
				? List.of(new Star("Sol", new Position("6.173", "5.026"), StarType.YELLOW, false),
						new Star("Alpha Centauri", new Position(7.680, 3.986), StarType.BLUE, true),
						new Star("Barnard's Star", new Position(5.173, 6.626), StarType.YELLOW, false),
						new Star("Deneb", new Position(7.680, 7.226), StarType.GREEN, false),
						new Star("Rigel", new Position(8.680, 2.133), StarType.GREEN, false),
						new Star("Vega", new Position("9.973", "5.626"), StarType.WHITE, false),
						new Star("Lalande", new Position(3.386, 2.133), StarType.YELLOW, true),
						new Star("Sirius", new Position("4.080", "8.226"), StarType.RED, true))
				: stars(gameOptions.galaxySize(), random);
		Map<Player, Position> homeSystems = gameOptions.testGame() ? Map.of( //
				Player.BLUE, new Position("6.173", "5.026"), //
				Player.YELLOW, new Position("9.973", "5.626"), //
				Player.WHITE, new Position("4.080", "8.226"))
				: Map.of( //
						Player.BLUE, stars.get(0).position(), //
						Player.WHITE, stars.get(1).position(), //
						Player.YELLOW, stars.get(2).position());

		return new Game2Impl(gameOptions,
				List.of(new Empire(Player.BLUE, Race.LUMERISKS), new Empire(Player.YELLOW, Race.MYXALOR),
						new Empire(Player.WHITE, Race.XELIPHARI)),
				stars,
				List.of(new Fleet(Player.BLUE, new Orbit(homeSystems.get(Player.BLUE)),
						new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1))),
						new Fleet(Player.YELLOW, new Orbit(homeSystems.get(Player.YELLOW)),
								new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1))),
						new Fleet(Player.WHITE, new Orbit(homeSystems.get(Player.WHITE)),
								new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1)))),
				homeSystems, random);
	}

	private static List<Star> stars(GalaxySize galaxySize, SeededRandom random) {
		List<Star> stars = new ArrayList<>();

		// sort and convert to list to have a derterministic iteration order
		List<Location> starPositions = BigBang.get()
			.systemLocations(galaxySize, 190, random)
			.stream()
			.sorted((a, b) -> Double.compare(a.distance(Location.ORIGIN), b.distance(Location.ORIGIN)))
			.toList();

		Map<Player, Location> startRegions = Map.of(//
				Player.BLUE, new Location(galaxySize.width() / 4, galaxySize.height() / 4), //
				Player.WHITE, new Location((galaxySize.width() / 4) * 3, galaxySize.height() / 4), //
				Player.YELLOW, new Location(galaxySize.width() / 2, (galaxySize.height() / 4) * 3));

		Map<Player, Location> startStars = new HashMap<>();

		for (Location starPosition : starPositions) {
			for (Player player : List.of(Player.BLUE, Player.WHITE, Player.YELLOW)) {
				Location previousClosestStar = startStars.get(player);
				if (previousClosestStar == null) {
					startStars.put(player, starPosition);
				}
				else {
					if (starPosition.distance(startRegions.get(player)) < previousClosestStar
						.distance(startRegions.get(player))) {
						startStars.put(player, starPosition);
					}
				}
			}
		}

		int i = 0;
		for (Location starPosition : starPositions) {
			if (!startStars.values().contains(starPosition)) {
				stars.add(new Star(BigBang.STAR_NAMES.get(i++),
						new Position(starPosition.x() / 75.0, starPosition.y() / 75.0), StarType.random(random),
						random.nextBoolean()));
			}
		}

		for (Player player : List.of(Player.YELLOW, Player.WHITE, Player.BLUE)) {
			stars.add(0,
					new Star(BigBang.STAR_NAMES.get(i++),
							new Position(startStars.get(player).x() / 75.0, startStars.get(player).y() / 75.0),
							StarType.YELLOW, false));
		}

		return stars;
	}

	@Override
	public Game load(Savegame savegame) {
		if (savegame instanceof Savegame2Impl savegame2) {
			Game2Impl game = createInternal(savegame2.gameOptions(), savegame2.seed());

			List<Round> rounds = savegame2.commands().keySet().stream().sorted().toList();

			for (int i = 0; i < rounds.size(); i++) {
				boolean last = i == rounds.size() - 1;
				Round round = rounds.get(i);

				for (Player player : savegame2.gameOptions().players()) {
					game.applyCommands(player, savegame2.commands().get(round).getOrDefault(player, List.of()));

					if (!last) {
						game.forPlayer(player).finishTurn();

					}
				}
			}

			return game;
		}
		else {
			throw new UnsupportedOperationException(
					"Savegame of type '" + savegame.getClass().getSimpleName() + "' is not supported!");
		}
	}

}
