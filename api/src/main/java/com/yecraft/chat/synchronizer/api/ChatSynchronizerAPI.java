package com.yecraft.chat.synchronizer.api;

import com.yecraft.chat.synchronizer.api.messenger.MessageSender;

public interface ChatSynchronizerAPI {

    MessageSender getSender();
}
