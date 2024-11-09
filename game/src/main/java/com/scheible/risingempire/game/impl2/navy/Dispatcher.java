package com.scheible.risingempire.game.impl2.navy;

import java.util.List;
import java.util.Optional;

import com.scheible.risingempire.game.impl2.apiinternal.Position;
import com.scheible.risingempire.game.impl2.apiinternal.Round;
import com.scheible.risingempire.game.impl2.apiinternal.Speed;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployJustLeaving;
import com.scheible.risingempire.game.impl2.navy.Navy.DeployOrbiting;
import com.scheible.risingempire.game.impl2.navy.Navy.Deployment;
import com.scheible.risingempire.game.impl2.navy.Navy.ShipDeployment;
import com.scheible.risingempire.game.impl2.navy.Navy.TransferColonists;

/**
 * @author sj
 */
class Dispatcher {

	private final Fleets fleets;

	Dispatcher(Fleets fleets) {
		this.fleets = fleets;
	}

	void dispatch(Round round, List<Deployment> deployments) {
		for (Deployment deployment : deployments) {
			switch (deployment) {
				case ShipDeployment shipDeployment -> {
					// move ships from the `from` fleet to the `to` fleet (which might not
					// yet exists, will then be created)
					Fleet from;
					Optional<Fleet> to;
					boolean backToOrigin = false;
					Position destination;
					Speed speed = this.fleets.effectiveSpeed(shipDeployment.player(), shipDeployment.ships());

					switch (shipDeployment) {
						case DeployOrbiting deployOrbiting -> {
							if (deployOrbiting.origin().equals(deployOrbiting.destination())
									|| deployOrbiting.ships().empty()) {
								continue;
							}

							from = this.fleets.findOrbiting(deployOrbiting.player(), deployOrbiting.origin())
								.orElseThrow();
							to = this.fleets.findJustLeaving(deployOrbiting.player(), deployOrbiting.origin(),
									deployOrbiting.destination(), speed);
							destination = deployOrbiting.destination();
						}
						case DeployJustLeaving deployJustLeaving -> {
							if (deployJustLeaving.previousDestination().equals(deployJustLeaving.newDestination())
									|| deployJustLeaving.ships().empty()) {
								continue;
							}

							from = this.fleets
								.findJustLeaving(deployJustLeaving.player(), deployJustLeaving.origin(),
										deployJustLeaving.previousDestination(), deployJustLeaving.speed())
								.orElseThrow();
							backToOrigin = deployJustLeaving.newDestination().equals(deployJustLeaving.origin());
							to = backToOrigin
									? this.fleets.findOrbiting(deployJustLeaving.player(),
											deployJustLeaving.newDestination())
									: this.fleets.findJustLeaving(deployJustLeaving.player(),
											deployJustLeaving.origin(), deployJustLeaving.newDestination(), speed);
							destination = deployJustLeaving.newDestination();
						}
					}

					this.fleets.remove(from);
					Fleet remainingFrom = from.detach(shipDeployment.ships());
					if (!remainingFrom.ships().empty()) {
						this.fleets.add(remainingFrom);
					}

					if (to.isPresent()) {
						this.fleets.remove(to.get());
						Fleet mergedTo = to.get().merge(shipDeployment.ships());
						this.fleets.add(mergedTo);
					}
					else {
						Fleet newTo = backToOrigin
								? Fleet.createOrbiting(shipDeployment.player(), shipDeployment.origin(),
										shipDeployment.ships())
								: Fleet.createDeployed(shipDeployment.player(), shipDeployment.origin(), destination,
										round, speed, shipDeployment.ships());
						this.fleets.add(newTo);
					}

				}
				case TransferColonists transferColonists -> {
					if (transferColonists.origin().equals(transferColonists.destination())) {
						continue;
					}

					Optional<Fleet> alreadyDeployed = this.fleets.findJustLeavingTransporter(transferColonists.player(),
							transferColonists.origin(), transferColonists.destination());
					if (alreadyDeployed.isPresent()) {
						this.fleets.remove(alreadyDeployed.get());
					}

					Ships ships = Ships.transporters(transferColonists.transporterCount())
						.merge(alreadyDeployed.map(Fleet::ships).orElse(Ships.NONE));
					if (transferColonists.transporterCount() > 0) {
						Fleet deployed = Fleet.createDeployed(transferColonists.player(), transferColonists.origin(),
								transferColonists.destination(), round,
								this.fleets.effectiveSpeed(transferColonists.player(), ships), ships);
						this.fleets.add(deployed);
					}
				}
			}
		}
	}

}
