package com.scheible.risingempire.game.impl.game;

import java.util.Optional;

import com.scheible.risingempire.game.api.PlayerGame;
import com.scheible.risingempire.game.api.TurnStatus;
import com.scheible.risingempire.game.api.universe.Player;
import com.scheible.risingempire.game.api.view.GameView;
import com.scheible.risingempire.game.api.view.colony.ColonyId;
import com.scheible.risingempire.game.api.view.colony.ProductionArea;
import com.scheible.risingempire.game.api.view.fleet.FleetId;
import com.scheible.risingempire.game.api.view.ship.ShipsView;
import com.scheible.risingempire.game.api.view.system.SystemId;
import com.scheible.risingempire.game.api.view.tech.TechId;

/**
 * @author sj
 */
public class PlayerGameImpl implements PlayerGame {

	private final Player player;

	private final GameImpl game;

	public PlayerGameImpl(Player player, GameImpl game) {
		this.player = player;
		this.game = game;
	}

	@Override
	public TurnStatus finishTurn() {
		return this.game.finishTurn(this.player);
	}

	@Override
	public void nextShipType(ColonyId colonyId) {
		this.game.nextShipType(this.player, colonyId);
	}

	@Override
	public void colonizeSystem(SystemId systemId, FleetId fleetId, boolean skip) {
		this.game.colonizeSystem(this.player, fleetId, skip);
	}

	@Override
	public void annexSystem(ColonyId colonyId, FleetId fleetId, boolean skip) {
		this.game.annexSystem(this.player, fleetId, skip);
	}

	@Override
	public void deployFleet(FleetId fleetId, SystemId destinationId, ShipsView ships) {
		this.game.deployFleet(this.player, fleetId, destinationId, ships);
	}

	@Override
	public void selectTech(TechId techId) {
		this.game.selectTech(this.player, techId);
	}

	@Override
	public Optional<Integer> calcEta(FleetId fleetId, SystemId destinationId, ShipsView ships) {
		return this.game.calcEta(this.player, fleetId, destinationId, ships);
	}

	@Override
	public Optional<Integer> calcTranportColonistsEta(SystemId originId, SystemId destinationId) {
		return this.game.calcTranportColonistsEta(this.player, originId, destinationId);
	}

	@Override
	public void transferColonists(ColonyId originId, ColonyId destinationId, int colonists) {
		this.game.transferColonists(this.player, originId, destinationId, colonists);
	}

	@Override
	public void relocateShips(ColonyId originId, ColonyId destinationId) {
		this.game.relocateShips(this.player, originId, destinationId);
	}

	@Override
	public void adjustRation(ColonyId colonyId, ProductionArea area, int percentage) {
		// not supported... just ignore...
	}

	@Override
	public GameView view() {
		return this.game.getGameState(this.player);
	}

	@Override
	public Player player() {
		return this.player;
	}

}
