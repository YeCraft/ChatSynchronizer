package com.yecraft.chat.synchronizer.common.messaging;

import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;

import java.util.function.Consumer;

public interface ComponentConsumer {

    void consume(Consumer<ComponentMessage> consumerMessage);

    void close();

    void init();
}
