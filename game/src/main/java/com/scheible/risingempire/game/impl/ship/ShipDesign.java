package com.scheible.risingempire.game.impl.ship;

import static java.util.Collections.unmodifiableSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.ship.ShipSize;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class ShipDesign {

	public static Builder builder() {
		return name -> size -> look -> computerLevel -> shieldLevel -> ecmLevel -> (armorName, armorFactor) -> (
				engineName,
				engineWarpSpeed) -> maneuverLevel -> (count1, weapon1, count2, weapon2, count3, weapon3, count4,
						weapon4) -> (special1, special2, special3) -> new ShipDesign(name, size, look,
								Computer.create(computerLevel), Shield.create(shieldLevel), Ecm.create(ecmLevel),
								new Armor(armorName, armorFactor), new Engine(engineName, engineWarpSpeed),
								Maneuver.create(maneuverLevel),
								unmodifiableSet(List
										.of(new WeaponSlot(count1, weapon1), new WeaponSlot(count2, weapon2),
												new WeaponSlot(count3, weapon3), new WeaponSlot(count4, weapon4))
										.stream().filter(WeaponSlot::isNotEmpty).collect(Collectors.toSet())),
								Stream.of(special1, special2, special3).filter(Objects::nonNull)
										.collect(Collectors.toSet()));
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

			default SpecialsStep weapons(final int count1, final AbstractWeapon weapon1) {
				return weapons(count1, weapon1, 0, null, 0, null, 0, null);
			}

			default SpecialsStep weapons(final int count1, final AbstractWeapon weapon1, final int count2,
					final AbstractWeapon weapon2) {
				return weapons(count1, weapon1, count2, weapon2, 0, null, 0, null);
			}

			default SpecialsStep weapons(final AbstractWeapon weapon1, final int count1, final AbstractWeapon weapon2,
					final int count2, final AbstractWeapon weapon3, final int count3) {
				return weapons(count1, weapon1, count2, weapon2, count3, weapon3, 0, null);
			}

			SpecialsStep weapons(int count1, AbstractWeapon weapon1, int count2, AbstractWeapon weapon2, int count3,
					AbstractWeapon weapon3, int count4, AbstractWeapon weapon4);
		}

		interface SpecialsStep {

			default ShipDesign specials() {
				return specials(null, null, null);
			}

			default ShipDesign specials(final AbstractSpecial special1) {
				return specials(special1, null, null);
			}

			default ShipDesign specials(final AbstractSpecial special1, final AbstractSpecial special2) {
				return specials(special1, special2, null);
			}

			ShipDesign specials(AbstractSpecial special1, AbstractSpecial special2, AbstractSpecial special3);
		}
	}

	private abstract static class AbstractLeveledPart {

		protected final String name;
		protected final int level;

		protected AbstractLeveledPart(final String partName, final int level) {
			this.name = (level == 0 ? "None" : partName + " ") + RomanNumberGenerator.getNumber(level);
			this.level = level;
		}

		protected static int requireLevelGreaterThanOrEquals(final Class<? extends AbstractLeveledPart> partClass,
				final int level, final int minLevel) {
			if (level < minLevel) {
				throw new IllegalArgumentException(String.format(
						"The level %d of %s parts must be greater than or " + "equals to the min level %d!", level,
						partClass.getSimpleName(), minLevel));
			}
			return level;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[name = '" + name + "', level = " + level + "]";
		}
	}

	private static class Computer extends AbstractLeveledPart {

		private Computer(final int level) {
			super("Mark", level);
		}

		private static Computer create(final int level) {
			return new Computer(requireLevelGreaterThanOrEquals(Computer.class, level, 0));
		}
	}

	private static class Shield extends AbstractLeveledPart {

		private Shield(final int level) {
			super("Class", level);
		}

		private static Shield create(final int level) {
			return new Shield(requireLevelGreaterThanOrEquals(Shield.class, level, 0));
		}
	}

	private static class Ecm extends AbstractLeveledPart {

		private Ecm(final int level) {
			super("Jammer", level);
		}

		private static Ecm create(final int level) {
			return new Ecm(requireLevelGreaterThanOrEquals(Ecm.class, level, 0));
		}
	}

	private static class Armor {

		@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False positive, is used...")
		private final String name;
		@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False positive, is used...")
		private final double factor;

		private Armor(final String name, final double factor) {
			this.name = name;
			this.factor = factor;
		}
	}

	private static class Engine {

		@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False positive, is used...")
		private final String name;
		@SuppressFBWarnings(value = "FCBL_FIELD_COULD_BE_LOCAL", justification = "False positive, is used...")
		private final int warp;

		private Engine(final String name, final int warp) {
			this.name = name;
			this.warp = warp;
		}
	}

	private static class Maneuver extends AbstractLeveledPart {

		private Maneuver(final int level) {
			super("Class", level);
		}

		private static Maneuver create(final int level) {
			return new Maneuver(requireLevelGreaterThanOrEquals(Ecm.class, level, 1));
		}
	}

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

	private ShipDesign(final String name, final ShipSize size, final int look, final Computer computer,
			final Shield shield, final Ecm ecm, final Armor armor, final Engine engine, final Maneuver maneuver,
			final Set<WeaponSlot> weapons, final Set<AbstractSpecial> specials) {
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

		if (maneuver.level > engine.warp) {
			throw new IllegalArgumentException(
					String.format("The maneuver level (%d) can't be larger than the engine " + "warp speed (%d)!",
							maneuver.level, engine.warp));
		}
	}

	public String getName() {
		return name;
	}

	public ShipSize getSize() {
		return size.shipSize;
	}

	public int getLook() {
		return look;
	}

	public int getAttackLevel() {
		final int battleScannerBonus = hasSpecial(BattleScanner.class) ? 1 : 0;
		return computer.level + battleScannerBonus;
	}

	public int getHitsAbsorbedByShield() {
		return shield.level;
	}

	public int getMissileDefence() {
		return getBeamDefence() + ecm.level;
	}

	public int getHitPoints() {
		return (int) (size.baseHits * armor.factor);
	}

	public int getBeamDefence() {
		return size.baseDefense + maneuver.level;
	}

	public int getCombatSpeed() {
		return (engine.warp + 2) / 2;
	}

	public int getWarpSpeed() {
		return engine.warp;
	}

	public int getInitiative() {
		final int battleScannerBonus = hasSpecial(BattleScanner.class) ? 3 : 0;
		return getAttackLevel() + getBeamDefence() + battleScannerBonus;
	}

	public Set<WeaponSlot> getWeaponSlots() {
		return weapons;
	}

	public Set<AbstractSpecial> getSpecials() {
		return specials;
	}

	public boolean hasColonyBase() {
		return hasSpecial(ColonyBase.class);
	}

	private boolean hasSpecial(final Class<? extends AbstractSpecial> specialType) {
		return specials.stream().anyMatch(s -> specialType.equals(s.getClass()));
	}

	@Override
	public String toString() {
		final StringJoiner values = new StringJoiner(", ", "ShipDesign[", "]").add("name='" + name + "'")
				.add("size=" + size).add("look=" + look).add("computer='" + computer.name + "'")
				.add("attackLevel=" + getAttackLevel()).add("shield='" + shield.name + "'")
				.add("hitsAbsorbedByShield=" + getHitsAbsorbedByShield()).add("ecm='" + ecm.name + "'")
				.add("missileDefence=" + getMissileDefence()).add("armor='" + armor.name + "'")
				.add("hitPoints=" + getHitPoints()).add("engine='" + engine.name + "'").add("warp=" + engine.warp)
				.add("beamDefence=" + getBeamDefence()).add("maneuver='" + maneuver.level + "'")
				.add("combatSpeed=" + getCombatSpeed()).add("weapons=" + weapons).add("specials=" + specials);
		return values.toString();
	}
}
