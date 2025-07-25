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

		Map<DesignSlot, ShipDesign> lumerisksDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		Fraction lumerisksFraction = new Fraction(Player.BLUE, Race.LUMERISKS, lumerisksDesigns,
				new Technology(gameOptions.fleetRangeFactor()));

		Map<DesignSlot, ShipDesign> myxalorDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		Fraction myxalorFraction = new Fraction(Player.WHITE, Race.MYXALOR, myxalorDesigns,
				new Technology(gameOptions.fleetRangeFactor()));

		Map<DesignSlot, ShipDesign> xeliphariDesigns = Map.of(DesignSlot.FIRST, scoutDesginFactory.get(),
				DesignSlot.SECOND, colonyShipDesginFactory.get(), DesignSlot.THIRD, fighterShipDesginFactory.get(),
				DesignSlot.FOURTH, destroyerShipDesginFactory.get(), DesignSlot.FIFTH, cruiserShipDesginFactory.get());
		Fraction xeliphariFraction = new Fraction(Player.YELLOW, Race.XELIPHARI, xeliphariDesigns,
				new Technology(gameOptions.fleetRangeFactor()));

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
		if (gameOptions.testGame()) {
			systems = Arrays2.asSet(//
					solSystem = System.createHomeSystem("Sol", new Location(60, 60), StarType.YELLOW, PlanetType.JUNGLE,
							PlanetSpecial.NONE, 100, Player.BLUE, firstUsedSlot.apply(lumerisksDesigns)), //
					fierasSystem = System.createHomeSystem("Fieras", new Location(220, 100), StarType.RED,
							PlanetType.TERRAN, PlanetSpecial.NONE, 100, Player.WHITE,
							firstUsedSlot.apply(myxalorDesigns)), //
					new System("Bunda", new Location(80, 260), StarType.GREEN, PlanetType.ARID, PlanetSpecial.NONE, 30),
					new System("Ajax", new Location(180, 220), StarType.BLUE, PlanetType.TOXIC, PlanetSpecial.RICH, 25),
					new System("Drakka", new Location(340, 140), StarType.WHITE, PlanetType.MINIMAL, PlanetSpecial.NONE,
							50),
					centauriSystem = System.createHomeSystem("Centauri", new Location(140, 340), StarType.PURPLE,
							PlanetType.OCEAN, PlanetSpecial.NONE, 110, Player.YELLOW,
							firstUsedSlot.apply(xeliphariDesigns)),
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

			Location blueStartRegionCenter = new Location(gameOptions.galaxySize().width() / 4,
					gameOptions.galaxySize().height() / 4);
			Location whiteStartRegionCenter = new Location((gameOptions.galaxySize().width() / 4) * 3,
					gameOptions.galaxySize().height() / 4);
			Location yellowStartRegionCenter = new Location(gameOptions.galaxySize().width() / 2,
					(gameOptions.galaxySize().height() / 4) * 3);

			int maxStartRegionDistanceRadius = Math.min(gameOptions.galaxySize().width() / 5,
					gameOptions.galaxySize().height() / 5);

			Set<Location> locations = BigBang.get().getSystemLocations(gameOptions.galaxySize(), 160);
			int i = 0;
			for (Location location : locations) {
				if (solSystem == null && location.distance(blueStartRegionCenter) < maxStartRegionDistanceRadius) {
					solSystem = System.createHomeSystem("Sol", location, StarType.YELLOW, PlanetType.JUNGLE,
							PlanetSpecial.NONE, 100, Player.BLUE, firstUsedSlot.apply(lumerisksDesigns));
					systems.add(solSystem);
				}
				else if (fierasSystem == null
						&& location.distance(whiteStartRegionCenter) < maxStartRegionDistanceRadius) {
					fierasSystem = System.createHomeSystem("Fieras", location, StarType.RED, PlanetType.TERRAN,
							PlanetSpecial.NONE, 100, Player.WHITE, firstUsedSlot.apply(myxalorDesigns));
					systems.add(fierasSystem);
				}
				else if (centauriSystem == null
						&& location.distance(yellowStartRegionCenter) < maxStartRegionDistanceRadius) {
					centauriSystem = System.createHomeSystem("Centauri", location, StarType.PURPLE, PlanetType.OCEAN,
							PlanetSpecial.NONE, 110, Player.YELLOW, firstUsedSlot.apply(xeliphariDesigns));
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

		StartFleet lumerisksHomeFleet = new StartFleet(Player.BLUE, solSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.testGame()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));
		StartFleet myxalorHomeFleet = new StartFleet(Player.WHITE, fierasSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.testGame()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));
		StartFleet xeliphariHomeFleet = new StartFleet(Player.YELLOW, centauriSystem,
				Map.of(DesignSlot.FIRST, 2, DesignSlot.SECOND, getColonyShipCount(gameOptions.testGame()),
						DesignSlot.THIRD, 30, DesignSlot.FOURTH, 10, DesignSlot.FIFTH, 4));

		return new GameImpl(systems, Arrays2.asSet(lumerisksFraction, myxalorFraction, xeliphariFraction),
				Arrays2.asSet(lumerisksHomeFleet, myxalorHomeFleet, xeliphariHomeFleet), gameOptions);
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
	public Game load(Savegame savegame) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
