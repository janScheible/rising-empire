package com.scheible.risingempire.game.core.event;

import com.scheible.risingempire.game.core.Leader;

/**
 *
 * @author sj
 */
public abstract class Event {
	
	private final Leader leader;

	public Event(Leader leader) {
		this.leader = leader;
	}

	public Leader getLeader() {
		return leader;
	}
}
