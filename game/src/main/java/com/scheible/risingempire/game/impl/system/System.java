package com.scheible.risingempire.game.impl.system;

import static com.scheible.risingempire.util.jdk.Objects2.toStringBuilder;

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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 *
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

	private System(final String starName, final Location location, final StarType starType, final Player homeSystem,
			final PlanetType planetType, final PlanetSpecial planetSpecial, final int planetMaxPopulation) {
		id = new SystemId("s" + location.getX() + "x" + location.getY());

		this.starName = starName;
		this.location = location;
		this.starType = starType;
		this.homeSystem = homeSystem;
		this.planetType = planetType;
		this.planetSpecial = planetSpecial;
		this.planetMaxPopulation = planetMaxPopulation;
	}

	public System(final String starName, final Location location, final StarType starType, final PlanetType planetType,
			final PlanetSpecial planetSpecial, final int planetMaxPopulation) {
		this(starName, location, starType, null, planetType, planetSpecial, planetMaxPopulation);
	}

	public static System createHomeSystem(final String starName, final Location location, final StarType starType,
			final PlanetType planetType, final PlanetSpecial planetSpecial, final int planetMaxPopulation,
			final Player player, final DesignSlot spaceDockDesign) {
		final System system = new System(starName, location, starType, player, planetType, planetSpecial,
				planetMaxPopulation);
		system.colony = new Colony(player, planetMaxPopulation / 2, spaceDockDesign);
		return system;
	}

	public void colonize(final Player player, final DesignSlot spaceDockDesign) {
		if (colony != null) {
			throw new IllegalStateException(
					"The system '" + starName + "' is already colonized by '" + colony.getPlayer() + "!");
		}

		colony = new Colony(player, planetMaxPopulation / 2, spaceDockDesign);
	}

	public void annex(final Player player, final DesignSlot spaceDockDesign) {
		colony = new Colony(player, planetMaxPopulation / 2, spaceDockDesign);
	}

	/**
	 * @return if system has colony of given player 0 is return, otherwise the shortest distance to any star with
	 *         a colony of the given player
	 */
	public int calcRange(final Player player, final Collection<System> systems) {
		int range = Integer.MAX_VALUE;

		for (final System other : systems) {
			if (other.getColony(player).isPresent()) {
				range = Math.min(range, (int) other.getLocation().getDistance(location));
			}
		}

		return range == Integer.MAX_VALUE ? 0 : range;
	}

	@Override
	public SystemId getId() {
		return id;
	}

	public boolean isHomeSystem(final Player player) {
		return player == homeSystem;
	}

	@Override
	public String getName() {
		return starName;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	public StarType getStarType() {
		return starType;
	}

	public PlanetType getPlanetType() {
		return planetType;
	}

	public PlanetSpecial getPlanetSpecial() {
		return planetSpecial;
	}

	public int getPlanetMaxPopulation() {
		return planetMaxPopulation;
	}

	public Optional<Colony> getColony() {
		return Optional.ofNullable(colony);
	}

	public Optional<Colony> getColony(final Player player) {
		return getColony().filter(c -> c.getPlayer() == player);
	}

	@SuppressFBWarnings(value = "EQ_UNUSUAL", justification = "Object2.equals() is allowed.")
	@Override
	public boolean equals(final Object obj) {
		return Objects2.equals(this, obj, other -> Objects.equals(id, other.id));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return toStringBuilder(getClass()).add("id/starName", starName, "'").add("location", location)
				.add("starType", starType).add("homeSystem", homeSystem).add("planetType", planetType)
				.add("planetSpecial", planetSpecial).add("planetMaxPopulation", planetMaxPopulation)
				.add("colony", getColony().map(Colony::toString)).toString();
	}
}
