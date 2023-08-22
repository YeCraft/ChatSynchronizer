package com.yecraft.chat.synchronizer.common.command;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Recorder for commands
 * @see <a href="https://commandapi.jorel.dev/javadocs/html/classdev_1_1jorel_1_1commandapi_1_1_command_a_p_i_command.html">CommandAPICommand</a>
 */
public class CommandRegistry {

    private final List<BaseCommand> commands;
    private final Logger logger;

    public CommandRegistry(Logger logger) {
        this.logger = logger;
        this.commands = new ArrayList<>();
    }

    public void addCommand(BaseCommand command){
        commands.add(command);
    }

    @SafeVarargs
    public final void addCommands(BaseCommand... commands){
        this.commands.addAll(Arrays.asList(commands));
    }

    /**
     * Registers commands using their register method
     * @see <a href="https://commandapi.jorel.dev/javadocs/html/classdev_1_1jorel_1_1commandapi_1_1_command_a_p_i_command.html">CommandAPICommand</a>
     */
    public void registerCommands(){
        commands.forEach(baseCommand -> baseCommand.command().register());
    }
}
