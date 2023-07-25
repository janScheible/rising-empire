package com.scheible.risingempire.game.impl.game;

import static com.scheible.risingempire.game.api.view.ship.ShipSize.LARGE;
import static com.scheible.risingempire.game.api.view.ship.ShipSize.MEDIUM;
import static com.scheible.risingempire.game.api.view.ship.ShipSize.SMALL;
import static com.scheible.risingempire.game.api.view.system.PlanetType.ARID;
import static com.scheible.risingempire.game.api.view.system.PlanetType.JUNGLE;
import static com.scheible.risingempire.game.api.view.system.PlanetType.MINIMAL;
import static com.scheible.risingempire.game.api.view.system.PlanetType.OCEAN;
import static com.scheible.risingempire.game.api.view.system.PlanetType.TERRAN;
import static com.scheible.risingempire.game.api.view.system.PlanetType.TOXIC;
import static com.scheible.risingempire.game.api.view.system.PlanetType.TUNDRA;
import static com.scheible.risingempire.game.api.view.system.StarType.BLUE;
import static com.scheible.risingempire.game.api.view.system.StarType.GREEN;
import static com.scheible.risingempire.game.api.view.system.StarType.PURPLE;
import static com.scheible.risingempire.game.api.view.system.StarType.RED;
import static com.scheible.risingempire.game.api.view.system.StarType.WHITE;
import static com.scheible.risingempire.game.api.view.system.StarType.YELLOW;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.Game;
import com.scheible.risingempire.game.api.GameFactory;
import com.scheible.risingempire.game.api.GameOptions;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;
import com.scheible.risingempire.game.impl.fleet.StartFleet;
import com.scheible.risingempire.game.impl.fraction.Fraction;
import com.scheible.risingempire.game.impl.fraction.Technology;
import com.scheible.risingempire.game.impl.ship.AbstractWeapon.Damage;
import com.scheible.risingempire.game.impl.ship.BeamWeapon;
import com.scheible.risingempire.game.impl.ship.ColonyBase;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.game.impl.ship.Missile;
import com.scheible.risingempire.game.impl.ship.ReserveTanks;
import com.scheible.risingempire.game.impl.ship.ShipDesign;
import com.scheible.risingempire.game.impl.system.System;
import com.scheible.risingempire.game.impl.universe.BigBang;
import com.scheible.risingempire.util.jdk.Arrays2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author sj
 */
public class GameFactoryImpl implements GameFactory {

