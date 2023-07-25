package com.scheible.risingempire.game.impl.fleet;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

/**
 *
 * @author sj
 */
public abstract class Fleet implements FleetOrb {

	protected final FleetId id;

	private final Player player;
	protected final Map<DesignSlot, Integer> ships = new EnumMap<>(DesignSlot.class);

	public Fleet(final FleetId id, final Player player, final Map<DesignSlot, Integer> ships) {
		this.id = id;
		this.player = player;
		this.ships.putAll(ships);
	}

	public void join(final Map<DesignSlot, Integer> otherShips) {
		otherShips.entrySet().forEach(slotAndCount -> {
			if (slotAndCount.getValue() > 0) {
				ships.put(slotAndCount.getKey(),
						ships.getOrDefault(slotAndCount.getKey(), 0) + slotAndCount.getValue());
			} else if (slotAndCount.getValue() < 0) {
				throw new IllegalArgumentException("Negative ship counts are invalid!");
			}
		});
	}

	public void detach(final Map<DesignSlot, Integer> otherShips) {
		if (otherShips.values().stream().anyMatch(count -> count < 0)) {
			throw new IllegalArgumentException(
					"The ships to detach (" + otherShips + ") from fleet " + id + " contain negative counts!");
		}

		final Set<DesignSlot> emptySlots = new HashSet<>();
		otherShips.entrySet().forEach(slotAndCount -> {
			final int newCount = ships.getOrDefault(slotAndCount.getKey(), 0) - slotAndCount.getValue();
			if (newCount > 0) {
				ships.put(slotAndCount.getKey(), newCount);
			} else if (newCount == 0) {
				emptySlots.add(slotAndCount.getKey());
			} else {
				throw new IllegalArgumentException("Negative ship counts are invalid!");
			}
		});

		emptySlots.forEach(s -> ships.remove(s));
	}

	public void retain(final Map<DesignSlot, Integer> shipCounts) {
		if (ships.keySet().containsAll(shipCounts.keySet())) {
			ships.clear();
			shipCounts.entrySet().stream().filter(e -> e.getValue() > 0)
					.forEach(e -> ships.put(e.getKey(), e.getValue()));
		} else {
			throw new IllegalArgumentException(
					String.format("Can't remove ships in slots %s of a fleet that only has ships in slots %s.",
							shipCounts.keySet(), ships.keySet()));
		}
	}

	@Override
	public abstract Location getLocation();

	public Map<DesignSlot, Integer> getShips() {
		return Collections.unmodifiableMap(ships);
	}

	public abstract double getDestinationDistance();

	public boolean hasShips() {
		return !ships.isEmpty();
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
		return id;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}