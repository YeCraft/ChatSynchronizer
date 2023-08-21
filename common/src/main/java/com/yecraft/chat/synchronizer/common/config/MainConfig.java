package com.yecraft.chat.synchronizer.common.config;

import com.yecraft.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.*;

public class MainConfig extends Configuration {

    public MainConfig(String version, @NotNull Logger logger, @NotNull Path path) {
        super(version, logger, path);
    }

    @Override
    public Map<String, Object> defaults() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("kafka",
                Map.of(
                        "topic", "minecraft.server.plugin.message",
                        "bootstrap-servers", List.of("localhost:9092")
                )
        );
        return map;
    }

    @Override
    public Map<String, List<String>> defaultComments() {
        Map<String, List<String>> map = new LinkedHashMap<>();
        return map;
    }
}
