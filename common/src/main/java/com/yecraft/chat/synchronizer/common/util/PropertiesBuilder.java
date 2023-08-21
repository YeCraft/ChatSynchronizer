package com.yecraft.chat.synchronizer.common.util;

import java.util.Properties;

public class PropertiesBuilder implements SimpleBuilder<Properties>{

    Properties properties;

    public PropertiesBuilder() {
        this.properties = new Properties();
    }

    public PropertiesBuilder withProperty(String property, String value){
        properties.put(property, value);
        return this;
    }

    @Override
    public Properties build() {
        return properties;
    }
}
