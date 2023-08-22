package com.yecraft.chat.synchronizer.common.messaging.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.commons.lang3.Validate;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class KafkaComponentProducer implements com.yecraft.chat.synchronizer.common.messaging.ComponentProducer {

    private final String topic;
    private final Producer<String, String> producer;
    private final Gson gson;

    public KafkaComponentProducer(String topic, Producer<String, String> producer) {
        this.topic = topic;
        this.producer = producer;
        this.gson = new GsonBuilder()
                .setPrettyPrinting().create();
    }

    public KafkaComponentProducer(String topic, Properties properties) {
        this.producer = new KafkaProducer<>(properties);
        this.topic = topic;
        this.gson = new GsonBuilder()
                .setPrettyPrinting().create();
    }

    @Override
    public void produce(@NotNull ComponentMessage message) {
        Validate.notNull(message, "Message can not be null");
        String uuid = message.playerUUID().toString();
        String json = JSONComponentSerializer.json().serialize(message.component());
        producer.send(
                new ProducerRecord<>(topic, uuid, json)
        );
    }

    @Override
    public void close() {
        producer.close();
    }
}
