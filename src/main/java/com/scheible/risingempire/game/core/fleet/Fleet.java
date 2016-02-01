package com.scheible.risingempire.game.core.fleet;

import com.scheible.risingempire.game.common.command.Command;
import com.scheible.risingempire.game.common.command.FleetDispatchCommand;
import com.scheible.risingempire.game.core.Leader;
import com.scheible.risingempire.game.core.star.Star;
import com.scheible.risingempire.game.core.star.Stars;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author sj
 */
public class Fleet {
	
	private final double SPEED = 30.0f;
	
	private final int id;
	private final Leader leader;
	
	private Star star;
	
	private Optional<Star> destinationStar;
	
	private Optional<Vector2D> location;

	public Fleet(int id, Leader leader, Star star) {
		this.id = id;
		this.leader = leader;
		
		this.star = star;
		destinationStar = Optional.empty();
		location = Optional.empty();
	}
	
	public void turn(Stars stars, List<Command> commands) {
		for(Command command : commands) {
			if(command instanceof FleetDispatchCommand) {
				FleetDispatchCommand fleetDispatchCommand = (FleetDispatchCommand) command;
				if(fleetDispatchCommand.getFleetId() == id) {
					this.destinationStar = Optional.of(stars.getByName(fleetDispatchCommand.getDestinationStar()));
					location = Optional.of(star.getLocation());

					move();
					return;
				}
			}
		}
		
		if(destinationStar.isPresent()) {
			move();
		}
	}
	
	private void move() {
		Vector2D moveVector = destinationStar.get().getLocation().subtract(location.get()).normalize();
		location = Optional.of(location.get().add(moveVector.scalarMultiply(SPEED)));

		boolean arrived = star.getLocation().distance(destinationStar.get().getLocation())
				<= star.getLocation().distance(location.get());
		if (arrived) {
			star = destinationStar.get();
			destinationStar = Optional.empty();
			location = Optional.empty();
		}		
	}
	
	public boolean isOrbiting() {
		return !destinationStar.isPresent();
	}

	public Vector2D getLocation() {
		return !destinationStar.isPresent() ? star.getLocation() : location.get();
	}
	
	public int getId() {
		return id;
	}

	public Star getStar() {
		return star;
	}	

	public Leader getLeader() {
		return leader;
	}
}
