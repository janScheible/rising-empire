package com.scheible.risingempire.web.game.message.client.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author sj
 */
public class FleetDispatchCommand extends com.scheible.risingempire.game.common.command.FleetDispatchCommand {
    
	@JsonCreator
	public FleetDispatchCommand(@JsonProperty("fleedId") int fleedId, @JsonProperty("destinationStar") String destinationStartName) {
		super(fleedId, destinationStartName);
	}
}