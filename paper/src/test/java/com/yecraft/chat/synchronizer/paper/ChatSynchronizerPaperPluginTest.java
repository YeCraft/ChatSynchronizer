package com.yecraft.chat.synchronizer.paper;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.yecraft.chat.synchronizer.api.ChatSynchronizerAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
class ChatSynchronizerPaperPluginTest {

    private ServerMock server;
    private ChatSynchronizerPaperPlugin plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        System.out.println(server.getVersion());
        System.out.println(server.getMinecraftVersion());
        System.out.println(server.getBukkitVersion());
        plugin = MockBukkit.load(ChatSynchronizerPaperPlugin.class);
        plugin.onLoad();
        plugin.onEnable();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void getChatSynchronizerLogger() {
        assertThat(plugin.getChatSynchronizerLogger()).isNotNull();
    }

    @Test
    void getDirectory() {
        assertThat(plugin.getDirectory()).isNotNull();
    }

    @Test
    void getConfiguration() {
        assertThat(plugin.getConfiguration()).isNotNull();
    }

    @Test
    void getProvider() {
        assertThat(plugin.getProvider()).isNotNull();
    }

    @Test
    void getProducer() {
        assertThat(plugin.getProducer()).isNotNull();
    }

    @Test
    void getConsumer() {
        assertThat(plugin.getConsumer()).isNotNull();
    }

    @Test
    void getAPI() {
        Set<Class<?>> knownServices = server.getServicesManager().getKnownServices();
        boolean contains = knownServices.contains(ChatSynchronizerAPI.class);
        assertThat(contains).isTrue();
        plugin.onDisable();
    }
}