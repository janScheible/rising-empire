package com.scheible.risingempire.web.join;

import com.scheible.risingempire.web.join.message.server.PlayerEntry;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.scheible.risingempire.web.security.ConnectedPlayer;
import java.security.Principal;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 *
 * @author sj
 */
@Controller
public class JoinController implements ApplicationListener<AbstractSubProtocolEvent> {

	@Autowired
	SimpMessagingTemplate messagingTemplate;

	@Autowired
	PlayerManager playerManager;

	@RequestMapping("/join.html")
	public String join(Map<String, Object> model) {
		model.put("defaultPassword", ConnectedPlayer.DEFAULT_PASSWORD);
		return "join";
	}

	@RequestMapping("/players")
	public @ResponseBody List<PlayerEntry> players() {
		return playerManager.getPlayerEntries();
	}
	
	@RequestMapping("/addAi")
	public @ResponseBody void addAi(@RequestBody com.scheible.risingempire.web.join.message.client.Player player) throws Exception {
		playerManager.addAi(player);
	}

	@Override
	public void onApplicationEvent(AbstractSubProtocolEvent event) {
		MessageHeaders headers = event.getMessage().getHeaders();
		Principal user = SimpMessageHeaderAccessor.getUser(headers);
			
		if (event instanceof SessionConnectEvent) {
			String id = SimpMessageHeaderAccessor.getSessionId(headers);
			
			playerManager.addPlayer(id, user.getName());
			
			messagingTemplate.convertAndSend("/topic/player/join", 
					PlayerEntry.create(user.getName(), PlayerEntry.State.ACTIVE));
		} else if (event instanceof SessionDisconnectEvent) {
			SessionDisconnectEvent disconnectEvent = (SessionDisconnectEvent) event;

			playerManager.removePlayer(disconnectEvent.getSessionId());
			
			messagingTemplate.convertAndSend("/topic/player/leave", 
					PlayerEntry.create(user.getName(), PlayerEntry.State.DETACHED));
		}
	}
}
