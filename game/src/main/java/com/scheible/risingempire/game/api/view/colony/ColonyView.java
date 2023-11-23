package com.scheible.risingempire.game.api.view.colony;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.view.ship.ShipTypeView;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.api.view.universe.Race;

/**
 * @author sj
 */
public class ColonyView {

	private final ColonyId id;

	private final Player player;

	private final Race race;

	private final int population;

	private final Optional<ShipTypeView> spaceDock;

	private final Optional<Map<ProductionArea, Integer>> ratios;

	private final Optional<AnnexationStatusView> annexationStatus;

	public ColonyView(ColonyId id, Player player, Race race, int population, Optional<ShipTypeView> spaceDock,
			Optional<Map<ProductionArea, Integer>> ratios, Optional<AnnexationStatusView> annexationStatus) {
		this.id = id;

		this.player = player;
		this.race = race;
		this.population = population;
		this.spaceDock = spaceDock;
		this.ratios = ratios.map(EnumMap::new).map(Collections::unmodifiableMap);
		this.annexationStatus = annexationStatus;
	}

	public Optional<ShipTypeView> getSpaceDock() {
		return this.spaceDock;
	}

	public Optional<Map<ProductionArea, Integer>> getRatios() {
		return this.ratios;
	}

	public ColonyId getId() {
		return this.id;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Race getRace() {
		return this.race;
	}

	public int getPopulation() {
		return this.population;
	}

	public Optional<AnnexationStatusView> getAnnexationStatus() {
		return this.annexationStatus;
	}

	@Override
	public String toString() {
		StringJoiner values = new StringJoiner(", ", "ColonyView[", "]").add("player=" + this.player)
			.add("race=" + this.race)
			.add("population=" + this.population);

		if (this.spaceDock.isPresent()) {
			values.add("spaceDock=" + this.spaceDock.get());
		}

		if (this.ratios.isPresent()) {
			values.add("ratios=" + this.ratios.get());
		}

		if (this.annexationStatus.isPresent()) {
			values.add("annexationStatus=" + this.annexationStatus.get());
		}

		return values.toString();
	}

}
