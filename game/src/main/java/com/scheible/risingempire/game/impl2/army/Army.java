package com.scheible.risingempire.game.impl2.army;

import java.util.List;
import java.util.Optional;

import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.colony.AnnexationStatusView;
import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.common.Command;

/**
 * @author sj
 */
public class Army {

	private final ControlledSystemProvider controlledSystemProvider;

	public Army(ControlledSystemProvider controlledSystemProvider) {
		this.controlledSystemProvider = controlledSystemProvider;
		this.controlledSystemProvider.hashCode(); // to make PMD happy for now...
	}

	public void annexSystems(List<Annex> commands) {
	}

	public boolean annexable(Player player, Position system) {
		return false;
	}

	public Optional<AnnexationStatusView> annexationStatus(Position system) {
		return Optional.empty();
	}

	public sealed interface ArmyCommand extends Command {

	}

	public record Annex(Player player, Position system, boolean skip) implements ArmyCommand {

	}

}
