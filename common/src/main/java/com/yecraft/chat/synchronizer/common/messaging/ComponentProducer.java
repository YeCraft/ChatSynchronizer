package com.yecraft.chat.synchronizer.common.messaging;

import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface ComponentProducer {
    void produce(ComponentMessage message);

    void close();
}
