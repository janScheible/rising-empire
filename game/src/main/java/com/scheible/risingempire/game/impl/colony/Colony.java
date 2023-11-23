package com.scheible.risingempire.game.impl.colony;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.universe.Player;
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

	private final Map<ProductionArea, Integer> ratios = new EnumMap<>(ProductionArea.class);

	public Colony(Player player, int population, DesignSlot spaceDockDesign) {
		this.player = player;
		this.population = population;
		this.spaceDock = spaceDockDesign;
		this.ratios.putAll(Stream.of(ProductionArea.values()).collect(Collectors.toMap(Function.identity(), a -> 20)));
	}

	public void adjustRation(ProductionArea area, int percentage) {
		adjustRationInternal(area, percentage, this.ratios);
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

	public Map<ProductionArea, Integer> getRatios() {
		return unmodifiableMap(this.ratios);
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
			.add("ratios", this.ratios)
			.toString();
	}

}
