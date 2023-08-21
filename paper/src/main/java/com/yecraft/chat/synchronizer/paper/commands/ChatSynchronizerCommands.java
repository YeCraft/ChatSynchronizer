package com.yecraft.chat.synchronizer.paper.commands;

import com.yecraft.chat.synchronizer.common.command.BaseCommand;
import com.yecraft.chat.synchronizer.common.command.RegisteredCommand;
import com.yecraft.chat.synchronizer.common.messaging.message.ComponentMessageImpl;
import com.yecraft.chat.synchronizer.common.plugin.ChatSynchronizer;
import com.yecraft.chat.synchronizer.common.util.ColorPicker;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ChatSynchronizerCommands implements BaseCommand {

    private final ChatSynchronizer plugin;

    public ChatSynchronizerCommands(ChatSynchronizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public RegisteredCommand command() {
        return (RegisteredCommand) new RegisteredCommandAPI("chat-synchronizer")
                .withAliases("chat-sync")
                .withSubcommand(
                        new CommandAPICommand("send")
                                .withSubcommand(
                                        new CommandAPICommand("message")
                                                .withArguments(new PlayerArgument("player"), new GreedyStringArgument("message"))
                                                .executes((sender, args) -> {
                                                    Player player = (Player) args.get("player");
                                                    String message = (String) args.get("message");
                                                    if (message == null){
                                                        sender.sendMessage("Повідомлення не може бути null");
                                                        return;
                                                    }
                                                    if(player == null){
                                                        sender.sendMessage("Гравець не може бути нуль");
                                                        return;
                                                    }
                                                    TextComponent text = Component.text(message).decorate(TextDecoration.BOLD).color(ColorPicker.BLUE);
                                                    plugin.getProducer().produce(new ComponentMessageImpl(player.getUniqueId(), text));
                                                })
                                )
                );

    }
}
