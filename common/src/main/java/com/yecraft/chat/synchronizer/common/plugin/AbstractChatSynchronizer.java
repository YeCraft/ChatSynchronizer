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
import com.yecraft.chat.synchronizer.common.util.PropertiesBuilder;
import com.yecraft.configuration.Configuration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public abstract class AbstractChatSynchronizer implements ChatSynchronizer, Plugin {

    private final Logger logger;
    private final Path directory;
    private final Configuration configuration;
    private final Consumer<ComponentMessage> messageConsumer;
    private ComponentConsumer consumer;
    private ComponentProducer producer;
    private final ChatSynchronizerAPI api;
    private final CommandRegistry registry;

    public AbstractChatSynchronizer(Logger logger, @NotNull Path directory, Consumer<ComponentMessage> messageConsumer) {
        this.logger = logger;
        this.directory = directory;
        this.messageConsumer = messageConsumer;
        this.configuration = new MainConfig("1.0.1", logger, directory.resolve("config.yml"));
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
        getConfiguration().initialize();

        // messages topic
        AtomicReference<String> topic = new AtomicReference<>();

        PropertiesBuilder producerProperties = new PropertiesBuilder();

        configuration.getString("kafka.topic").ifPresentOrElse(str -> {
            topic.set(str);
            logger.info("Kafka topic is set to " + str);
        }, () -> {
            String defaultTopic = "minecraft.server.plugin.chat.synchronizer.message";
            topic.set(defaultTopic);
            logger.info("Kafka topic is set to " + defaultTopic);
        });

        configuration.getList("kafka.bootstrap-servers").ifPresentOrElse(serversList -> {
            StringBuffer buffer = new StringBuffer();
            serversList.forEach(str -> buffer.append(str).append(", "));
            producerProperties.withProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, buffer.toString());
            logger.info(String.format("Kafka producer bootstrap servers is set to %s", buffer));
        }, () -> {
            String host = "localhost:9092";
            producerProperties.withProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
            logger.info(String.format("Kafka producer bootstrap server is set to %s (default)", host));
        });

        configuration.getString("kafka.producer.linger_ms").ifPresentOrElse(str -> {
            producerProperties.withProperty(ProducerConfig.LINGER_MS_CONFIG, str);
            logger.info(String.format("Kafka producer linger ms is set to %s ms", str));
        }, () -> {
            String value = "1";
            producerProperties.withProperty(ProducerConfig.LINGER_MS_CONFIG, value);
            logger.info(String.format("Kafka producer linger ms is set to %s ms", value));
        });

        configuration.getString("kafka.producer.key_serializer_class").ifPresentOrElse(str -> {
            producerProperties.withProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, str);
            logger.info(String.format("Kafka producer key serializer is set to %s", str));
        }, () -> {
            String clazz = "org.apache.kafka.common.serialization.StringSerializer";
            producerProperties.withProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, clazz);
            logger.info(String.format("Kafka producer key serializer is set to %s", clazz));
        });

        configuration.getString("kafka.producer.value_serializer_class").ifPresentOrElse(str -> {
            producerProperties.withProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, str);
            logger.info(String.format("Kafka producer value serializer is set to %s", str));
        }, () -> {
            String clazz = "org.apache.kafka.common.serialization.StringSerializer";
            producerProperties.withProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, clazz);
            logger.info(String.format("Kafka producer value serializer is set to %s", clazz));
        });

        setProducer(
                new KafkaComponentProducer(topic.get(), producerProperties.build())
        );

        PropertiesBuilder consumerProperties = new PropertiesBuilder();

        configuration.getList("kafka.bootstrap-servers").ifPresentOrElse(serversList -> {
            StringBuffer buffer = new StringBuffer();
            serversList.forEach(str -> buffer.append(str).append(", "));
            consumerProperties.withProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, buffer.toString());
            logger.info(String.format("Kafka consumer bootstrap servers is set to %s", buffer));
        }, () -> {
            String host = "localhost:9092";
            consumerProperties.withProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
            logger.info(String.format("Kafka consumer bootstrap server is set to %s (default)", host));
        });

        configuration.getString("kafka.consumer.enable_auto_commit").ifPresentOrElse(str -> {
            consumerProperties.withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, str);
            logger.info(String.format("Kafka consumer enable auto commit is set to %s", str));
        }, () -> {
            String enable = "true";
            consumerProperties.withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enable);
            logger.info(String.format("Kafka consumer enable auto commit is set to %s", enable));
        });

        configuration.getString("kafka.consumer.group_id").ifPresentOrElse(str -> {
            consumerProperties.withProperty(ConsumerConfig.GROUP_ID_CONFIG, str);
            logger.info(String.format("Kafka consumer group id is set to %s", str));
        }, () -> {
            String groupID = "0";
            consumerProperties.withProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
            logger.info(String.format("Kafka consumer group id is set to %s", groupID));
        });

        configuration.getString("kafka.consumer.auto_commit_interval_ms").ifPresentOrElse(str -> {
            consumerProperties.withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, str);
            logger.info(String.format("Kafka consumer enable auto commit interval is set to %s", str));
        }, () -> {
            String ms = "1000";
            consumerProperties.withProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, ms);
            logger.info(String.format("Kafka consumer enable auto commit interval is set to %s", ms));
        });

        configuration.getString("kafka.consumer.key_deserializer_class").ifPresentOrElse(str -> {
            consumerProperties.withProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, str);
            logger.info(String.format("Kafka consumer key deserializer class is set to %s", str));
        }, () -> {
            String clazz = "org.apache.kafka.common.serialization.StringDeserializer";
            consumerProperties.withProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, clazz);
            logger.info(String.format("Kafka consumer key deserializer class is set to %s", clazz));
        });

        configuration.getString("kafka.consumer.value_deserializer_class").ifPresentOrElse(str -> {
            consumerProperties.withProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, str);
            logger.info(String.format("Kafka consumer value deserializer class is set to %s", str));
        }, () -> {
            String clazz = "org.apache.kafka.common.serialization.StringDeserializer";
            consumerProperties.withProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
            logger.info(String.format("Kafka consumer value deserializer class is set to %s", clazz));
        });

        setConsumer(
                new KafkaComponentConsumer(topic.get(), consumerProperties.build())
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
