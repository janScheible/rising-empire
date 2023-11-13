package com.scheible.risingempire.game.impl.colony;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.ship.DesignSlot;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;
import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;

/**
 * @author sj
 */
public class Colony {

	private final Player player;

	private int population;

	private DesignSlot spaceDock;

	private final Map<ProductionArea, Integer> ratios = new EnumMap<>(ProductionArea.class);

	public Colony(final Player player, final int population, final DesignSlot spaceDockDesign) {
		this.player = player;
		this.population = population;
		this.spaceDock = spaceDockDesign;
		ratios.putAll(Stream.of(ProductionArea.values()).collect(Collectors.toMap(identity(), a -> 20)));
	}

	public void adjustRation(final ProductionArea area, final int percentage) {
		adjustRationInternal(area, percentage, ratios);
	}

	static void adjustRationInternal(final ProductionArea area, final int percentage,
			final Map<ProductionArea, Integer> ratios) {
		int difference = percentage - ratios.get(area);

		if (difference > 0) {
			while (difference != 0) {
				final Entry<ProductionArea, Integer> highestOtherRatio = ratios.entrySet()
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
			final Entry<ProductionArea, Integer> lowestOtherRatio = ratios.entrySet()
				.stream()
				.filter(areaAndAmount -> areaAndAmount.getKey() != area)
				.sorted((a, b) -> Integer.compare(a.getValue(), b.getValue()))
				.findFirst()
				.get();
			ratios.put(lowestOtherRatio.getKey(), ratios.get(lowestOtherRatio.getKey()) - difference);
		}

		ratios.put(area, percentage);
	}

	public void build(final DesignSlot design) {
		this.spaceDock = design;
	}

	public Map<ProductionArea, Integer> getRatios() {
		return unmodifiableMap(ratios);
	}

	public DesignSlot getSpaceDock() {
		return spaceDock;
	}

	public Player getPlayer() {
		return player;
	}

	public int getPopulation() {
		return population;
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("player", player)
			.add("population", population)
			.add("spaceDock", spaceDock)
			.add("ratios", ratios)
			.toString();
	}

}
