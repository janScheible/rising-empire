package com.scheible.risingempire.game.impl.game;

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
import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
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

/**
 * @author sj
 */
public class GameFactoryImpl implements GameFactory {

	@Override
	public Game create(GameOptions gameOptions) {
		Supplier<ShipDesign> scoutDesginFactory = () -> ShipDesign.builder()
			.name("Scout")
			.size(ShipSize.SMALL)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Nuclear", 2)
			.maneuver(1)
			.weapons()
			.specials(new ReserveTanks());
		Supplier<ShipDesign> colonyShipDesginFactory = () -> ShipDesign.builder()
			.name("Colony Ship")
			.size(ShipSize.LARGE)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons()
			.specials(new ColonyBase());
		Supplier<ShipDesign> fighterShipDesginFactory = () -> ShipDesign.builder()
			.name("Fighter")
			.size(ShipSize.SMALL)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons(1, new BeamWeapon("Laser", new Damage(1, 4)))
			.specials();
		Supplier<ShipDesign> destroyerShipDesginFactory = () -> ShipDesign.builder()
			.name("Destroyer")
			.size(ShipSize.MEDIUM)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons(3, new BeamWeapon("Laser", new Damage(1, 4)), //
					1, new Missile("Nuclear Missile", new Damage(4), Missile.RackSize.TWO))
			.specials();
		Supplier<ShipDesign> cruiserShipDesginFactory = () -> ShipDesign.builder()
			.name("Cruiser")
			.size(ShipSize.LARGE)
			.look(0)
			.computer(0)
			.shield(0)
			.ecm(0)
			.armor("Titanium", 1.0)
			.engine("Retro", 1)
			.maneuver(1)
			.weapons(11, new BeamWeapon("Laser", new Damage(1, 4)), //
					5, new Missile("Nuclear Missile", new Damage(4), Missile.RackSize.TWO))
			.specials();

		Map<DesignSlot, ShipDesign> humanDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(), DesignSlot.SECOND,
				colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(), DesignSlot.FOURTH,
				destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		Fraction humanFraction = new Fraction(Player.BLUE, Race.HUMAN, humanDesigns,
				new Technology(gameOptions.getFleetRangeFactor()));

		Map<DesignSlot, ShipDesign> mrrshanDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		Fraction mrrshanFraction = new Fraction(Player.WHITE, Race.MRRSHAN, mrrshanDesigns,
				new Technology(gameOptions.getFleetRangeFactor()));

		Map<DesignSlot, ShipDesign> psilonDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		Fraction psilonFraction = new Fraction(Player.YELLOW, Race.PSILON, psilonDesigns,
				new Technology(gameOptions.getFleetRangeFactor()));

		Function<Map<DesignSlot, ShipDesign>, DesignSlot> firstUsedSlot = designs -> {
			ArrayList<DesignSlot> usedSlots = new ArrayList<>(designs.keySet());
			Collections.sort(usedSlots);
			return usedSlots.get(0);
		};

		System solSystem = null;
		System fierasSystem = null;
		System centauriSystem = null;
		System rigelSystem = null;
		System spicaSystem = null;
		Set<System> systems;
		if (gameOptions.isTestGame()) {
			systems = Arrays2.asSet(//
					solSystem = System.createHomeSystem("Sol", new Location(60, 60), StarType.YELLOW, PlanetType.JUNGLE,
							PlanetSpecial.NONE, 100, Player.BLUE, firstUsedSlot.apply(humanDesigns)), //
					fierasSystem = System.createHomeSystem("Fieras", new Location(220, 100), StarType.RED,
							PlanetType.TERRAN, PlanetSpecial.NONE, 100, Player.WHITE,
							firstUsedSlot.apply(mrrshanDesigns)), //
					new System("Bunda", new Location(80, 260), StarType.GREEN, PlanetType.ARID, PlanetSpecial.NONE, 30),
					new System("Ajax", new Location(180, 220), StarType.BLUE, PlanetType.TOXIC, PlanetSpecial.RICH, 25),
					new System("Drakka", new Location(340, 140), StarType.WHITE, PlanetType.MINIMAL, PlanetSpecial.NONE,
							50),
					centauriSystem = System.createHomeSystem("Centauri", new Location(140, 340), StarType.PURPLE,
							PlanetType.OCEAN, PlanetSpecial.NONE, 110, Player.YELLOW,
							firstUsedSlot.apply(psilonDesigns)),
					new System("Spica", new Location(984, 728), StarType.GREEN, PlanetType.TUNDRA, PlanetSpecial.NONE,
							50),
					rigelSystem = new System("Rigel", new Location(240, 440), StarType.GREEN, PlanetType.TUNDRA,
							PlanetSpecial.NONE, 50),
					spicaSystem = new System("Spicia", new Location(380, 300), StarType.YELLOW, PlanetType.ARID,
							PlanetSpecial.NONE, 50));
			rigelSystem.colonize(Player.YELLOW, DesignSlot.FIRST);
			spicaSystem.colonize(Player.WHITE, DesignSlot.FIRST);
		}
		else {
			systems = new HashSet<>();

			Location blueStartRegionCenter = new Location(gameOptions.getGalaxySize().getWidth() / 4,
					gameOptions.getGalaxySize().getHeight() / 4);
			Location whiteStartRegionCenter = new Location((gameOptions.getGalaxySize().getWidth() / 4) * 3,
					gameOptions.getGalaxySize().getHeight() / 4);
			Location yellowStartRegionCenter = new Location(gameOptions.getGalaxySize().getWidth() / 2,
					(gameOptions.getGalaxySize().getHeight() / 4) * 3);

			int maxStartRegionDistanceRadius = Math.min(gameOptions.getGalaxySize().getWidth() / 5,
					gameOptions.getGalaxySize().getHeight() / 5);

			Set<Location> locations = BigBang.get().getSystemLocations(gameOptions.getGalaxySize(), 160);
			int i = 0;
			for (Location location : locations) {
				if (solSystem == null && location.getDistance(blueStartRegionCenter) < maxStartRegionDistanceRadius) {
					solSystem = System.createHomeSystem("Sol", location, StarType.YELLOW, PlanetType.JUNGLE,
							PlanetSpecial.NONE, 100, Player.BLUE, firstUsedSlot.apply(humanDesigns));
					systems.add(solSystem);
				}
				else if (fierasSystem == null
						&& location.getDistance(whiteStartRegionCenter) < maxStartRegionDistanceRadius) {
					fierasSystem = System.createHomeSystem("Fieras", location, StarType.RED, PlanetType.TERRAN,
							PlanetSpecial.NONE, 100, Player.WHITE, firstUsedSlot.apply(mrrshanDesigns));
					systems.add(fierasSystem);
				}
				else if (centauriSystem == null
						&& location.getDistance(yellowStartRegionCenter) < maxStartRegionDistanceRadius) {
					centauriSystem = System.createHomeSystem("Centauri", location, StarType.PURPLE, PlanetType.OCEAN,
							PlanetSpecial.NONE, 110, Player.YELLOW, firstUsedSlot.apply(psilonDesigns));
					systems.add(centauriSystem);
				}
				else {
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

		StartFleet humanHomeFleet = new StartFleet(Player.BLUE, solSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.isTestGame()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));
		StartFleet mrrshanHomeFleet = new StartFleet(Player.WHITE, fierasSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.isTestGame()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));
		StartFleet psilonHomeFleet = new StartFleet(Player.YELLOW, centauriSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.isTestGame()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));

		return new GameImpl(systems, Arrays2.asSet(humanFraction, mrrshanFraction, psilonFraction),
				Arrays2.asSet(humanHomeFleet, mrrshanHomeFleet, psilonHomeFleet), gameOptions);
	}

	private int getColonyShipCount(boolean testGame) {
		return testGame ? 4 : 40;
	}

	private void assertHomeSystems(System solSystem, System fierasSystem, System centauriSystem) {
		if (solSystem == null || fierasSystem == null || centauriSystem == null) {
			throw new IllegalStateException(
					"It was not possible to find locations for the following home systems: " + Stream
						.of(Map.entry("solSystem", solSystem), Map.entry("fierasSystem", fierasSystem),
								Map.entry("centauriSystem", centauriSystem))
						.filter(e -> e.getValue() == null)
						.map(Entry::getKey)
						.collect(Collectors.joining(", ")));
		}
	}

	@Override
	public Game load(Object whatEver) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
