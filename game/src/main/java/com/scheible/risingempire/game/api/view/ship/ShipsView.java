package com.scheible.risingempire.game.api.view.ship;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.annotation.StagedRecordBuilder;
import com.scheible.risingempire.game.api.view.ship.ShipsViewBuilder.ShipsStage;

import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

/**
 * @author sj
 */
@StagedRecordBuilder
public record ShipsView(Map<ShipTypeView, Integer> ships) {

	public ShipsView {
		ships = unmodifiableMap(ships);

		for (Entry<ShipTypeView, Integer> typeAndCount : ships.entrySet()) {
			if (typeAndCount.getValue() < 0) {
				throw new IllegalArgumentException(
						"Count of '" + typeAndCount.getKey().name() + "' can't be negative!");
			}
		}
	}

	public static ShipsStage builder() {
		return ShipsViewBuilder.builder();
	}

	public boolean empty() {
		return this.ships.isEmpty();
	}

	public ShipsView partByName(String name, int count) {
		Entry<ShipTypeView, Integer> typeAndCount = this.ships.entrySet()
			.stream()
			.filter(typeWithCount -> typeWithCount.getKey().name().equals(name))
			.findFirst()
			.orElseThrow();
		if (count > typeAndCount.getValue()) {
			throw new IllegalArgumentException(
					"Can't get " + count + " of " + typeAndCount.getValue() + "'" + name + "'!");
		}

		return ShipsView.builder().ships(Map.of(typeAndCount.getKey(), count)).build();
	}

	public int countByType(ShipTypeView type) {
		return this.ships.entrySet()
			.stream()
			.filter(typeAndCount -> typeAndCount.getKey().equals(type))
			.findFirst()
			.orElseThrow()
			.getValue();
	}

	public int countByName(String name) {
		return this.ships.entrySet()
			.stream()
			.filter(typeAndCount -> typeAndCount.getKey().name().equals(name))
			.findFirst()
			.orElseThrow()
			.getValue();
	}

	public Set<Entry<ShipTypeView, Integer>> typesWithCount() {
		return unmodifiableSet(this.ships.entrySet());
	}

	public Set<ShipTypeView> types() {
		return unmodifiableSet(this.ships.keySet());
	}

	public Set<String> typeNames() {
		return this.ships.keySet().stream().map(ShipTypeView::name).collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		return this.ships.entrySet()
			.stream()
			.map(typeWithCount -> Map.entry(typeWithCount.getKey().name(), typeWithCount.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue))
			.toString();
	}

}
