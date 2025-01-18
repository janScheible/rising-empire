package com.scheible.risingempire.game.impl2.colonization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.impl2.apiinternal.Credit;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.ResearchPoint;
import com.scheible.risingempire.game.impl2.apiinternal.ShipClassId;
import com.scheible.risingempire.game.impl2.common.Command;

/**
 * @author sj
 */
public class Colonization {

	private final List<Colony> colonies;

	private final ColonyFleetProvider colonyFleetProvider;

	public Colonization(ColonyFleetProvider colonyFleetProvider) {
		this.colonies = new ArrayList<>(
				List.of(new Colony(Player.BLUE, new Position("6.173", "5.026"), new ShipClassId("scout")),
						new Colony(Player.YELLOW, new Position("9.973", "5.626"), new ShipClassId("scout")),
						new Colony(Player.WHITE, new Position("4.080", "8.226"), new ShipClassId("scout"))));

		this.colonyFleetProvider = colonyFleetProvider;
		this.colonyFleetProvider.hashCode(); // to make PMD happy for now...
	}

	private Colonization(List<Colony> colonies, ColonyFleetProvider colonyFleetProvider) {
		this.colonies = colonies;
		this.colonyFleetProvider = colonyFleetProvider;
	}

	public Colonization apply(List<ColonyCommand> commands) {
		Colonization copy = new Colonization(new ArrayList<>(this.colonies), this.colonyFleetProvider);
		copy.updateColonies(commands);
		return copy;
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

	public void updateColonies(List<ColonyCommand> commands) {
		Map<Position, List<ColonyCommand>> colonyCommandMapping = commands.stream()
			.filter(ColonyCommand.class::isInstance)
			.map(ColonyCommand.class::cast)
			.collect(Collectors.groupingBy(ColonyCommand::colony));

		for (int i = 0; i < this.colonies.size(); i++) {
			Colony colony = this.colonies.get(i);
			List<ColonyCommand> colonyCommands = colonyCommandMapping.getOrDefault(colony.position(), List.of());
			Optional<ShipClassId> spaceDockShipClass = colonyCommands.stream()
				.filter(SpaceDockShipClass.class::isInstance)
				.map(SpaceDockShipClass.class::cast)
				.findFirst()
				.map(SpaceDockShipClass::shipClassId);

			Colony updatedColony = new Colony(colony.player(), colony.position(),
					spaceDockShipClass.orElse(colony.spaceDockShipClass()));
			this.colonies.set(i, updatedColony);
		}
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

	public sealed interface ColonyCommand extends ColonizationCommand {

		Position colony();

	}

	public record SpaceDockShipClass(Player player, Position colony, ShipClassId shipClassId) implements ColonyCommand {

	}

	public record AllocateResources(Player player, Position colony) implements ColonyCommand {

	}

}