	@SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Unique enough for a generated universe.")
	@Override
	public Game create(final GameOptions gameOptions) {

		final Supplier<ShipDesign> scoutDesginFactory = () -> ShipDesign.builder().name("Scout").size(SMALL).look(0)
				.computer(0).shield(0).ecm(0).armor("Titanium", 1.0).engine("Nuclear", 2).maneuver(1).weapons()
				.specials(new ReserveTanks());
		final Supplier<ShipDesign> colonyShipDesginFactory = () -> ShipDesign.builder().name("Colony Ship").size(LARGE)
				.look(0).computer(0).shield(0).ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1).weapons()
				.specials(new ColonyBase());
		final Supplier<ShipDesign> fighterShipDesginFactory = () -> ShipDesign.builder().name("Fighter").size(SMALL)
				.look(0).computer(0).shield(0).ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1)
				.weapons(1, new BeamWeapon("Laser", new Damage(1, 4))).specials();
		final Supplier<ShipDesign> destroyerShipDesginFactory = () -> ShipDesign.builder().name("Destroyer")
				.size(MEDIUM).look(0).computer(0).shield(0).ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1)
				.weapons(3, new BeamWeapon("Laser", new Damage(1, 4)), //
						1, new Missile("Nuclear Missile", new Damage(4), Missile.RackSize.TWO))
				.specials();
		final Supplier<ShipDesign> cruiserShipDesginFactory = () -> ShipDesign.builder().name("Cruiser").size(LARGE)
				.look(0).computer(0).shield(0).ecm(0).armor("Titanium", 1.0).engine("Retro", 1).maneuver(1)
				.weapons(11, new BeamWeapon("Laser", new Damage(1, 4)), //
						5, new Missile("Nuclear Missile", new Damage(4), Missile.RackSize.TWO))
				.specials();

		final Map<DesignSlot, ShipDesign> humanDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		final Fraction humanFraction = new Fraction(Player.BLUE, Race.HUMAN, humanDesigns,
				new Technology(gameOptions.getFleetRangeFactor()));

		final Map<DesignSlot, ShipDesign> mrrshanDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		final Fraction mrrshanFraction = new Fraction(Player.WHITE, Race.MRRSHAN, mrrshanDesigns,
				new Technology(gameOptions.getFleetRangeFactor()));

		final Map<DesignSlot, ShipDesign> psilonDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		final Fraction psilonFraction = new Fraction(Player.YELLOW, Race.PSILON, psilonDesigns,
				new Technology(gameOptions.getFleetRangeFactor()));

		final Function<Map<DesignSlot, ShipDesign>, DesignSlot> firstUsedSlot = designs -> {
			final ArrayList<DesignSlot> usedSlots = new ArrayList<>(designs.keySet());
			Collections.sort(usedSlots);
			return usedSlots.get(0);
		};

		System solSystem = null, fierasSystem = null, centauriSystem = null;
		final Set<System> systems;
		if (gameOptions.isTestGameScenario()) {
			systems = Arrays2.asSet(//
					solSystem = System.createHomeSystem("Sol", new Location(60, 60), YELLOW, JUNGLE, PlanetSpecial.NONE,
							100, Player.BLUE, firstUsedSlot.apply(humanDesigns)), //
					fierasSystem = System.createHomeSystem("Fieras", new Location(220, 100), RED, TERRAN,
							PlanetSpecial.NONE, 100, Player.WHITE, firstUsedSlot.apply(mrrshanDesigns)), //
					new System("Bunda", new Location(80, 260), GREEN, ARID, PlanetSpecial.NONE, 30),
					new System("Ajax", new Location(180, 220), BLUE, TOXIC, PlanetSpecial.RICH, 25),
					new System("Drakka", new Location(340, 140), WHITE, MINIMAL, PlanetSpecial.NONE, 50),
					centauriSystem = System.createHomeSystem("Centauri", new Location(140, 340), PURPLE, OCEAN,
							PlanetSpecial.NONE, 110, Player.YELLOW, firstUsedSlot.apply(psilonDesigns)),
					new System("Spica", new Location(984, 728), GREEN, TUNDRA, PlanetSpecial.NONE, 50));
		} else {
			systems = new HashSet<>();

			final Location blueStartRegionCenter = new Location(gameOptions.getGalaxySize().getWidth() / 4,
					gameOptions.getGalaxySize().getHeight() / 4);
			final Location whiteStartRegionCenter = new Location((gameOptions.getGalaxySize().getWidth() / 4) * 3,
					gameOptions.getGalaxySize().getHeight() / 4);
			final Location yellowStartRegionCenter = new Location(gameOptions.getGalaxySize().getWidth() / 2,
					(gameOptions.getGalaxySize().getHeight() / 4) * 3);

			final int maxStartRegionDistanceRadius = Math.min(gameOptions.getGalaxySize().getWidth() / 5,
					gameOptions.getGalaxySize().getHeight() / 5);

			final Set<Location> locations = BigBang.get().getSystemLocations(gameOptions.getGalaxySize(), 180);
			int i = 0;
			for (final Location location : locations) {
				if (solSystem == null && location.getDistance(blueStartRegionCenter) < maxStartRegionDistanceRadius) {
					solSystem = System.createHomeSystem("Sol", location, YELLOW, JUNGLE, PlanetSpecial.NONE, 100,
							Player.BLUE, firstUsedSlot.apply(humanDesigns));
					systems.add(solSystem);
				} else if (fierasSystem == null
						&& location.getDistance(whiteStartRegionCenter) < maxStartRegionDistanceRadius) {
					fierasSystem = System.createHomeSystem("Fieras", location, RED, TERRAN, PlanetSpecial.NONE, 100,
							Player.WHITE, firstUsedSlot.apply(mrrshanDesigns));
					systems.add(fierasSystem);
				} else if (centauriSystem == null
						&& location.getDistance(yellowStartRegionCenter) < maxStartRegionDistanceRadius) {
					centauriSystem = System.createHomeSystem("Centauri", location, PURPLE, OCEAN, PlanetSpecial.NONE,
							110, Player.YELLOW, firstUsedSlot.apply(psilonDesigns));
					systems.add(centauriSystem);
				} else {
					systems.add(new System(BigBang.STAR_NAMES.get(i), location,
							StarType.values()[ThreadLocalRandom.current().nextInt(StarType.values().length)],
							PlanetType.values()[ThreadLocalRandom.current().nextInt(PlanetType.values().length)],
							PlanetSpecial.values()[ThreadLocalRandom.current().nextInt(PlanetSpecial.values().length)],
							ThreadLocalRandom.current().nextInt(4, 20) * 5));
				}

				i++;
			}
		}

		assertHomeSystems(solSystem, fierasSystem, centauriSystem);

		final StartFleet humanHomeFleet = new StartFleet(Player.BLUE, solSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.isTestGameScenario()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));
		final StartFleet mrrshanHomeFleet = new StartFleet(Player.WHITE, fierasSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.isTestGameScenario()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));
		final StartFleet psilonHomeFleet = new StartFleet(Player.YELLOW, centauriSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.isTestGameScenario()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));

		final Set<Fraction> fractions = Arrays2.asSet(humanFraction, mrrshanFraction, psilonFraction);

		return new GameImpl(systems, fractions, Arrays2.asSet(humanHomeFleet, mrrshanHomeFleet, psilonHomeFleet),
				gameOptions);
	}

	private int getColonyShipCount(final boolean testGameScenario) {
		return testGameScenario ? 4 : 40;
	}

	private void assertHomeSystems(final System solSystem, final System fierasSystem, final System centauriSystem) {
		if (solSystem == null || fierasSystem == null || centauriSystem == null) {
			throw new IllegalStateException(
					"It was not possible to find locations for the following home systems: " + Stream
							.of(new SimpleImmutableEntry<>("solSystem", solSystem),
									new SimpleImmutableEntry<>("fierasSystem", fierasSystem),
									new SimpleImmutableEntry<>("centauriSystem", centauriSystem))
							.filter(e -> e.getValue() == null).map(Entry::getKey).collect(Collectors.joining(", ")));
		}
	}

	@Override
	public Game load(final Object whatEver) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
