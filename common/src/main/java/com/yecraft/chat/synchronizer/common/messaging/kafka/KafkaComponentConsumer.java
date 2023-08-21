package com.yecraft.chat.synchronizer.common.messaging.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import com.yecraft.chat.synchronizer.common.messaging.ComponentConsumer;
import com.yecraft.chat.synchronizer.common.messaging.message.ComponentMessageImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaComponentConsumer implements ComponentConsumer {

    private ConcurrentLinkedQueue<ConsumerRecords<String, String>> queue;
    private final String topic;
    private final Consumer<String, String> consumer;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private final ExecutorService service = Executors.newFixedThreadPool(5);
    private boolean poll = true;

    public KafkaComponentConsumer(String topic, Consumer<String, String> consumer) {
        this.topic = topic;
        this.consumer = consumer;
        this.consumer.subscribe(List.of(topic));
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public KafkaComponentConsumer(String topic, Properties properties) {
        this.topic = topic;
        this.consumer = new KafkaConsumer<>(properties);
        this.queue = new ConcurrentLinkedQueue<>();
    }


    @Override
    public synchronized void consume(java.util.function.Consumer<ComponentMessage> consumerMessage) {
        service.execute(() -> {
            while (true){
                queue.add(consumer.poll(Duration.ofMillis(100)));
            }
        });
        service.execute(() -> {
            while (true){
                ConsumerRecords<String, String> records = queue.poll();
                if (records != null){
                    if (!records.isEmpty()){
                        records.forEach(record -> {
                            String key = record.key();
                            String value = record.value();
                            Component component = JSONComponentSerializer.json().deserialize(value);
                            consumerMessage.accept(
                                    new ComponentMessageImpl(UUID.fromString(key), component)
                            );
                        });
                    }
                }
            }
        });
    }

    @Override
    public synchronized void close() {
        poll = false;
        service.shutdown();
        synchronized (consumer){
            consumer.close();
        }
    }

    @Override
    public void init() {
        consumer.subscribe(Collections.singletonList(topic));
    }
}
