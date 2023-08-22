package com.yecraft.chat.synchronizer.common.component;

import com.yecraft.chat.synchronizer.common.component.wrapper.IComponentBuilder;
import com.yecraft.chat.synchronizer.common.component.wrapper.PluginComponent;
import com.yecraft.chat.synchronizer.common.util.ColorPicker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class PluginComponentBuilder implements IComponentBuilder, PluginComponent {

    private String prefix;
    private TextColor prefixColor;
    private String divider;
    private TextColor dividerColor;

    public PluginComponentBuilder() {
        this.prefix = "Prefix";
        this.prefixColor = ColorPicker.PLUM;
        this.divider = "→";
        this.dividerColor = ColorPicker.YELLOW;
    }

    public PluginComponentBuilder withPrefix(String prefix, TextColor color){
        this.prefix = prefix;
        this.prefixColor = color;
        return this;
    }

    public PluginComponentBuilder withDivider(String divider, TextColor color){
        this.divider = divider;
        this.dividerColor = color;
        return this;
    }

    /**
     *
     * @return returns a plugin message blank with no text
     */
    @Override
    public Component build() {
        return Component.text()
                .append(Component.text(prefix).color(prefixColor))
                .appendSpace()
                .append(Component.text(divider).color(dividerColor))
                .appendSpace()
                .build();
    }


    @Override
    public Component fine(String text) {
        return Component.text()
                .append(Component.text(prefix).color(prefixColor))
                .appendSpace()
                .append(Component.text(divider).color(dividerColor))
                .appendSpace()
                .append(Component.text(text).color(ColorPicker.CHARTREUSE))
                .build();
    }

    @Override
    public Component info(String text) {
        return Component.text()
                .append(Component.text(prefix).color(prefixColor))
                .appendSpace()
                .append(Component.text(divider).color(dividerColor))
                .appendSpace()
                .append(Component.text(text).color(ColorPicker.ROYAL_BLUE))
                .build();
    }

    @Override
    public Component warn(String text) {
        return Component.text()
                .append(Component.text(prefix).color(prefixColor))
                .appendSpace()
                .append(Component.text(divider).color(dividerColor))
                .appendSpace()
                .append(Component.text(text).color(ColorPicker.ORANGE))
                .build();
    }

    @Override
    public Component error(String text) {
        return Component.text()
                .append(Component.text(prefix).color(prefixColor))
                .appendSpace()
                .append(Component.text(divider).color(dividerColor))
                .appendSpace()
                .append(Component.text(text).color(ColorPicker.RED))
                .build();
    }
}
