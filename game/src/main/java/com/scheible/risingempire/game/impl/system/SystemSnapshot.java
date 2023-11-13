package com.scheible.risingempire.game.impl.system;

import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.colony.Colony;
import com.scheible.risingempire.util.jdk.Objects2;
import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * @author sj
 */
public class SystemSnapshot {

	private final SystemId id;

	@Nullable
	private final Integer firstSeenTurn;

	private final int lastSeenTurn;

	private final boolean known;

	private final Location location;

	private final StarType starType;

	@Nullable
	private final PlanetType planetType;

	@Nullable
	private final PlanetSpecial planetSpecial;

	@Nullable
	private final String starName;

	@Nullable
	private final Integer planetMaxPopulation;

	@Nullable
	private final Player colonyPlayer;

	@Nullable
	private final Integer colonyPopulation;

	private SystemSnapshot(final SystemId id, final Integer firstSeenTurn, final int lastSeenTurn, final boolean known,
			final Location location, final StarType starType, @Nullable final PlanetType planetType,
			@Nullable final PlanetSpecial planetSpecial, @Nullable final String starName,
			@Nullable final Integer planetMaxPopulation, @Nullable final Player colonyPlayer,
			@Nullable final Integer colonyPopulation) {
		this.id = id;
		this.firstSeenTurn = firstSeenTurn;
		this.lastSeenTurn = lastSeenTurn;
		this.known = known;
		this.location = location;
		this.starType = starType;
		this.planetType = planetType;
		this.planetSpecial = planetSpecial;

		this.starName = starName;
		this.planetMaxPopulation = planetMaxPopulation;
		this.colonyPlayer = colonyPlayer;
		this.colonyPopulation = colonyPopulation;
	}

	public static SystemSnapshot forKnown(final int round, final System system) {
		return new SystemSnapshot(system.getId(), null, round, true, system.getLocation(), system.getStarType(),
				system.getPlanetType(), system.getPlanetSpecial(), system.getName(), system.getPlanetMaxPopulation(),
				system.getColony().map(Colony::getPlayer).orElse(null),
				system.getColony().map(Colony::getPopulation).orElse(null));
	}

	public static SystemSnapshot forUnknown(final int round, final System system) {
		return new SystemSnapshot(system.getId(), null, round, false, system.getLocation(), system.getStarType(), null,
				null, null, null, null, null);
	}

	public static SystemSnapshot withFirstSeenTurn(final SystemSnapshot snapshot, final int firstSeenTurn) {
		if (!snapshot.isKnown()) {
			throw new IllegalStateException("Doesn't make sense to set the first seen turn for an unknown system!");
		}

		return new SystemSnapshot(snapshot.id, firstSeenTurn, snapshot.lastSeenTurn, snapshot.known, snapshot.location,
				snapshot.starType, snapshot.planetType, snapshot.planetSpecial, snapshot.starName,
				snapshot.planetMaxPopulation, snapshot.colonyPlayer, snapshot.colonyPopulation);
	}

	public SystemId getId() {
		return id;
	}

	public Optional<Integer> getFirstSeenTurn() {
		return Optional.ofNullable(firstSeenTurn);
	}

	public int getLastSeenTurn() {
		return lastSeenTurn;
	}

	public boolean wasJustExplored(final int round) {
		return getFirstSeenTurn().filter(fst -> fst == lastSeenTurn && fst == round).isPresent();
	}

	public boolean isKnown() {
		return known;
	}

	public Location getLocation() {
		return location;
	}

	public StarType getStarType() {
		return starType;
	}

	public Optional<PlanetType> getPlanetType() {
		return Optional.ofNullable(planetType);
	}

	public Optional<PlanetSpecial> getPlanetSpecial() {
		return Optional.ofNullable(planetSpecial);
	}

	public Optional<String> getStarName() {
		return Optional.ofNullable(starName);
	}

	public Optional<Integer> getPlanetMaxPopulation() {
		return Optional.ofNullable(planetMaxPopulation);
	}

	public Optional<Player> getColonyPlayer() {
		return Optional.ofNullable(colonyPlayer);
	}

	public Optional<Integer> getColonyPopulation() {
		return Optional.ofNullable(colonyPopulation);
	}

	@Override
	public String toString() {
		return Objects2.toStringBuilder(getClass())
			.add("id", id)
			.add("firstSeenTurn", firstSeenTurn)
			.add("lastSeenTurn", lastSeenTurn)
			.add("known", known)
			.add("location", location)
			.add("starType", starType)
			.add("planetType", getPlanetType())
			.add("planetSpecial", getPlanetSpecial())
			.add("starName", getStarName())
			.add("planetMaxPopulation", getPlanetMaxPopulation())
			.add("colonyPlayer", getColonyPlayer().map(Player::toString))
			.add("colonyPopulation", getColonyPopulation())
			.toString();
	}

}
