package com.scheible.risingempire.game.impl2.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.scheible.risingempire.game.api.GalaxySize;
import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.impl2.apiinternal.Population;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.empire.Empire;
import com.scheible.risingempire.game.impl2.navy.Fleet;
import com.scheible.risingempire.game.impl2.navy.Fleet.Location.Orbit;
import com.scheible.risingempire.game.impl2.navy.Ships;
import com.scheible.risingempire.game.impl2.universe.Planet;
import com.scheible.risingempire.game.impl2.universe.Star;
import com.scheible.risingempire.game.impl2.universe.bigbang.BigBang;
import com.scheible.risingempire.util.SeededRandom;

/**
 * @author sj
 */
public class Game2FactoryImpl implements GameFactory {

	private static List<StarType> STAR_TYPES = List.of(StarType.RED, StarType.GREEN, StarType.YELLOW, StarType.BLUE,
			StarType.WHITE, StarType.PURPLE);

	private static List<PlanetType> PLANET_TYPES = List.of(PlanetType.NOT_HABITABLE, PlanetType.RADIATED,
			PlanetType.TOXIC, PlanetType.INFERNO, PlanetType.DEAD, PlanetType.TUNDRA, PlanetType.BARREN,
			PlanetType.MINIMAL, PlanetType.DESERT, PlanetType.STEPPE, PlanetType.ARID, PlanetType.OCEAN,
			PlanetType.JUNGLE, PlanetType.TERRAN);

	@Override
	public Game create(GameOptions gameOptions) {
		return createInternal(gameOptions, ThreadLocalRandom.current().nextLong());
	}

