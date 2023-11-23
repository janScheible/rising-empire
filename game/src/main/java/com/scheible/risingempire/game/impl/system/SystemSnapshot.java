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

/**
 * @author sj
 */
public class SystemSnapshot {

	private final SystemId id;

	private final Optional<Integer> firstSeenTurn;

	private final int lastSeenTurn;

	private final boolean known;

	private final Location location;

	private final StarType starType;

	private final Optional<PlanetType> planetType;

	private final Optional<PlanetSpecial> planetSpecial;

	private final Optional<String> starName;

	private final Optional<Integer> planetMaxPopulation;

	private final Optional<Player> colonyPlayer;

	private final Optional<Integer> colonyPopulation;

	private SystemSnapshot(SystemId id, Optional<Integer> firstSeenTurn, int lastSeenTurn, boolean known,
			Location location, StarType starType, Optional<PlanetType> planetType,
			Optional<PlanetSpecial> planetSpecial, Optional<String> starName, Optional<Integer> planetMaxPopulation,
			Optional<Player> colonyPlayer, Optional<Integer> colonyPopulation) {
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

	public static SystemSnapshot forKnown(int round, System system) {
		return new SystemSnapshot(system.getId(), Optional.empty(), round, true, system.getLocation(),
				system.getStarType(), Optional.of(system.getPlanetType()), Optional.of(system.getPlanetSpecial()),
				Optional.of(system.getName()), Optional.of(system.getPlanetMaxPopulation()),
				system.getColony().map(Colony::getPlayer), system.getColony().map(Colony::getPopulation));
	}

	public static SystemSnapshot forUnknown(int round, System system) {
		return new SystemSnapshot(system.getId(), Optional.empty(), round, false, system.getLocation(),
				system.getStarType(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
				Optional.empty(), Optional.empty());
	}

	public static SystemSnapshot withFirstSeenTurn(SystemSnapshot snapshot, int firstSeenTurn) {
		if (!snapshot.isKnown()) {
			throw new IllegalStateException("Doesn't make sense to set the first seen turn for an unknown system!");
		}

		return new SystemSnapshot(snapshot.id, Optional.of(firstSeenTurn), snapshot.lastSeenTurn, snapshot.known,
				snapshot.location, snapshot.starType, snapshot.planetType, snapshot.planetSpecial, snapshot.starName,
				snapshot.planetMaxPopulation, snapshot.colonyPlayer, snapshot.colonyPopulation);
	}

	public SystemId getId() {
		return this.id;
	}

	public Optional<Integer> getFirstSeenTurn() {
		return this.firstSeenTurn;
	}

	public int getLastSeenTurn() {
		return this.lastSeenTurn;
	}

	public boolean wasJustExplored(int round) {
		return getFirstSeenTurn().filter(fst -> fst == this.lastSeenTurn && fst == round).isPresent();
	}

	public boolean isKnown() {
		return this.known;
	}

	public Location getLocation() {
		return this.location;
	}

	public StarType getStarType() {
		return this.starType;
	}

	public Optional<PlanetType> getPlanetType() {
		return this.planetType;
	}

	public Optional<PlanetSpecial> getPlanetSpecial() {
		return this.planetSpecial;
	}

	public Optional<String> getStarName() {
		return this.starName;
	}

	public Optional<Integer> getPlanetMaxPopulation() {
		return this.planetMaxPopulation;
	}

	public Optional<Player> getColonyPlayer() {
		return this.colonyPlayer;
	}

	public Optional<Integer> getColonyPopulation() {
		return this.colonyPopulation;
	}

	@Override
	public String toString() {
		return Objects2.toStringBuilder(getClass())
			.add("id", this.id)
			.add("firstSeenTurn", this.firstSeenTurn)
			.add("lastSeenTurn", this.lastSeenTurn)
			.add("known", this.known)
			.add("location", this.location)
			.add("starType", this.starType)
			.add("planetType", getPlanetType())
			.add("planetSpecial", getPlanetSpecial())
			.add("starName", getStarName())
			.add("planetMaxPopulation", getPlanetMaxPopulation())
			.add("colonyPlayer", getColonyPlayer().map(Player::toString))
			.add("colonyPopulation", getColonyPopulation())
			.toString();
	}

}
