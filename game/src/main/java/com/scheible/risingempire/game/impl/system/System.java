package com.scheible.risingempire.game.impl.system;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import com.scheible.risingempire.game.api.view.system.PlanetSpecial;
import com.scheible.risingempire.game.api.view.system.PlanetType;
import com.scheible.risingempire.game.api.view.system.StarType;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.universe.Location;
import com.scheible.risingempire.game.api.view.universe.Player;
import com.scheible.risingempire.game.impl.colony.Colony;
import com.scheible.risingempire.game.impl.ship.DesignSlot;
import com.scheible.risingempire.util.jdk.Objects2;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

/**
 * @author sj
 */
public class System implements SystemOrb {

	private final SystemId id;

	private final String starName;

	private final Location location;

	private final StarType starType;

	private final Player homeSystem;

	private final PlanetType planetType;

	private final PlanetSpecial planetSpecial;

	private final int planetMaxPopulation;

	private Colony colony = null;

	private System(String starName, Location location, StarType starType, Player homeSystem, PlanetType planetType,
			PlanetSpecial planetSpecial, int planetMaxPopulation) {
		this.id = new SystemId("s" + location.getX() + "x" + location.getY());

		this.starName = starName;
		this.location = location;
		this.starType = starType;
		this.homeSystem = homeSystem;
		this.planetType = planetType;
		this.planetSpecial = planetSpecial;
		this.planetMaxPopulation = planetMaxPopulation;
	}

	public System(String starName, Location location, StarType starType, PlanetType planetType,
			PlanetSpecial planetSpecial, int planetMaxPopulation) {
		this(starName, location, starType, null, planetType, planetSpecial, planetMaxPopulation);
	}

	public static System createHomeSystem(String starName, Location location, StarType starType, PlanetType planetType,
			PlanetSpecial planetSpecial, int planetMaxPopulation, Player player, DesignSlot spaceDockDesign) {
		System system = new System(starName, location, starType, player, planetType, planetSpecial,
				planetMaxPopulation);
		system.colony = new Colony(player, planetMaxPopulation / 2, spaceDockDesign);
		return system;
	}

	public void colonize(Player player, DesignSlot spaceDockDesign) {
		if (this.colony != null) {
			throw new IllegalStateException(
					"The system '" + this.starName + "' is already colonized by '" + this.colony.getPlayer() + "!");
		}

		this.colony = new Colony(player, this.planetMaxPopulation / 2, spaceDockDesign);
	}

	public void annex(Player player, DesignSlot spaceDockDesign) {
		this.colony = new Colony(player, this.planetMaxPopulation / 2, spaceDockDesign);
	}

	/**
	 * @return if system has colony of given player 0 is return, otherwise the shortest
	 * distance to any star with a colony of the given player
	 */
	public int calcRange(Player player, Collection<System> systems) {
		int range = Integer.MAX_VALUE;

		for (System other : systems) {
			if (other.getColony(player).isPresent()) {
				range = Math.min(range, (int) other.getLocation().getDistance(this.location));
			}
		}

		return range == Integer.MAX_VALUE ? 0 : range;
	}

	@Override
	public SystemId getId() {
		return this.id;
	}

	public boolean isHomeSystem(Player player) {
		return player == this.homeSystem;
	}

	@Override
	public String getName() {
		return this.starName;
	}

	@Override
	public Location getLocation() {
		return this.location;
	}

	public StarType getStarType() {
		return this.starType;
	}

	public PlanetType getPlanetType() {
		return this.planetType;
	}

	public PlanetSpecial getPlanetSpecial() {
		return this.planetSpecial;
	}

	public int getPlanetMaxPopulation() {
		return this.planetMaxPopulation;
	}

	public Optional<Colony> getColony() {
		return Optional.ofNullable(this.colony);
	}

	public Optional<Colony> getColony(Player player) {
		return getColony().filter(c -> c.getPlayer() == player);
	}

	@Override
	public boolean equals(Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(this.id, other.id));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("id/starName", this.starName, "'")
			.add("location", this.location)
			.add("starType", this.starType)
			.add("homeSystem", this.homeSystem)
			.add("planetType", this.planetType)
			.add("planetSpecial", this.planetSpecial)
			.add("planetMaxPopulation", this.planetMaxPopulation)
			.add("colony", getColony().map(Colony::toString))
			.toString();
	}

}
