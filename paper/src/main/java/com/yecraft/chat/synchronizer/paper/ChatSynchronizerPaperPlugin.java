package com.yecraft.chat.synchronizer.paper;

import com.yecraft.chat.synchronizer.api.ChatSynchronizerAPI;
import com.yecraft.chat.synchronizer.common.command.CommandRegistry;
import com.yecraft.chat.synchronizer.common.messaging.ComponentConsumer;
import com.yecraft.chat.synchronizer.common.messaging.ComponentProducer;
import com.yecraft.chat.synchronizer.common.plugin.ChatSynchronizer;
import com.yecraft.chat.synchronizer.common.plugin.ChatSynchronizerPlugin;
import com.yecraft.chat.synchronizer.common.service.ServiceProvider;
import com.yecraft.chat.synchronizer.paper.commands.ChatSynchronizerCommands;
import com.yecraft.configuration.Configuration;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

public class ChatSynchronizerPaperPlugin extends JavaPlugin implements ChatSynchronizer {

    private final ChatSynchronizerPlugin chatSynchronizer;

    public ChatSynchronizerPaperPlugin() {
        this.chatSynchronizer = new ChatSynchronizerPaper(this);
    }

    @Override
    public void onLoad(){
        CommandAPI.onLoad(
                new CommandAPIBukkitConfig(this)
                        .silentLogs(false)
                        .verboseOutput(true)
                        .missingExecutorImplementationMessage("Вам ця команда нажаль не призначена ╯︿╰")
        );
        chatSynchronizer.onLoad();
        chatSynchronizer.getRegistry().addCommands(
                new ChatSynchronizerCommands(this)
        );
    }

    @Override
    public void onEnable(){
        CommandAPI.onEnable();
        chatSynchronizer.onEnable();
    }

    @Override
    public @NotNull ComponentLogger getComponentLogger() {
        return super.getComponentLogger();
    }

    @Override
    public void onDisable(){
        CommandAPI.onDisable();
        chatSynchronizer.onDisable();
    }

    @Override
    public Logger getChatSynchronizerLogger() {
        return chatSynchronizer.getChatSynchronizerLogger();
    }

    @Override
    public Path getDirectory() {
        return chatSynchronizer.getDirectory();
    }

    @Override
    public Configuration getConfiguration() {
        return chatSynchronizer.getConfiguration();
    }

    @Override
    public ServiceProvider<ChatSynchronizerAPI> getProvider() {
        return chatSynchronizer.getProvider();
    }

    @Override
    public ComponentProducer getProducer() {
        return chatSynchronizer.getProducer();
    }

    @Override
    public ComponentConsumer getConsumer() {
        return chatSynchronizer.getConsumer();
    }

    @Override
    public ChatSynchronizerAPI getAPI() {
        return chatSynchronizer.getAPI();
    }

    @Override
    public CommandRegistry getRegistry() {
        return chatSynchronizer.getRegistry();
    }
}
