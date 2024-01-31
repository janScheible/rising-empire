package com.scheible.risingempire.game.api.view.ship;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
public class ShipsView {

	private final HashMap<ShipTypeView, Integer> map = new HashMap<>();

	public ShipsView() {
	}

	public ShipsView(Map<ShipTypeView, Integer> ships) {
		ships.entrySet().stream().forEach(typeAndCount -> {
			if (typeAndCount.getValue() < 0) {
				throw new IllegalArgumentException(
						"Count of '" + typeAndCount.getKey().getName() + "' can't be negative!");
			}

			this.map.put(typeAndCount.getKey(), typeAndCount.getValue());
		});
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	public ShipsView getPartByName(String name, int count) {
		Entry<ShipTypeView, Integer> typeAndCount = this.map.entrySet()
			.stream()
			.filter(typeWithCount -> typeWithCount.getKey().getName().equals(name))
			.findFirst()
			.orElseThrow();
		if (count > typeAndCount.getValue()) {
			throw new IllegalArgumentException(
					"Can't get " + count + " of " + typeAndCount.getValue() + "'" + name + "'!");
		}

		ShipsView ships = new ShipsView();
		ships.map.put(typeAndCount.getKey(), count);
		return ships;
	}

	public int getCountByType(ShipTypeView type) {
		return this.map.entrySet()
			.stream()
			.filter(typeAndCount -> typeAndCount.getKey().equals(type))
			.findFirst()
			.orElseThrow()
			.getValue();
	}

	public int getCountByName(String name) {
		return this.map.entrySet()
			.stream()
			.filter(typeAndCount -> typeAndCount.getKey().getName().equals(name))
			.findFirst()
			.orElseThrow()
			.getValue();
	}

	public Set<Entry<ShipTypeView, Integer>> getTypesWithCount() {
		return unmodifiableSet(this.map.entrySet());
	}

	public Set<ShipTypeView> getTypes() {
		return unmodifiableSet(this.map.keySet());
	}

	public Set<String> getTypeNames() {
		return this.map.keySet().stream().map(ShipTypeView::getName).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return this.map.entrySet()
			.stream()
			.map(typeWithCount -> Map.entry(typeWithCount.getKey().getName(), typeWithCount.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue))
			.toString();
	}

}
