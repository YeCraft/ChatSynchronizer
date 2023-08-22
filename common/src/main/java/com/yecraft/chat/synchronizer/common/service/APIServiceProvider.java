package com.yecraft.chat.synchronizer.common.service;

import com.yecraft.chat.synchronizer.api.ChatSynchronizerAPI;
import org.jetbrains.annotations.Nullable;

public abstract class APIServiceProvider implements ServiceProvider<ChatSynchronizerAPI> {

    private final ChatSynchronizerAPI api;

    public APIServiceProvider(ChatSynchronizerAPI api) {
        this.api = api;
    }

    @Override
    public final @Nullable ChatSynchronizerAPI getService() {
        return api;
    }
}
