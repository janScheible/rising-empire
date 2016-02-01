package com.scheible.risingempire.game.common.event;

/**
 *
 * @author sj
 */
public class RandomMessageEvent extends Event {

	private final String message;

	public RandomMessageEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
