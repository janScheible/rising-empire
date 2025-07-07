package com.scheible.risingempire.game.impl2.ship;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Damage;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.ship.component.Armor;
import com.scheible.risingempire.game.impl2.ship.component.Computer;
import com.scheible.risingempire.game.impl2.ship.component.Ecm;
import com.scheible.risingempire.game.impl2.ship.component.Engine;
import com.scheible.risingempire.game.impl2.ship.component.Maneuver;
import com.scheible.risingempire.game.impl2.ship.component.Shield;
import com.scheible.risingempire.game.impl2.ship.special.BattleScanner;
import com.scheible.risingempire.game.impl2.ship.special.ColonyBase;
import com.scheible.risingempire.game.impl2.ship.special.ReserveTanks;
import com.scheible.risingempire.game.impl2.ship.weapon.BeamWeapon;
import com.scheible.risingempire.game.impl2.ship.weapon.Missile;

/**
 * @author sj
 */
public class Shipyard {

	private static final List<ShipClassId> SHIP_CLASS_IDS = List.of(new ShipClassId("scout"),
			new ShipClassId("colony-ship"), new ShipClassId("fighter"), new ShipClassId("destroyer"),
			new ShipClassId("cruiser"));

	private final BuildCapacityProvider buildCapacityProvider;

	public Shipyard(BuildCapacityProvider buildCapacityProvider) {
		this.buildCapacityProvider = buildCapacityProvider;
		this.buildCapacityProvider.hashCode(); // to make PMD happy for now...
	}

	public ShipDesign design(Player player, ShipClassId shipClassId) {
		if (shipClassId.value().equals("scout")) {
			return ShipDesign.builder()
				.id(shipClassId)
				.name("Scout")
				.size(ShipSize.SMALL)
				.look(0)
				.computer(new Computer(0))
				.shield(new Shield(0))
				.ecm(new Ecm(0))
				.armor(new Armor("Titanium", 1.0))
				.engine(new Engine("Nuclear", 2))
				.maneuver(new Maneuver(1))
				.weapons(Map.of())
				.specials(Set.of(new ReserveTanks()))
				.build();
		}
		else if (shipClassId.value().equals("colony-ship")) {
			return ShipDesign.builder()
				.id(shipClassId)
				.name("Colony Ship")
				.size(ShipSize.LARGE)
				.look(0)
				.computer(new Computer(0))
				.shield(new Shield(0))
				.ecm(new Ecm(0))
				.armor(new Armor("Titanium", 1.0))
				.engine(new Engine("Retro", 1))
				.maneuver(new Maneuver(1))
				.weapons(Map.of())
				.specials(Set.of(new ColonyBase()))
				.build();
		}
		else if (shipClassId.value().equals("fighter")) {
			return ShipDesign.builder()
				.id(shipClassId)
				.name("Fighter")
				.size(ShipSize.SMALL)
				.look(0)
				.computer(new Computer(0))
				.shield(new Shield(0))
				.ecm(new Ecm(0))
				.armor(new Armor("Titanium", 1.0))
				.engine(new Engine("Retro", 1))
				.maneuver(new Maneuver(1))
				.weapons(Map.of(new BeamWeapon("Laser", new Damage(1, 4)), 1))
				.specials(Set.of())
				.build();
		}
		else if (shipClassId.value().equals("destroyer")) {
			return ShipDesign.builder()
				.id(shipClassId)
				.name("Destroyer")
				.size(ShipSize.MEDIUM)
				.look(0)
				.computer(new Computer(0))
				.shield(new Shield(0))
				.ecm(new Ecm(0))
				.armor(new Armor("Titanium", 1.0))
				.engine(new Engine("Retro", 1))
				.maneuver(new Maneuver(1))
				.weapons(Map.of(new BeamWeapon("Laser", new Damage(1, 4)), 3, //
						new Missile("Nuclear Missile", new Damage(4), 2), 1))
				.specials(Set.of())
				.build();
		}
		else if (shipClassId.value().equals("cruiser")) {
			return ShipDesign.builder()
				.id(shipClassId)
				.name("Cruiser")
				.size(ShipSize.LARGE)
				.look(0)
				.computer(new Computer(0))
				.shield(new Shield(0))
				.ecm(new Ecm(0))
				.armor(new Armor("Titanium", 1.0))
				.engine(new Engine("Retro", 1))
				.maneuver(new Maneuver(1))
				.weapons(Map.of(new BeamWeapon("Laser", new Damage(1, 4)), 11, //
						new Missile("Nuclear Missile", new Damage(4), 5), 5))
				.specials(Set.of(new BattleScanner()))
				.build();
		}
		else if (shipClassId.equals(ShipClassId.COLONISTS_TRANSPORTER)) {
			return ShipDesign.builder()
				.id(shipClassId)
				.name("Transporter")
				.size(ShipSize.LARGE)
				.look(0)
				.computer(new Computer(0))
				.shield(new Shield(0))
				.ecm(new Ecm(0))
				.armor(new Armor("Titanium", 1.0))
				.engine(new Engine("Retro", 1))
				.maneuver(new Maneuver(1))
				.weapons(Map.of())
				.specials(Set.of())
				.build();
		}
		else {
			throw new IllegalArgumentException("Unknown ship class '" + shipClassId + "'!");
		}
	}

	public boolean colonyShip(ShipClassId shipClassId) {
		return shipClassId.equals(new ShipClassId("colony-ship"));
	}

	public boolean battleScanner(Player player, ShipClassId shipClassId) {
		return design(player, shipClassId).specials().stream().anyMatch(s -> s.getClass().equals(BattleScanner.class));
	}

	public boolean colonizable(Player player, Set<ShipClassId> shipClassIds, PlanetType planetType) {
		return shipClassIds.contains(new ShipClassId("colony-ship"));
	}

	public ShipClassId initalShipClass() {
		return SHIP_CLASS_IDS.get(0);
	}

	public ShipClassId nextShipClass(ShipClassId shipClass) {
		int i = SHIP_CLASS_IDS.indexOf(shipClass);

		if (i + 1 < SHIP_CLASS_IDS.size()) {
			return SHIP_CLASS_IDS.get(i + 1);
		}
		else {
			return SHIP_CLASS_IDS.get(0);
		}

	}

	public Credit cost(Player player, ShipClassId shipClassId) {
		if (shipClassId.value().equals("scout")) {
			return new Credit(100);
		}
		else if (shipClassId.value().equals("colony-ship")) {
			return new Credit(5000);
		}
		else if (shipClassId.value().equals("fighter")) {
			return new Credit(200);
		}
		else if (shipClassId.value().equals("destroyer")) {
			return new Credit(2000);
		}
		else if (shipClassId.value().equals("cruiser")) {
			return new Credit(6000);
		}
		else {
			throw new IllegalArgumentException("Unknown ship class '" + shipClassId + "'!");
		}
	}

}
