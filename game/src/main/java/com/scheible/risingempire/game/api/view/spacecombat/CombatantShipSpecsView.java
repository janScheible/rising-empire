package com.scheible.risingempire.game.api.view.spacecombat;

import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
 * @author sj
 */
public class CombatantShipSpecsView {

	private final ShipTypeId id;
	private final String name;

	private final int count;
	private final int previousCount;
	private final ShipSize size;

	@Nullable
	final Integer shield;
	@Nullable
	final Integer beamDefence;
	@Nullable
	final Integer attackLevel;
	@Nullable
	final Integer warp;
	@Nullable
	final Integer missleDefence;
	@Nullable
	final Integer hits;
	@Nullable
	final Integer speed;

	@Nullable
	final List<String> equipment;

	private final List<FireExchangeView> fireExchanges;

	public CombatantShipSpecsView(final ShipTypeId id, final String name, final int count, final int previousCount,
			final ShipSize size, @Nullable final Integer shield, @Nullable final Integer beamDefence,
			@Nullable final Integer attackLevel, @Nullable final Integer warp, @Nullable final Integer missleDefence,
			@Nullable final Integer hits, @Nullable final Integer speed, final List<String> equipment,
			final List<FireExchangeView> fireExchanges) {
		this.id = id;
		this.name = name;

		this.count = count;
		this.previousCount = previousCount;
		this.size = size;

		this.shield = shield;
		this.beamDefence = beamDefence;
		this.attackLevel = attackLevel;
		this.warp = warp;
		this.missleDefence = missleDefence;
		this.hits = hits;
		this.speed = speed;

		this.equipment = unmodifiableList(equipment);

		this.fireExchanges = unmodifiableList(fireExchanges);
	}

	public ShipTypeId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public int getPreviousCount() {
		return previousCount;
	}

	public ShipSize getSize() {
		return size;
	}

	public Optional<Integer> getShield() {
		return Optional.ofNullable(shield);
	}

	public Optional<Integer> getBeamDefence() {
		return Optional.ofNullable(beamDefence);
	}

	public Optional<Integer> getAttackLevel() {
		return Optional.ofNullable(attackLevel);
	}

	public Optional<Integer> getWarp() {
		return Optional.ofNullable(warp);
	}

	public Optional<Integer> getMissleDefence() {
		return Optional.ofNullable(missleDefence);
	}

	public Optional<Integer> getHits() {
		return Optional.ofNullable(hits);
	}

	public Optional<Integer> getSpeed() {
		return Optional.ofNullable(speed);
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP")
	public List<String> getEquipment() {
		return equipment;
	}

	public List<FireExchangeView> getFireExchanges() {
		return fireExchanges;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj != null && obj.getClass().equals(getClass())) {
			final CombatantShipSpecsView other = (CombatantShipSpecsView) obj;
			return Objects.equals(id, other.id) && Objects.equals(name, other.name)
					&& Objects.equals(count, other.count) && Objects.equals(previousCount, other.previousCount)
					&& Objects.equals(size, other.size) && Objects.equals(fireExchanges, other.fireExchanges);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, count, previousCount, size, fireExchanges);
	}
}
