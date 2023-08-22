package com.yecraft.chat.synchronizer.paper.commands;

import com.yecraft.chat.synchronizer.common.command.RegisteredCommand;
import dev.jorel.commandapi.CommandAPICommand;

public class RegisteredCommandAPI extends CommandAPICommand implements RegisteredCommand {
    public RegisteredCommandAPI(String commandName) {
        super(commandName);
    }
}
