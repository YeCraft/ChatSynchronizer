package com.yecraft.chat.synchronizer.common.plugin;

import com.yecraft.chat.synchronizer.api.ChatSynchronizerAPI;
import com.yecraft.chat.synchronizer.api.messenger.ComponentMessage;
import com.yecraft.chat.synchronizer.api.messenger.MessageSender;
import com.yecraft.chat.synchronizer.common.command.CommandRegistry;
import com.yecraft.chat.synchronizer.common.config.MainConfig;
import com.yecraft.chat.synchronizer.common.messaging.ComponentConsumer;
import com.yecraft.chat.synchronizer.common.messaging.ComponentProducer;
import com.yecraft.chat.synchronizer.common.messaging.kafka.KafkaComponentConsumer;
import com.yecraft.chat.synchronizer.common.messaging.kafka.KafkaComponentProducer;
import com.yecraft.chat.synchronizer.common.service.ServiceProvider;
import com.yecraft.chat.synchronizer.common.util.PropertiesBuilder;
import com.yecraft.configuration.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

public abstract class ChatSynchronizerPlugin implements ChatSynchronizer, Plugin {

    private final Logger logger;
    private final Path directory;
    private final Configuration configuration;
    private final Consumer<ComponentMessage> messageConsumer;
    private ComponentConsumer consumer;
    private ComponentProducer producer;
    private final ChatSynchronizerAPI api;
    private final CommandRegistry registry;

    public ChatSynchronizerPlugin(Logger logger, @NotNull Path directory, Consumer<ComponentMessage> messageConsumer) {
        this.logger = logger;
        this.directory = directory;
        this.messageConsumer = messageConsumer;
        this.configuration = new MainConfig("1.0", logger, directory.resolve("config.yml"));
        MessageSender sender = (message) -> producer.produce(message);
        this.api = () -> sender;
        this.registry = new CommandRegistry(logger);
    }

    @Override
    public Logger getChatSynchronizerLogger() {
        return logger;
    }

    @Override
    public Path getDirectory() {
        return directory;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public ComponentProducer getProducer() {
        return producer;
    }

    @Override
    public ComponentConsumer getConsumer() {
        return consumer;
    }

    @Override
    public ChatSynchronizerAPI getAPI() {
        return api;
    }

    @Override
    public CommandRegistry getRegistry() {
        return registry;
    }

    @Override
    public void onLoad() {
        Thread.currentThread().setContextClassLoader(null);
        configuration.initialize();

        Optional<String> kafkaTopic = configuration.getString("kafka.topic");
        String defaultTopic = "minecraft.server.plugin.message";
        if (kafkaTopic.isEmpty()){
            logger.error("Topic is empty, set default " + defaultTopic);
        }

        Optional<String> kafkaServers = configuration.getString("kafka.bootstrap-servers");
        String defaultServer = "localhost:29092";
        if (kafkaServers.isEmpty()){
            logger.error("Kafka bootstrap servers is empty, server set to default " + defaultServer);
        }

        Properties producerProperties = new PropertiesBuilder()
                .withProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, defaultServer)
                .withProperty(ProducerConfig.LINGER_MS_CONFIG, "1")
                .withProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
                .withProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer ")
                .build();

        setProducer(
                new KafkaComponentProducer(defaultTopic, producerProperties)
        );

        Properties consumerProperties = new PropertiesBuilder()
                .withProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, defaultServer)
                .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
                .withProperty(ConsumerConfig.GROUP_ID_CONFIG, "0")
                .withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
                .withProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .withProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
                .build();

        setConsumer(
                new KafkaComponentConsumer(defaultTopic, consumerProperties)
        );
    }

    @Override
    public void onEnable() {
        consumer.init();
        consumer.consume(messageConsumer);
        getProvider().register();
        getRegistry().registerCommands();
    }

    @Override
    public void onDisable() {
        getProvider().unregister();
        consumer.close();
        producer.close();
    }

    private void setConsumer(ComponentConsumer consumer) {
        this.consumer = consumer;
    }

    private void setProducer(ComponentProducer producer) {
        this.producer = producer;
    }
}
