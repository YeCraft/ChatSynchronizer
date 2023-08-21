package com.yecraft.chat.synchronizer.common.messaging.message;

import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public record ComponentMessageImpl(UUID playerUUID, Component component) implements ComponentMessage {
}
