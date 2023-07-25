package com.scheible.risingempire.game.api.view.system;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import com.scheible.risingempire.game.api.view.colony.ColonyView;
import com.scheible.risingempire.game.api.view.universe.Location;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 *
 * @author sj
 */
public class SystemView {

	private final SystemId id;

	private final boolean justExplored;
	private final Location location;
	private final StarType starType;
	private final boolean small;
	private final boolean homeSystem;
	@Nullable
	private final Integer range;
	@Nullable
	private final PlanetType planetType;
	@Nullable
	private final PlanetSpecial planetSpecial;
	@Nullable
	private final Integer seenInTurn;
	@Nullable
	private final String starName;
	@Nullable
	private final Integer planetMaxPopulation;
	@Nullable
	private final ColonyView colony;
	@Nullable
	private final Integer fleetRange;
	@Nullable
	private final Integer extendedFleetRange;
	@Nullable
	private final Integer scannerRange;

	public SystemView(final SystemId id, final boolean justExplored, final Location location, final StarType starType,
			final boolean small, final boolean homeSystem, @Nullable final Integer range,
			@Nullable final PlanetType planetType, @Nullable final PlanetSpecial planetSpecial,
			@Nullable final Integer seenInTurn, @Nullable final String starName,
			@Nullable final Integer planetMaxPopulation, @Nullable final ColonyView colony,
			@Nullable final Integer fleetRange, @Nullable final Integer extendedFleetRange,
			@Nullable final Integer scannerRange) {
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
	}

	public SystemId getId() {
		return id;
	}

	public boolean wasJustExplored() {
		return justExplored;
	}

	public Location getLocation() {
		return location;
	}

	public StarType getStarType() {
		return starType;
	}

	public boolean isSmall() {
		return small;
	}

	public boolean isHomeSystem() {
		return homeSystem;
	}

	public Optional<Integer> getRange() {
		return Optional.ofNullable(range);
	}

	public Optional<PlanetType> getPlanetType() {
		return Optional.ofNullable(planetType);
	}

	public Optional<PlanetSpecial> getPlanetSpecial() {
		return Optional.ofNullable(planetSpecial);
	}

	public Optional<Integer> getSeenInTurn() {
		return Optional.ofNullable(seenInTurn);
	}

	public Optional<String> getStarName() {
		return Optional.ofNullable(starName);
	}

	public Optional<Integer> getPlanetMaxPopulation() {
		return Optional.ofNullable(planetMaxPopulation);
	}

	public Optional<ColonyView> getColonyView() {
		return Optional.ofNullable(colony);
	}

	public Optional<Integer> getFleetRange() {
		return Optional.ofNullable(fleetRange);
	}

	public boolean hasFleetRange() {
		return fleetRange != null;
	}

	public Optional<Integer> getExtendedFleetRange() {
		return Optional.ofNullable(extendedFleetRange);
	}

	public boolean hasExtendedFleetRange() {
		return extendedFleetRange != null;
	}

	public Optional<Integer> getScannerRange() {
		return Optional.ofNullable(scannerRange);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		} else if (obj != null && getClass().equals(obj.getClass())) {
			final SystemView other = (SystemView) obj;
			return Objects.equals(id, other.id);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		final StringJoiner values = new StringJoiner(", ", "SystemView[", "]").add("id=" + id)
				.add("justExplored=" + justExplored).add("location=" + location).add("starType=" + starType)
				.add("small=" + small);

		if (homeSystem) {
			values.add("homeSytem=" + homeSystem);
		}

		if (range != null) {
			values.add("range=" + range);
		}

		if (starName != null) {
			values.add("starName=" + starName);
		}

		if (planetType != null) {
			values.add("planetType=" + planetType);
		}

		if (planetSpecial != null) {
			values.add("planetSpecial=" + planetSpecial);
		}

		if (seenInTurn != null) {
			values.add("seenInTurn=" + seenInTurn);
		}

		if (colony != null) {
			final String colonyString = colony.toString();
			values.add("colony=" + colonyString.substring(colonyString.indexOf('[')));
		}

		addRanges(values);

		return values.toString();
	}

	private void addRanges(final StringJoiner values) {
		if (fleetRange != null) {
			values.add("fleetRange=" + fleetRange);
		}
		if (extendedFleetRange != null) {
			values.add("extendedFleetRange=" + extendedFleetRange);
		}

		if (scannerRange != null) {
			values.add("scannerRange=" + scannerRange);
		}
	}
}
