package com.yecraft.chat.synchronizer.common.messaging.kafka;

import com.yecraft.chat.synchronizer.common.messaging.ComponentProducer;
import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import com.yecraft.chat.synchronizer.common.messaging.message.ComponentMessageImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class KafkaComponentProducerTest {

    private ComponentProducer producer;
    private MockProducer<String, String> mockProducer;
    private final String topic = "server.message";

    @BeforeEach
    void setUp() {
        this.mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        producer = new KafkaComponentProducer(
                topic, mockProducer
        );
    }

    @Test
    void produce() {
        UUID uuid = UUID.randomUUID();
        ComponentMessageImpl componentMessage = new ComponentMessageImpl(uuid, Component.text("test"));
        producer.produce(componentMessage);

        List<ProducerRecord<String, String>> history = mockProducer.history();

        history.forEach(record -> assertThat(record.value()).isEqualTo(JSONComponentSerializer.json().serialize(componentMessage.component())));
    }
}