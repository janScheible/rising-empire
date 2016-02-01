package com.scheible.risingempire.web.join.message.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author sj
 */
public class Player extends com.scheible.risingempire.game.common.Player {
	
	@JsonCreator
	public Player(@JsonProperty("name") String name, @JsonProperty("nation") String nation) {
		super(name, nation);
	}	
}
