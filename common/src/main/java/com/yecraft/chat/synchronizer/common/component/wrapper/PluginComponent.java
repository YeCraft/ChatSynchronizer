package com.yecraft.chat.synchronizer.common.component.wrapper;

import net.kyori.adventure.text.Component;

public interface PluginComponent {

    Component fine(String text);
    Component info(String text);
    Component warn(String text);
    Component error(String text);
}
