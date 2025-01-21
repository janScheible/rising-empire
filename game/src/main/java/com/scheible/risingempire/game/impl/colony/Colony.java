package com.scheible.risingempire.game.impl.colony;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;
import static java.util.Collections.unmodifiableMap;

/**
 * @author sj
 */
public class Colony {

	private final Player player;

	private int population;

	private DesignSlot spaceDock;

	private final Map<ProductionArea, Allocation> allocations = new EnumMap<>(ProductionArea.class);

	public Colony(Player player, int population, DesignSlot spaceDockDesign) {
		this.player = player;
		this.population = population;
		this.spaceDock = spaceDockDesign;
		this.allocations.putAll(Map.of( //
				ProductionArea.DEFENCE, new Allocation(0, "None"), //
				ProductionArea.ECOLOGY, new Allocation(25, "Clean"), //
				ProductionArea.INDUSTRY, new Allocation(0, "None"), //
				ProductionArea.SHIP, new Allocation(75, "5 r"), //
				ProductionArea.TECHNOLOGY, new Allocation(0, "0 RP")));
	}

	public void adjustRation(ProductionArea area, int percentage) {
		adjustRationInternal(area, percentage, new HashMap<>());
	}

	static void adjustRationInternal(ProductionArea area, int percentage, Map<ProductionArea, Integer> ratios) {
		int difference = percentage - ratios.get(area);

		if (difference > 0) {
			while (difference != 0) {
				Entry<ProductionArea, Integer> highestOtherRatio = ratios.entrySet()
					.stream()
					.filter(areaAndAmount -> areaAndAmount.getKey() != area)
					.sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
					.findFirst()
					.get();
				if (highestOtherRatio.getValue() > difference) {
					ratios.put(highestOtherRatio.getKey(), highestOtherRatio.getValue() - difference);
					difference = 0;
				}
				else {
					difference -= ratios.get(highestOtherRatio.getKey());
					ratios.put(highestOtherRatio.getKey(), 0);
				}
			}
		}
		else if (difference < 0) {
			Entry<ProductionArea, Integer> lowestOtherRatio = ratios.entrySet()
				.stream()
				.filter(areaAndAmount -> areaAndAmount.getKey() != area)
				.sorted((a, b) -> Integer.compare(a.getValue(), b.getValue()))
				.findFirst()
				.get();
			ratios.put(lowestOtherRatio.getKey(), ratios.get(lowestOtherRatio.getKey()) - difference);
		}

		ratios.put(area, percentage);
	}

	public void build(DesignSlot design) {
		this.spaceDock = design;
	}

	public Map<ProductionArea, Allocation> getAllocations() {
		return unmodifiableMap(this.allocations);
	}

	public DesignSlot getSpaceDock() {
		return this.spaceDock;
	}

	public Player getPlayer() {
		return this.player;
	}

	public int getPopulation() {
		return this.population;
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("player", this.player)
			.add("population", this.population)
			.add("spaceDock", this.spaceDock)
			.add("allocations", this.allocations)
			.toString();
	}

}
