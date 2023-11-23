package com.scheible.risingempire.game.impl.ship;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.ship.ShipSize;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class ShipDesign {

	private final String name;

	private final ShipSizeWithBaseValues size;

	private final int look;

	private final Computer computer;

	private final Shield shield;

	private final Ecm ecm;

	private final Armor armor;

	private final Engine engine;

	private final Maneuver maneuver;

	private final Set<WeaponSlot> weapons;

	private final Set<AbstractSpecial> specials;

	private ShipDesign(String name, ShipSize size, int look, Computer computer, Shield shield, Ecm ecm, Armor armor,
			Engine engine, Maneuver maneuver, Set<WeaponSlot> weapons, Set<AbstractSpecial> specials) {
		this.name = name;
		this.size = ShipSizeWithBaseValues.of(size);
		this.look = look;

		this.computer = computer;
		this.shield = shield;
		this.ecm = ecm;
		this.armor = armor;
		this.engine = engine;
		this.maneuver = maneuver;

		this.weapons = unmodifiableSet(weapons);
		this.specials = unmodifiableSet(specials);

		if (this.maneuver.level > engine.warp) {
			throw new IllegalArgumentException(String.format(Locale.ROOT,
					"The maneuver level (%d) can't be larger than the engine " + "warp speed (%d)!",
					this.maneuver.level, this.engine.warp));
		}
	}

	public static Builder builder() {
		return name -> size -> look -> computerLevel -> shieldLevel -> ecmLevel -> (armorName,
				armorFactor) -> (engineName,
						engineWarpSpeed) -> maneuverLevel -> (count1, weapon1, count2, weapon2, count3, weapon3, count4,
								weapon4) -> (special1, special2, special3) -> new ShipDesign(name, size, look,
										Computer.create(computerLevel), Shield.create(shieldLevel),
										Ecm.create(ecmLevel), new Armor(armorName, armorFactor),
										new Engine(engineName, engineWarpSpeed), Maneuver.create(maneuverLevel),
										unmodifiableSet(List
											.of(new WeaponSlot(count1, weapon1), new WeaponSlot(count2, weapon2),
													new WeaponSlot(count3, weapon3), new WeaponSlot(count4, weapon4))
											.stream()
											.filter(WeaponSlot::isNotEmpty)
											.collect(Collectors.toSet())),
										Stream.of(special1, special2, special3)
											.filter(Objects::nonNull)
											.collect(Collectors.toSet()));
	}

	public String getName() {
		return this.name;
	}

	public ShipSize getSize() {
		return this.size.shipSize;
	}

	public int getLook() {
		return this.look;
	}

	public int getAttackLevel() {
		int battleScannerBonus = hasSpecial(BattleScanner.class) ? 1 : 0;
		return this.computer.level + battleScannerBonus;
	}

	public int getHitsAbsorbedByShield() {
		return this.shield.level;
	}

	public int getMissileDefence() {
		return getBeamDefence() + this.ecm.level;
	}

	public int getHitPoints() {
		return (int) (this.size.baseHits * this.armor.factor);
	}

	public int getBeamDefence() {
		return this.size.baseDefense + this.maneuver.level;
	}

	public int getCombatSpeed() {
		return (this.engine.warp + 2) / 2;
	}

	public int getWarpSpeed() {
		return this.engine.warp;
	}

	public int getInitiative() {
		int battleScannerBonus = hasSpecial(BattleScanner.class) ? 3 : 0;
		return getAttackLevel() + getBeamDefence() + battleScannerBonus;
	}

	public Set<WeaponSlot> getWeaponSlots() {
		return this.weapons;
	}

	public Set<AbstractSpecial> getSpecials() {
		return this.specials;
	}

	public boolean hasColonyBase() {
		return hasSpecial(ColonyBase.class);
	}

	private boolean hasSpecial(Class<? extends AbstractSpecial> specialType) {
		return this.specials.stream().anyMatch(s -> specialType.equals(s.getClass()));
	}

	@Override
	public String toString() {
		StringJoiner values = new StringJoiner(", ", "ShipDesign[", "]").add("name='" + this.name + "'")
			.add("size=" + this.size)
			.add("look=" + this.look)
			.add("computer='" + this.computer.name + "'")
			.add("attackLevel=" + getAttackLevel())
			.add("shield='" + this.shield.name + "'")
			.add("hitsAbsorbedByShield=" + getHitsAbsorbedByShield())
			.add("ecm='" + this.ecm.name + "'")
			.add("missileDefence=" + getMissileDefence())
			.add("armor='" + this.armor.name + "'")
			.add("hitPoints=" + this.getHitPoints())
			.add("engine='" + this.engine.name + "'")
			.add("warp=" + this.engine.warp)
			.add("beamDefence=" + getBeamDefence())
			.add("maneuver='" + this.maneuver.level + "'")
			.add("combatSpeed=" + getCombatSpeed())
			.add("weapons=" + this.weapons)
			.add("specials=" + this.specials);
		return values.toString();
	}

	public interface Builder {

		SizeStep name(String name);

		interface SizeStep {

			LookStep size(ShipSize size);

		}

		interface LookStep {

			ComputerStep look(int look);

		}

		interface ComputerStep {

			ShieldStep computer(int level);

		}

		interface ShieldStep {

			EcmStep shield(int level);

		}

		interface EcmStep {

			ArmorStep ecm(int level);

		}

		interface ArmorStep {

			EngineStep armor(String name, double factor);

		}

		interface EngineStep {

			ManeuverStep engine(String name, int warpSpeed);

		}

		interface ManeuverStep {

			WeaponsStep maneuver(int level);

		}

		interface WeaponsStep {

			default SpecialsStep weapons() {
				return weapons(0, null, 0, null, 0, null, 0, null);
			}

			default SpecialsStep weapons(int count1, AbstractWeapon weapon1) {
				return weapons(count1, weapon1, 0, null, 0, null, 0, null);
			}

			default SpecialsStep weapons(int count1, AbstractWeapon weapon1, int count2, AbstractWeapon weapon2) {
				return weapons(count1, weapon1, count2, weapon2, 0, null, 0, null);
			}

			default SpecialsStep weapons(AbstractWeapon weapon1, int count1, AbstractWeapon weapon2, int count2,
					AbstractWeapon weapon3, int count3) {
				return weapons(count1, weapon1, count2, weapon2, count3, weapon3, 0, null);
			}

			SpecialsStep weapons(int count1, AbstractWeapon weapon1, int count2, AbstractWeapon weapon2, int count3,
					AbstractWeapon weapon3, int count4, AbstractWeapon weapon4);

		}

		interface SpecialsStep {

			default ShipDesign specials() {
				return specials(null, null, null);
			}

			default ShipDesign specials(AbstractSpecial special1) {
				return specials(special1, null, null);
			}

			default ShipDesign specials(AbstractSpecial special1, AbstractSpecial special2) {
				return specials(special1, special2, null);
			}

			ShipDesign specials(AbstractSpecial special1, AbstractSpecial special2, AbstractSpecial special3);

		}

	}

	private abstract static class AbstractLeveledPart {

		protected final String name;

		protected final int level;

		protected AbstractLeveledPart(String partName, int level) {
			this.name = (level == 0 ? "None" : partName + " ") + RomanNumberGenerator.getNumber(level);
			this.level = level;
		}

		protected static int requireLevelGreaterThanOrEquals(Class<? extends AbstractLeveledPart> partClass, int level,
				int minLevel) {
			if (level < minLevel) {
				throw new IllegalArgumentException(String.format(Locale.ROOT,
						"The level %d of %s parts must be greater than or " + "equals to the min level %d!", level,
						partClass.getSimpleName(), minLevel));
			}
			return level;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[name = '" + this.name + "', level = " + this.level + "]";
		}

	}

	private static class Computer extends AbstractLeveledPart {

		private Computer(int level) {
			super("Mark", level);
		}

		private static Computer create(int level) {
			return new Computer(requireLevelGreaterThanOrEquals(Computer.class, level, 0));
		}

	}

	private static class Shield extends AbstractLeveledPart {

		private Shield(int level) {
			super("Class", level);
		}

		private static Shield create(int level) {
			return new Shield(requireLevelGreaterThanOrEquals(Shield.class, level, 0));
		}

	}

	private static class Ecm extends AbstractLeveledPart {

		private Ecm(int level) {
			super("Jammer", level);
		}

		private static Ecm create(int level) {
			return new Ecm(requireLevelGreaterThanOrEquals(Ecm.class, level, 0));
		}

	}

	private static class Armor {

		private final String name;

		private final double factor;

		private Armor(String name, double factor) {
			this.name = name;
			this.factor = factor;
		}

	}

	private static class Engine {

		private final String name;

		private final int warp;

		private Engine(String name, int warp) {
			this.name = name;
			this.warp = warp;
		}

	}

	private static class Maneuver extends AbstractLeveledPart {

		private Maneuver(final int level) {
			super("Class", level);
		}

		private static Maneuver create(int level) {
			return new Maneuver(requireLevelGreaterThanOrEquals(Ecm.class, level, 1));
		}

	}

}
