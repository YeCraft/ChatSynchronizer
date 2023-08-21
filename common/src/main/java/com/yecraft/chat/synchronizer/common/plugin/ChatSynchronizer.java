package com.yecraft.chat.synchronizer.common.plugin;

import com.yecraft.chat.synchronizer.api.ChatSynchronizerAPI;
import com.yecraft.chat.synchronizer.common.command.CommandRegistry;
import com.yecraft.chat.synchronizer.common.messaging.ComponentConsumer;
import com.yecraft.chat.synchronizer.common.messaging.ComponentProducer;
import com.yecraft.chat.synchronizer.common.service.ServiceProvider;
import com.yecraft.configuration.Configuration;
import org.slf4j.Logger;

import java.nio.file.Path;

public interface ChatSynchronizer {

    Logger getChatSynchronizerLogger();

    Path getDirectory();

    Configuration getConfiguration();

    ServiceProvider<ChatSynchronizerAPI> getProvider();

    ComponentProducer getProducer();

    ComponentConsumer getConsumer();

    ChatSynchronizerAPI getAPI();

    CommandRegistry getRegistry();

}
