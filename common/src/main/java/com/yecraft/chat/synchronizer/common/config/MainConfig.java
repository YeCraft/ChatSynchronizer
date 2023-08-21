package com.yecraft.chat.synchronizer.common.config;

import com.yecraft.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainConfig extends Configuration {

    public MainConfig(String version, @NotNull Logger logger, @NotNull Path path) {
        super(version, logger, path);
    }

    @Override
    public Map<String, Object> defaults() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(
                "kafka", Map.of(
                        "topic", "minecraft.server.plugin.chat.synchronizer.message",
                        "bootstrap-servers", List.of("localhost:9092"),
                        "producer", Map.of(
                                "linger_ms", 1,
                                "key_serializer_class", "org.apache.kafka.common.serialization.StringSerializer",
                                "value_serializer_class", "org.apache.kafka.common.serialization.StringSerializer"
                        ),
                        "consumer", Map.of(
                                "enable_auto_commit", "true",
                                "consumer.group_id", "0",
                                "auto_commit_interval_ms", "1000",
                                "key_deserializer_class", "org.apache.kafka.common.serialization.StringDeserializer",
                                "value_deserializer_class", "org.apache.kafka.common.serialization.StringDeserializer"
                        )
                )
        );
        return map;
    }

    @Override
    public Map<String, List<String>> defaultComments() {
        Map<String, List<String>> map = new LinkedHashMap<>();

        List<String> kafka = new LinkedList<>();
        kafka.add("Apache Kafka is the heart of the plugin that helps it communicate between different servers and perform its function.");
        kafka.add("The setting of this item is very important!");
        map.put("kafka", kafka);

        List<String> kafkaTopic = new LinkedList<>();
        kafkaTopic.add("Messages from the plugin will be sent and received on the specified topic.");
        kafkaTopic.add("Example: *.chat.synchronizer.message");
        map.put("kafka.topic", kafkaTopic);

        List<String> kafkaServers = new LinkedList<>();
        kafkaServers.add("Host your Kafka brokers");
        kafkaServers.add("It is advisable to specify all brokers, so that after disabling one, the plugin does not break");
        map.put("kafka.bootstrap-servers", kafkaServers);

        return map;
    }
}
