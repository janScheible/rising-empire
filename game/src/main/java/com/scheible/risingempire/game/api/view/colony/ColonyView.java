package com.scheible.risingempire.game.api.view.colony;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.universe.Race;
import com.scheible.risingempire.game.api.view.ship.ShipTypeView;

import static java.util.Collections.unmodifiableMap;

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

	private final Optional<AnnexationStatus> annexationStatus;

	private final Map<ColonyId, Integer> colonistTransfers;

	private final Optional<ColonyId> relocationTarget;

	public ColonyView(ColonyId id, Player player, Race race, int population, Optional<ShipTypeView> spaceDock,
			Optional<Map<ProductionArea, Integer>> ratios, Optional<AnnexationStatus> annexationStatus,
			Map<ColonyId, Integer> colonistTransfers, Optional<ColonyId> relocationTarget) {
		this.id = id;

		this.player = player;
		this.race = race;
		this.population = population;
		this.spaceDock = spaceDock;
		this.ratios = ratios.map(EnumMap::new).map(Collections::unmodifiableMap);
		this.annexationStatus = annexationStatus;
		this.colonistTransfers = unmodifiableMap(colonistTransfers);
		this.relocationTarget = relocationTarget;
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

	public Optional<AnnexationStatus> getAnnexationStatus() {
		return this.annexationStatus;
	}

	public Map<ColonyId, Integer> getColonistTransfers() {
		return this.colonistTransfers;
	}

	public Optional<ColonyId> getRelocationTarget() {
		return this.relocationTarget;
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

		if (!this.colonistTransfers.isEmpty()) {
			values.add("colonistTransfers=" + this.colonistTransfers);
		}

		if (this.relocationTarget.isPresent()) {
			values.add("relocationTarget=" + this.relocationTarget.get());
		}

		return values.toString();
	}

}
