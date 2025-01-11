package com.scheible.risingempire.game.impl2.colonization;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;
import com.scheible.risingempire.game.impl2.common.Command;

/**
 * @author sj
 */
public class Colonization {

	private final Set<Colony> colonies = Set.of(new Colony(Player.BLUE, new Position("6.173", "5.026")),
			new Colony(Player.YELLOW, new Position("9.973", "5.626")),
			new Colony(Player.WHITE, new Position("4.080", "8.226")));

	private final ColonyFleetProvider colonyFleetProvider;

	public Colonization(ColonyFleetProvider colonyFleetProvider) {
		this.colonyFleetProvider = colonyFleetProvider;
		this.colonyFleetProvider.hashCode(); // to make PMD happy for now...
	}

	public boolean colonizable(Player player, Position system) {
		return false;
	}

	public Set<Colony> colonies(Player player) {
		return this.colonies.stream().filter(c -> c.player().equals(player)).collect(Collectors.toSet());
	}

	public Optional<Colony> colony(Player player, Position system) {
		return colony(system).filter(c -> c.player() == player);
	}

	public Optional<Colony> colony(Position system) {
		return this.colonies.stream().filter(c -> c.position().equals(system)).findFirst();
	}

	public void allocate(List<AllocateResources> commands) {
	}

	public void growPopulation() {
	}

	public void colonizeSystems(List<Colonize> commands) {
	}

	public void welcomeColonistTransports() {
	}

	public boolean transfareable(Player player, Position colony, int colonists) {
		return false;
	}

	public Credit buildCapacity(Player player, Position system) {
		return new Credit(0);
	}

	public ResearchPoint researchPoints(Player player) {
		return new ResearchPoint(0);
	}

	public boolean homeSystem(Player player, Position position) {
		if (player == Player.BLUE && position.equals(new Position("6.173", "5.026"))) {
			return true;
		}
		else if (player == Player.YELLOW && position.equals(new Position("9.973", "5.626"))) {
			return true;
		}
		else if (player == Player.WHITE && position.equals(new Position("4.080", "8.226"))) {
			return true;
		}
		else {
			return false;
		}
	}

	public boolean colonizeCommand(Player player, Position position) {
		return false;
	}

	public sealed interface ColonizationCommand extends Command {

	}

	public record Colonize(Player player, Position system, boolean skip) implements ColonizationCommand {

	}

	public record NextShipClass(Player player, Position colony) implements ColonizationCommand {

	}

	public record AllocateResources(Player player, Position colony) implements ColonizationCommand {

	}

}
