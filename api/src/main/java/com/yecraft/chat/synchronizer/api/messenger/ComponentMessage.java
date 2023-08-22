package com.yecraft.chat.synchronizer.api.messenger;

import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface ComponentMessage {

    UUID playerUUID();
    Component component();
}
