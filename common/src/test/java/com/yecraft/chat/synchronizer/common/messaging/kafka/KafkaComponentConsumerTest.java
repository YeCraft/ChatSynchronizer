package com.yecraft.chat.synchronizer.common.messaging.kafka;

import com.yecraft.chat.synchronizer.common.messaging.ComponentConsumer;
import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import com.yecraft.chat.synchronizer.common.messaging.message.ComponentMessageImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class KafkaComponentConsumerTest {

    private MockConsumer<String, String> mockConsumer;
    private ComponentConsumer consumer;
    private final String topic = "server.message";
    @BeforeEach
    void setUp() {
        this.mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        this.consumer = new KafkaComponentConsumer(topic, mockConsumer);
    }

    @Test
    void consume() {
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        UUID uuid = UUID.randomUUID();
        ComponentMessage message = new ComponentMessageImpl(uuid, Component.text("test"));

        mockConsumer.unsubscribe();
        mockConsumer.assign(Collections.singleton(topicPartition));

        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(topicPartition, 0L);
        mockConsumer.updateBeginningOffsets(beginningOffsets);

        mockConsumer.addRecord(
                new ConsumerRecord<>(topic, 0, 0, message.playerUUID().toString(), JSONComponentSerializer.json().serialize(message.component()))
        );

        consumer.consume(message1 -> assertThat(message1).isEqualTo(JSONComponentSerializer.json().serialize(message.component())));
    }

}