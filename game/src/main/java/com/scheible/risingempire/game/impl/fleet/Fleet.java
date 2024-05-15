package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

/**
 * @author sj
 */
public abstract class Fleet implements FleetOrb {

	protected final FleetId id;

	protected final Map<DesignSlot, Integer> ships = new EnumMap<>(DesignSlot.class);

	private final Player player;

	public Fleet(FleetId id, Player player, Map<DesignSlot, Integer> ships) {
		this.id = id;
		this.player = player;
		this.ships.putAll(ships);
	}

	public void join(Map<DesignSlot, Integer> otherShips) {
		otherShips.entrySet().forEach(slotAndCount -> {
			if (slotAndCount.getValue() > 0) {
				this.ships.put(slotAndCount.getKey(),
						this.ships.getOrDefault(slotAndCount.getKey(), 0) + slotAndCount.getValue());
			}
			else if (slotAndCount.getValue() < 0) {
				throw new IllegalArgumentException("Negative ship counts are invalid!");
			}
		});
	}

	public void detach(Map<DesignSlot, Integer> otherShips) {
		if (otherShips.values().stream().anyMatch(count -> count < 0)) {
			throw new IllegalArgumentException(
					"The ships to detach (" + otherShips + ") from fleet " + this.id + " contain negative counts!");
		}

		Set<DesignSlot> emptySlots = new HashSet<>();
		otherShips.entrySet().forEach(slotAndCount -> {
			int newCount = this.ships.getOrDefault(slotAndCount.getKey(), 0) - slotAndCount.getValue();
			if (newCount > 0) {
				this.ships.put(slotAndCount.getKey(), newCount);
			}
			else if (newCount == 0) {
				emptySlots.add(slotAndCount.getKey());
			}
			else {
				throw new IllegalArgumentException("Negative ship counts are invalid!");
			}
		});

		emptySlots.forEach(this.ships::remove);
	}

	public void retain(Map<DesignSlot, Integer> shipCounts) {
		if (this.ships.keySet().containsAll(shipCounts.keySet())) {
			this.ships.clear();
			shipCounts.entrySet()
				.stream()
				.filter(e -> e.getValue() > 0)
				.forEach(e -> this.ships.put(e.getKey(), e.getValue()));
		}
		else {
			throw new IllegalArgumentException(String.format(Locale.ROOT,
					"Can't remove ships in slots %s of a fleet that only has ships in slots %s.", shipCounts.keySet(),
					this.ships.keySet()));
		}
	}

	@Override
	public abstract Location getLocation();

	public Map<DesignSlot, Integer> getShips() {
		return Collections.unmodifiableMap(this.ships);
	}

	public abstract double getDestinationDistance();

	public boolean hasShips() {
		return !this.ships.isEmpty();
	}

	public boolean isOrbiting() {
		return this instanceof OrbitingFleet;
	}

	public OrbitingFleet asOrbiting() {
		return (OrbitingFleet) this;
	}

	public boolean isDeployed() {
		return this instanceof DeployedFleet;
	}

	public DeployedFleet asDeployed() {
		return (DeployedFleet) this;
	}

	@Override
	public FleetId getId() {
		return this.id;
	}

	@Override
	public Player getPlayer() {
		return this.player;
	}

}
