package com.scheible.risingempire.game.core.event;

import com.scheible.risingempire.game.core.Leader;

/**
 *
 * @author sj
 */
public class RandomMessageEvent extends Event {
	
	private final String message;

	public RandomMessageEvent(Leader leader, String message) {
		super(leader);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
