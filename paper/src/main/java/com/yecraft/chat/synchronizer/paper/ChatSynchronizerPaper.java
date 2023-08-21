package com.yecraft.chat.synchronizer.paper;

import com.yecraft.chat.synchronizer.api.ChatSynchronizerAPI;
import com.yecraft.chat.synchronizer.common.plugin.ChatSynchronizerPlugin;
import com.yecraft.chat.synchronizer.common.service.APIServiceProvider;
import com.yecraft.chat.synchronizer.common.service.ServiceProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class ChatSynchronizerPaper extends ChatSynchronizerPlugin {

    private final ServiceProvider<ChatSynchronizerAPI> provider;

    public ChatSynchronizerPaper(@NotNull JavaPlugin plugin) {
        super(plugin.getSLF4JLogger(), plugin.getDataFolder().toPath(), componentMessage -> {
            UUID playerUUID = componentMessage.playerUUID();
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) player.sendMessage(componentMessage.component());
        });

        this.provider = new APIServiceProvider(getAPI()) {
            @Override
            public void register() {
                Bukkit.getServicesManager().register(ChatSynchronizerAPI.class, getAPI(), plugin, ServicePriority.Highest);
            }

            @Override
            public void unregister() {
                Bukkit.getServicesManager().unregister(ChatSynchronizerAPI.class, getAPI());
            }
        };
    }


    @Override
    public ServiceProvider<ChatSynchronizerAPI> getProvider() {
        return provider;
    }
}
