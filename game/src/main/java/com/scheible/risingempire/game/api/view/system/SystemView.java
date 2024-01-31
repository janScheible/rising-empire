package com.scheible.risingempire.game.api.view.system;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.universe.Location;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.ColonyView;

/**
 * @author sj
 */
public class SystemView {

	private final SystemId id;

	private final boolean justExplored;

	private final Location location;

	private final StarType starType;

	private final boolean small;

	private final boolean homeSystem;

	private final Optional<Integer> range;

	private final Optional<PlanetType> planetType;

	private final Optional<PlanetSpecial> planetSpecial;

	private final Optional<Integer> seenInTurn;

	private final Optional<String> starName;

	private final Optional<Integer> planetMaxPopulation;

	private final Optional<ColonyView> colony;

	private final Optional<Integer> fleetRange;

	private final Optional<Integer> extendedFleetRange;

	private final Optional<Integer> scannerRange;

	private final Optional<Boolean> colonizable;

	private final Optional<Boolean> colonizeCommand;

	public SystemView(SystemId id, boolean justExplored, Location location, StarType starType, boolean small,
			boolean homeSystem, Optional<Integer> range, Optional<PlanetType> planetType,
			Optional<PlanetSpecial> planetSpecial, Optional<Integer> seenInTurn, Optional<String> starName,
			Optional<Integer> planetMaxPopulation, Optional<ColonyView> colony, Optional<Integer> fleetRange,
			Optional<Integer> extendedFleetRange, Optional<Integer> scannerRange, Optional<Boolean> colonizable,
			Optional<Boolean> colonizeCommand) {
		this.id = id;

		this.justExplored = justExplored;
		this.location = location;
		this.starType = starType;
		this.small = small;
		this.homeSystem = homeSystem;
		this.range = range;
		this.planetType = planetType;
		this.planetSpecial = planetSpecial;
		this.seenInTurn = seenInTurn;
		this.starName = starName;
		this.planetMaxPopulation = planetMaxPopulation;
		this.colony = colony;
		this.fleetRange = fleetRange;
		this.extendedFleetRange = extendedFleetRange;
		this.scannerRange = scannerRange;

		if (Boolean.TRUE.equals(colonizable.orElse(Boolean.FALSE)) && colonizeCommand.isEmpty()) {
			throw new IllegalArgumentException("colonizationCommand can't be absent when canColonize = true!");
		}
		this.colonizable = colonizable;
		this.colonizeCommand = colonizeCommand;
	}

	public SystemId getId() {
		return this.id;
	}

	public boolean wasJustExplored() {
		return this.justExplored;
	}

	public Location getLocation() {
		return this.location;
	}

	public StarType getStarType() {
		return this.starType;
	}

	public boolean isSmall() {
		return this.small;
	}

	public boolean isHomeSystem() {
		return this.homeSystem;
	}

	public Optional<Integer> getRange() {
		return this.range;
	}

	public Optional<PlanetType> getPlanetType() {
		return this.planetType;
	}

	public Optional<PlanetSpecial> getPlanetSpecial() {
		return this.planetSpecial;
	}

	public Optional<Integer> getSeenInTurn() {
		return this.seenInTurn;
	}

	public Optional<String> getStarName() {
		return this.starName;
	}

	public Optional<Integer> getPlanetMaxPopulation() {
		return this.planetMaxPopulation;
	}

	public Optional<ColonyView> getColonyView() {
		return this.colony;
	}

	public Optional<ColonyView> getColonyView(Player player) {
		return this.colony.filter(c -> c.getPlayer().equals(player));
	}

	public Optional<Integer> getFleetRange() {
		return this.fleetRange;
	}

	public boolean hasFleetRange() {
		return this.fleetRange.isPresent();
	}

	public Optional<Integer> getExtendedFleetRange() {
		return this.extendedFleetRange;
	}

	public boolean hasExtendedFleetRange() {
		return this.extendedFleetRange.isPresent();
	}

	public Optional<Integer> getScannerRange() {
		return this.scannerRange;
	}

	public boolean isColonizable() {
		return this.colonizable.orElse(Boolean.FALSE);
	}

	public Optional<Boolean> hasColonizeCommand() {
		return this.colonizeCommand;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj != null && getClass().equals(obj.getClass())) {
			SystemView other = (SystemView) obj;
			return Objects.equals(this.id, other.id);
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		StringJoiner values = new StringJoiner(", ", "SystemView[", "]").add("id=" + this.id)
			.add("justExplored=" + this.justExplored)
			.add("location=" + this.location)
			.add("starType=" + this.starType)
			.add("small=" + this.small);

		if (this.homeSystem) {
			values.add("homeSytem=" + this.homeSystem);
		}

		if (this.range.isPresent()) {
			values.add("range=" + this.range.get());
		}

		if (this.starName.isPresent()) {
			values.add("starName=" + this.starName.get());
		}

		if (this.planetType.isPresent()) {
			values.add("planetType=" + this.planetType.get());
		}

		if (this.planetSpecial.isPresent()) {
			values.add("planetSpecial=" + this.planetSpecial.get());
		}

		if (this.seenInTurn.isPresent()) {
			values.add("seenInTurn=" + this.seenInTurn.get());
		}

		if (this.colonizable.isPresent()) {
			values.add("colonizable=" + this.colonizable.get()).add("colonizeCommand=" + this.colonizeCommand.get());
		}

		if (this.colony.isPresent()) {
			String colonyString = this.colony.get().toString();
			values.add("colony=" + colonyString.substring(colonyString.indexOf('[')));
		}

		addRanges(values);

		return values.toString();
	}

	private void addRanges(StringJoiner values) {
		if (this.fleetRange != null) {
			values.add("fleetRange=" + this.fleetRange);
		}
		if (this.extendedFleetRange != null) {
			values.add("extendedFleetRange=" + this.extendedFleetRange);
		}

		if (this.scannerRange != null) {
			values.add("scannerRange=" + this.scannerRange);
		}
	}

}
