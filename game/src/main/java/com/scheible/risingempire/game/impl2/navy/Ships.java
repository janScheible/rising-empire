package com.scheible.risingempire.game.impl2.navy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import com.scheible.risingempire.game.impl2.ship.ShipClassId;

import static java.util.Collections.unmodifiableMap;

public record Ships(Map<ShipClassId, Integer> counts) {

	public static Ships NONE = new Ships(Map.of());

	public Ships {
		if (counts.keySet().contains(ShipClassId.COLONISTS_TRANSPORTER) && counts.size() != 1) {
			throw new IllegalArgumentException(
					"Ships can either consist of only colonists transporter or regular ships but was: '" + counts
							+ "'!");
		}

		counts = unmodifiableMap(counts);
	}

	public static Ships transporters(int count) {
		return new Ships(Map.of(ShipClassId.COLONISTS_TRANSPORTER, count));
	}

	public boolean empty() {
		return this.counts.values().stream().reduce(0, Integer::sum) == 0;
	}

	public boolean transporters() {
		return this.counts.keySet().equals(Set.of(ShipClassId.COLONISTS_TRANSPORTER));
	}

	public Ships detach(Ships ships) {
		return update(ships, (a, b) -> a - b);
	}

	public Ships merge(Ships ships) {
		return update(ships, Integer::sum);
	}

	private Ships update(Ships ships, BiFunction<Integer, Integer, Integer> operation) {
		Map<ShipClassId, Integer> updatedShips = new HashMap<>(this.counts);
		ships.counts.forEach((key, value) -> {
			int newCount = operation.apply(updatedShips.getOrDefault(key, 0), value);
			if (newCount < 0) {
				throw new IllegalStateException(
						"Can't detach " + value + " " + key + " from " + updatedShips.getOrDefault(key, 0) + "!");
			}
			else if (newCount > 0) {
				updatedShips.put(key, newCount);
			}
			else {
				updatedShips.remove(key);
			}
		});
		return new Ships(updatedShips);
	}
}