	private Game2Impl createInternal(GameOptions gameOptions, long seed) {
		SeededRandom random = new SeededRandom(seed);

		GeneratedStars generatedStars = generateStars(gameOptions.galaxySize(), random);
		List<Star> stars = gameOptions.testGame()
				? List.of(new Star("Sol", new Position("6.173", "5.026"), StarType.YELLOW, false),
						new Star("Alpha Centauri", new Position(7.680, 3.986), StarType.BLUE, true),
						new Star("Barnard's Star", new Position(5.173, 6.626), StarType.YELLOW, false),
						new Star("Deneb", new Position(7.680, 7.226), StarType.GREEN, false),
						new Star("Rigel", new Position(8.680, 2.133), StarType.GREEN, false),
						new Star("Vega", new Position("9.973", "5.626"), StarType.WHITE, false),
						new Star("Lalande", new Position(3.386, 2.133), StarType.YELLOW, true),
						new Star("Sirius", new Position("4.080", "8.226"), StarType.RED, true))
				: generatedStars.stars();
		Map<Player, Position> homeSystems = gameOptions.testGame() ? Map.of( //
				Player.BLUE, new Position("6.173", "5.026"), //
				Player.YELLOW, new Position("9.973", "5.626"), //
				Player.WHITE, new Position("4.080", "8.226"))
				: Map.of( //
						Player.BLUE, stars.get(0).position(), //
						Player.WHITE, stars.get(1).position(), //
						Player.YELLOW, stars.get(2).position());

		return new Game2Impl(gameOptions, List.of(new Empire(Player.BLUE, Race.LUMERISKS),
				new Empire(Player.YELLOW, Race.MYXALOR), new Empire(Player.WHITE, Race.XELIPHARI)), stars,
				generatedStars.planets(),
				List.of(new Fleet(Player.BLUE, new Orbit(homeSystems.get(Player.BLUE)),
						new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1))),
						new Fleet(Player.YELLOW, new Orbit(homeSystems.get(Player.YELLOW)),
								new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1))),
						new Fleet(Player.WHITE, new Orbit(homeSystems.get(Player.WHITE)),
								new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1)))),
				homeSystems, random);
	}

	private static GeneratedStars generateStars(GalaxySize galaxySize, SeededRandom random) {
		List<Star> stars = new ArrayList<>();
		Map<Position, Planet> planets = new HashMap<>();

		// sort and convert to list to have a derterministic iteration order
		List<Location> starLocations = BigBang.get()
			.systemLocations(galaxySize, 190, random)
			.stream()
			.sorted((a, b) -> Double.compare(a.distance(Location.ORIGIN), b.distance(Location.ORIGIN)))
			.toList();

		Map<Player, Location> startRegions = Map.of(//
				Player.BLUE, new Location(galaxySize.width() / 4, galaxySize.height() / 4), //
				Player.WHITE, new Location((galaxySize.width() / 4) * 3, galaxySize.height() / 4), //
				Player.YELLOW, new Location(galaxySize.width() / 2, (galaxySize.height() / 4) * 3));

		Map<Player, Location> startStars = new HashMap<>();

		for (Location starPosition : starLocations) {
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
		for (Location starLocation : starLocations) {
			if (!startStars.values().contains(starLocation)) {
				Position starPosition = new Position(starLocation.x() / 75.0, starLocation.y() / 75.0);
				StarType starType = randomStarType(random);

				stars.add(new Star(BigBang.STAR_NAMES.get(i++), starPosition, starType, random.nextBoolean()));

				PlanetType planetType = randomPlanetType(starType, random);
				planets.put(starPosition, new Planet(planetType, PlanetSpecial.NONE, maxPopulation(planetType)));
			}
		}

		for (Player player : List.of(Player.YELLOW, Player.WHITE, Player.BLUE)) {
			Position starPosition = new Position(startStars.get(player).x() / 75.0, startStars.get(player).y() / 75.0);
			stars.add(0, new Star(BigBang.STAR_NAMES.get(i++), starPosition, StarType.YELLOW, false));
			planets.put(starPosition,
					new Planet(PlanetType.TERRAN, PlanetSpecial.NONE, maxPopulation(PlanetType.TERRAN)));
		}

		return new GeneratedStars(stars, planets);
	}

	/**
	 * Same probabilities as in Master of Orion official strategy guide on page 51.
	 */
	private static StarType randomStarType(SeededRandom random) {
		return percentagewiseRandom(STAR_TYPES, List.of(30, 25, 15, 15, 10, 5), random);
	}

	/**
	 * Similar probabilities as in Master of Orion official strategy guide on page 52 (not
	 * yet supported `NOT_HABITABLE` is compensated).
	 */
	private static PlanetType randomPlanetType(StarType starType, SeededRandom random) {
		return switch (starType) {
			case RED ->
				percentagewiseRandom(PLANET_TYPES, List.of(0, 5, 5, 5, 5, 5, 5, 5, 11, 11, 16, 11, 11, 5), random);
			case GREEN ->
				percentagewiseRandom(PLANET_TYPES, List.of(0, 5, 5, 5, 5, 5, 5, 5, 5, 11, 11, 11, 11, 16), random);
			case YELLOW ->
				percentagewiseRandom(PLANET_TYPES, List.of(0, 0, 0, 5, 0, 5, 5, 5, 5, 5, 10, 10, 10, 40), random);
			case BLUE ->
				percentagewiseRandom(PLANET_TYPES, List.of(0, 12, 12, 12, 12, 12, 12, 12, 6, 5, 5, 0, 0, 0), random);
			case WHITE ->
				percentagewiseRandom(PLANET_TYPES, List.of(0, 5, 12, 12, 12, 12, 12, 12, 6, 6, 6, 5, 0, 0), random);
			case PURPLE ->
				percentagewiseRandom(PLANET_TYPES, List.of(0, 28, 18, 18, 13, 8, 8, 7, 0, 0, 0, 0, 0, 0), random);
		};
	}

	private static Population maxPopulation(PlanetType planetType) {
		return switch (planetType) {
			case NOT_HABITABLE -> null; // not yet supported
			case RADIATED -> new Population(20.0);
			case TOXIC -> new Population(20.0);
			case INFERNO -> new Population(25.0);
			case DEAD -> new Population(25.0);
			case TUNDRA -> new Population(40.0);
			case BARREN -> new Population(40.0);
			case MINIMAL -> new Population(45.0);
			case DESERT -> new Population(45.0);
			case STEPPE -> new Population(60.0);
			case ARID -> new Population(65.0);
			case OCEAN -> new Population(80.0);
			case JUNGLE -> new Population(100.0);
			case TERRAN -> new Population(100.0);
		};
	}

	static <T extends Enum<T>> T percentagewiseRandom(List<T> enumConsts, List<Integer> percentages,
			SeededRandom random) {
		if (percentages.stream().reduce(0, Integer::sum) != 100) {
			throw new IllegalArgumentException("The percentages must be 100 in total!");
		}

		int randomThreshold = random.nextInt(1, 101);

		int i = 0;
		int currentThreshold = 0;
		for (T enumConst : enumConsts) {
			currentThreshold += percentages.get(i);
			if (randomThreshold <= currentThreshold) {
				return enumConst;
			}

			i++;
		}

		return enumConsts.getLast();
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

	private record GeneratedStars(List<Star> stars, Map<Position, Planet> planets) {

	}

}
