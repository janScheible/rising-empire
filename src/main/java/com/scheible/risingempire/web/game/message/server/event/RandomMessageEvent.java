package com.scheible.risingempire.web.game.message.server.event;

import static com.scheible.risingempire.web.game.message.server.event.EventType.*;

/**
 *
 * @author sj
 */
public class RandomMessageEvent extends com.scheible.risingempire.game.common.event.RandomMessageEvent {
	
	private final EventType type = RANDOM_MESSAGE;

	public RandomMessageEvent(String message) {
		super(message);
	}
}
