package com.scheible.risingempire.game.api.view.spacecombat;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.ship.ShipSize;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;

import static java.util.Collections.unmodifiableList;

/**
 * @author sj
 */
public class CombatantShipSpecsView {

	private final ShipTypeId id;

	private final String name;

	private final int count;

	private final int previousCount;

	private final ShipSize size;

	private final Integer shield;

	private final Integer beamDefence;

	private final Integer attackLevel;

	private final Integer warp;

	private final Integer missleDefence;

	private final Integer hits;

	private final Integer speed;

	private final List<String> equipment;

	private final List<FireExchangeView> fireExchanges;

	public CombatantShipSpecsView(ShipTypeId id, String name, int count, int previousCount, ShipSize size,
			Integer shield, Integer beamDefence, Integer attackLevel, Integer warp, Integer missleDefence, Integer hits,
			Integer speed, List<String> equipment, List<FireExchangeView> fireExchanges) {
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
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public int getCount() {
		return this.count;
	}

	public int getPreviousCount() {
		return this.previousCount;
	}

	public ShipSize getSize() {
		return this.size;
	}

	public Optional<Integer> getShield() {
		return Optional.ofNullable(this.shield);
	}

	public Optional<Integer> getBeamDefence() {
		return Optional.ofNullable(this.beamDefence);
	}

	public Optional<Integer> getAttackLevel() {
		return Optional.ofNullable(this.attackLevel);
	}

	public Optional<Integer> getWarp() {
		return Optional.ofNullable(this.warp);
	}

	public Optional<Integer> getMissleDefence() {
		return Optional.ofNullable(this.missleDefence);
	}

	public Optional<Integer> getHits() {
		return Optional.ofNullable(this.hits);
	}

	public Optional<Integer> getSpeed() {
		return Optional.ofNullable(this.speed);
	}

	public List<String> getEquipment() {
		return this.equipment;
	}

	public List<FireExchangeView> getFireExchanges() {
		return this.fireExchanges;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (obj != null && obj.getClass().equals(getClass())) {
			CombatantShipSpecsView other = (CombatantShipSpecsView) obj;
			return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name)
					&& Objects.equals(this.count, other.count)
					&& Objects.equals(this.previousCount, other.previousCount) && Objects.equals(this.size, other.size)
					&& Objects.equals(this.fireExchanges, other.fireExchanges);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.name, this.count, this.previousCount, this.size, this.fireExchanges);
	}

}
