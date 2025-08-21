package com.scheible.risingempire.game.impl2.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	private static final List<StarType> STAR_TYPES = List.of(StarType.RED, StarType.GREEN, StarType.YELLOW,
			StarType.BLUE, StarType.WHITE, StarType.PURPLE);

	private static final List<PlanetType> PLANET_TYPES = List.of(PlanetType.NOT_HABITABLE, PlanetType.RADIATED,
			PlanetType.TOXIC, PlanetType.INFERNO, PlanetType.DEAD, PlanetType.TUNDRA, PlanetType.BARREN,
			PlanetType.MINIMAL, PlanetType.DESERT, PlanetType.STEPPE, PlanetType.ARID, PlanetType.OCEAN,
			PlanetType.JUNGLE, PlanetType.TERRAN);

	private static final Map<Race, String> HOME_SYSTEM_NAMES = Map.of( //
			Race.BLYZARIANS, "Blyzar", //
			Race.DRACONILITHS, "Draconis", //
			Race.KRYLOQUIANS, "Krylon", //
			Race.LUMERISKS, "Lumera", //
			Race.MYXALOR, "Myxar", //
			Race.OLTHARIEN, "Olthara", //
			Race.QALTRUVIAN, "Qaltru", //
			Race.VORTELUXIAN, "Vortelux", //
			Race.XELIPHARI, "Xeliphar", //
			Race.ZYNTHORAX, "Zynthor");

	@Override
	public Game create(GameOptions gameOptions) {
		return createInternal(gameOptions, ThreadLocalRandom.current().nextLong());
	}

	private Game2Impl createInternal(GameOptions gameOptions, long seed) {
		SeededRandom random = new SeededRandom(seed);

		Map<Player, Race> races = Map.of( //
				Player.BLUE, Race.LUMERISKS, //
				Player.GREEN, Race.DRACONILITHS, //
				Player.PURPLE, Race.BLYZARIANS, //
				Player.RED, Race.ZYNTHORAX, //
				Player.WHITE, Race.XELIPHARI, //
				Player.YELLOW, Race.MYXALOR);

		List<Player> players = gameOptions.players().stream().sorted().toList();

		GeneratedStars generatedStars = generateStars(gameOptions.galaxySize(), players, races, random);

		List<Star> stars = gameOptions.testGame()
				? List.of(new Star("Sol", new Position("0.8", "0.8"), StarType.YELLOW, false),
						new Star("Krylon", new Position("2.933", "1.333"), StarType.RED, false),
						new Star("Bunda", new Position("1.066", "3.466"), StarType.GREEN, false),
						new Star("Ajax", new Position("2.4", "2.933"), StarType.BLUE, false),
						new Star("Drakka", new Position("4.533", "1.866"), StarType.WHITE, false),
						new Star("Centauri", new Position("1.866", "4.533"), StarType.PURPLE, true),
						new Star("Spica", new Position("13.12", "9.706"), StarType.GREEN, true),
						new Star("Rigel", new Position("3.2", "5.866"), StarType.GREEN, true),
						new Star("Spicia", new Position("5.066", "4"), StarType.YELLOW, true))
				: generatedStars.stars();

		Map<Player, Position> homeSystems = gameOptions.testGame() ? Map.of( //
				Player.BLUE, /* Sol */ new Position("0.8", "0.8"), //
				Player.YELLOW, /* Centauri */ new Position("1.866", "4.533"), //
				Player.WHITE, /* Krylon */ new Position("2.933", "1.333"))
				: IntStream.range(0, players.size())
					.mapToObj(i -> Map.entry(players.get(i), stars.get(i).position()))
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		Map<Position, Planet> planets = gameOptions.testGame() ? Map.of( //
				/* Sol */ new Position("0.8", "0.8"),
				new Planet(PlanetType.JUNGLE, PlanetSpecial.NONE, new Population(100)), //
				/* Krylon */ new Position("2.933", "1.333"),
				new Planet(PlanetType.TERRAN, PlanetSpecial.NONE, new Population(100)), //
				/* Bunda */ new Position("1.066", "3.466"),
				new Planet(PlanetType.ARID, PlanetSpecial.NONE, new Population(30)), //
				/* Ajax */ new Position("2.4", "2.933"),
				new Planet(PlanetType.TOXIC, PlanetSpecial.RICH, new Population(25)), //
				/* Drakka */ new Position("4.533", "1.866"),
				new Planet(PlanetType.MINIMAL, PlanetSpecial.NONE, new Population(50)), //
				/* Centauri */ new Position("1.866", "4.533"),
				new Planet(PlanetType.OCEAN, PlanetSpecial.NONE, new Population(110)), //
				/* Spica */ new Position("13.12", "9.706"),
				new Planet(PlanetType.TUNDRA, PlanetSpecial.NONE, new Population(50)), //
				/* Rigel */ new Position("3.2", "5.866"),
				new Planet(PlanetType.TUNDRA, PlanetSpecial.NONE, new Population(50)), //
				/* Spicia */ new Position("5.066", "4"),
				new Planet(PlanetType.ARID, PlanetSpecial.NONE, new Population(50)) //
		) : generatedStars.planets();

		List<Empire> empires = players.stream().map(p -> new Empire(p, races.get(p))).toList();

		List<Fleet> fleets = players.stream()
			.map(p -> new Fleet(p, new Orbit(homeSystems.get(p)),
					new Ships(Map.of(new ShipClassId("scout"), 2, new ShipClassId("colony-ship"), 1))))
			.toList();

		Game2Impl game = new Game2Impl(gameOptions, empires, stars, planets, fleets, homeSystems, random);

		if (gameOptions.testGame()) {
			game.addColonies(Map.of( //
					Player.YELLOW, /* Rigel */ new Position("3.2", "5.866"), //
					Player.WHITE, /* Spicia */ new Position("5.066", "4")));
		}

		return game;
	}

	private static GeneratedStars generateStars(GalaxySize galaxySize, List<Player> players, Map<Player, Race> races,
			SeededRandom random) {
		List<Star> stars = new ArrayList<>();
		Map<Position, Planet> planets = new HashMap<>();

		// sort and convert to list to have a derterministic iteration order
		List<Location> starLocations = BigBang.get()
			.systemLocations(galaxySize, 190, random)
			.stream()
			.sorted((a, b) -> Double.compare(a.distance(Location.ORIGIN), b.distance(Location.ORIGIN)))
			.toList();

		Map<Player, Location> startRegions = startRegions(galaxySize, players);

		Map<Player, Location> startStars = new HashMap<>();

		for (Location starPosition : starLocations) {
			for (Player player : players) {
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

		for (Player player : players) {
			Position starPosition = new Position(startStars.get(player).x() / 75.0, startStars.get(player).y() / 75.0);
			stars.add(0, new Star(HOME_SYSTEM_NAMES.get(races.get(player)), starPosition, StarType.YELLOW, false));
			planets.put(starPosition,
					new Planet(PlanetType.TERRAN, PlanetSpecial.NONE, maxPopulation(PlanetType.TERRAN)));
		}

		return new GeneratedStars(stars, planets);
	}

	/**
	 * Start regions are placed on an ellipse with equal distance between every player.
	 */
	private static Map<Player, Location> startRegions(GalaxySize galaxySize, List<Player> players) {
		Map<Player, Location> startRegions = new HashMap<>();

		double a = (galaxySize.width() / 3.0);
		double b = (galaxySize.height() / 3.0);
		double centerX = galaxySize.width() / 2.0;
		double centerY = galaxySize.height() / 2.0;

		double step = (2.0 * Math.PI) / players.size();
		double t = 0;
		int startRegionPlayer = 0;

		while (t < 2.0 * (Math.PI - 0.01)) {
			double x = a * Math.cos(t);
			double y = b * Math.sin(t);

			startRegions.put(players.get(startRegionPlayer), new Location((int) (centerX + x), (int) (centerY + y)));

			t += step;
			startRegionPlayer++;
		}

		return startRegions;
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
