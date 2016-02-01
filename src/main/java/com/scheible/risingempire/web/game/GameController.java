package com.scheible.risingempire.web.game;

import com.scheible.risingempire.game.common.Player;
import com.scheible.risingempire.game.common.view.View;
import com.scheible.risingempire.web.appearance.BrowserColor;
import com.scheible.risingempire.web.appearance.AvailablePlayer;
import com.scheible.risingempire.web.join.PlayerHelper;
import com.scheible.risingempire.web.game.message.client.CommandsMessage;
import com.scheible.risingempire.web.game.message.server.TurnFinishedMessage;
import com.scheible.risingempire.web.game.message.server.TurnInfoMessage;
import com.scheible.risingempire.web.join.PlayerManager;
import com.scheible.risingempire.web.security.ConnectedPlayer;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author sj
 */
@Controller
public class GameController {
	
	@Autowired
	PlayerManager playerManager;
	
	@Autowired
	GameHolder gameHolder;	

	@Autowired
	SimpMessagingTemplate messagingTemplate;
	
	@RequestMapping("/game.html")
	public String game(Map<String, Object> model) {
		return "game";
	}

	@SubscribeMapping("/topic/turn/update")
	public TurnFinishedMessage subscribeTurnUpdate(@AuthenticationPrincipal ConnectedPlayer connectedPlayer) {
		Player player = PlayerHelper.resolvePlayer(connectedPlayer.getUsername());
		return new TurnFinishedMessage(player, generateColorMapping(), gameHolder.getTurn(), 
				gameHolder.getViews().get(player));
	}
	
	@MessageMapping("/commands")
	@SendTo("/topic/turn/info")
	public TurnInfoMessage commands(CommandsMessage message, @AuthenticationPrincipal ConnectedPlayer connectedPlayer) {
		boolean turnFinished = gameHolder.process(PlayerHelper.resolvePlayer(connectedPlayer.getUsername()), 
				message.getCommands());

		if (turnFinished) {
			int turn = gameHolder.getTurn();
			Map<Player, View> views = gameHolder.getViews();
		
			for(Player player : gameHolder.getPlayers()) {
				TurnFinishedMessage turnFinishedMessage = new TurnFinishedMessage(player, generateColorMapping(), 
						turn, views.get(player));
				messagingTemplate.convertAndSendToUser(player.getName() + "@" + player.getNation(), 
						"/topic/turn/update", turnFinishedMessage);
			}
			
			return null;
		} else {
			return new TurnInfoMessage(gameHolder.getTurnFinishInfo());
		}
	}
	
	private Map<String, String> generateColorMapping() {
		Map<String, String> result = new HashMap<>();
		
		for(AvailablePlayer availablePlayer : AvailablePlayer.values()) {
			result.put(availablePlayer.getNation(), new BrowserColor(availablePlayer.getColor()).getHex());
		}
		
		return result;
	}
}
