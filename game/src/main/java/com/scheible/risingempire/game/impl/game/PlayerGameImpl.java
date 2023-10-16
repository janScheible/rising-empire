package com.scheible.risingempire.game.impl.game;

import java.util.Map;
import java.util.Optional;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipTypeId;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;
import com.scheible.risingempire.game.api.view.universe.Player;

/**
 *
 * @author sj
 */
public class PlayerGameImpl implements PlayerGame {

	private final Player player;
	private final GameImpl game;

	public PlayerGameImpl(final Player player, final GameImpl game) {
		this.player = player;
		this.game = game;
	}

	@Override
	public TurnStatus finishTurn() {
		return game.finishTurn(player);
	}

	@Override
	public void nextShipType(final ColonyId colonyId) {
		game.nextShipType(player, colonyId);
	}

	@Override
	public void colonizeSystem(final SystemId systemId, final FleetId fleetId, final boolean skip) {
		game.colonizeSystem(player, fleetId, skip);
	}

	@Override
	public void annexSystem(final ColonyId colonyId, final FleetId fleetId, final boolean skip) {
		game.annexSystem(player, fleetId, skip);
	}

	@Override
	public void deployFleet(final FleetId fleetId, final SystemId destinationId, final Map<ShipTypeId, Integer> ships) {
		game.deployFleet(player, fleetId, destinationId, ships);
	}

	@Override
	public void selectTech(final TechId techId) {
		game.selectTech(player, techId);
	}

	@Override
	public Optional<Integer> calcEta(final FleetId fleetId, final SystemId destinationId,
			final Map<ShipTypeId, Integer> ships) {
		return game.calcEta(player, fleetId, destinationId, ships);
	}

	@Override
	public GameView getView() {
		return game.getGameState(player);
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
