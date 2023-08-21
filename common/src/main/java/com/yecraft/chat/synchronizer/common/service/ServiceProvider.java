package com.yecraft.chat.synchronizer.common.service;

public interface ServiceProvider<S> {

    S getService();

    void register();

    void unregister();
}
