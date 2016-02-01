package com.scheible.risingempire.web.game.message.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scheible.risingempire.game.common.command.Command;
import java.util.List;

/**
 *
 * @author sj
 */
public class CommandsMessage {
 
    private final List<Command> commands;

	@JsonCreator
	public CommandsMessage(@JsonProperty("commands") List<Command> commands) {
		this.commands = commands;
	}

    public List<Command> getCommands() {
        return commands;
    }
}
