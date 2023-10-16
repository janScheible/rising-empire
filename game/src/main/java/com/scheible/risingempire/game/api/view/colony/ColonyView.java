package com.scheible.risingempire.game.api.view.colony;

import static java.util.Collections.unmodifiableMap;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 *
 * @author sj
 */
public class ColonyView {

	private final ColonyId id;

	private final Player player;
	private final Race race;
	private final Integer population;
	@Nullable
	private final ShipTypeView spaceDock;
	@Nullable
	private final Map<ProductionArea, Integer> ratios;
	@Nullable
	private final AnnexationStatusView annexationStatus;

	final ColonyManager colonyManager;

	public ColonyView(final ColonyId id, final Player player, final Race race, final Integer population,
			@Nullable final ShipTypeView spaceDock, @Nullable final Map<ProductionArea, Integer> ratios,
			@Nullable final AnnexationStatusView annexationStatus, final ColonyManager colonyManager) {
		this.id = id;

		this.player = player;
		this.race = race;
		this.population = population;
		this.spaceDock = spaceDock;
		this.ratios = ratios != null ? new EnumMap<>(ratios) : null;
		this.annexationStatus = annexationStatus;

		this.colonyManager = colonyManager;
	}

	public void adjustRatio(final ProductionArea area, final int percentage) {
		if (percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException(
					"The percentage " + percentage + " for the area " + area + " is not valid!");
		}

		ratios.putAll(colonyManager.adjustRatio(player, id, area, percentage));
	}

	public Optional<ShipTypeView> getSpaceDock() {
		return Optional.ofNullable(spaceDock);
	}

	public Optional<Map<ProductionArea, Integer>> getRatios() {
		return Optional.ofNullable(ratios != null ? unmodifiableMap(ratios) : null);
	}

	public ColonyId getId() {
		return id;
	}

	public Player getPlayer() {
		return player;
	}

	public Race getRace() {
		return race;
	}

	public Integer getPopulation() {
		return population;
	}

	public Optional<AnnexationStatusView> getAnnexationStatus() {
		return Optional.ofNullable(annexationStatus);
	}

	@Override
	public String toString() {
		final StringJoiner values = new StringJoiner(", ", "ColonyView[", "]").add("player=" + player)
				.add("race=" + race);

		if (population != null) {
			values.add("population=" + population);
		}

		if (spaceDock != null) {
			values.add("spaceDock=" + spaceDock);
		}

		if (ratios != null) {
			values.add("ratios=" + ratios);
		}

		if (annexationStatus != null) {
			values.add("annexationStatus=" + annexationStatus);
		}

		return values.toString();
	}
}
