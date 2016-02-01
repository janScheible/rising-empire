package com.scheible.risingempire.web.game.message.server;

import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.game.common.event.Event;
import com.scheible.risingempire.game.common.event.RandomMessageEvent;
import com.scheible.risingempire.game.common.view.View;
import java.util.Map;

/**
 *
 * @author sj
 */
public class TurnFinishedMessage {
	
	private final Player player;
	private final Map<String, String> colorMapping; // <nationName, #??????>
	
	private final int turn;
	private final View view;

	public TurnFinishedMessage(Player player, Map<String, String> colorMapping, int turn, View view) {
		this.player = player;
		this.colorMapping = colorMapping;
		
		this.turn = turn;
		this.view = view;
		
		// NOTE We have to exchange all event instance with ones that contain the EventType info.
		int eventIndex = -1;
		for(Event event : view.getEvents()) {
			eventIndex++;
			if(event instanceof RandomMessageEvent) {
				RandomMessageEvent randomMessageEvent = (RandomMessageEvent) event;
				view.getEvents().set(eventIndex, new com.scheible.risingempire.web.game.message.server.event.RandomMessageEvent(randomMessageEvent.getMessage()));
			} else {
				throw new IllegalStateException("Can't translate event of type '" + event.getClass().getSimpleName() + "' to a web tpye!");
			}
		}
	}
}
