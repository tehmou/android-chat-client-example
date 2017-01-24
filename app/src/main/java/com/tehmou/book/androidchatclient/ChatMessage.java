package com.tehmou.book.androidchatclient;

import java.util.Date;
import java.util.UUID;

public class ChatMessage {
    private String id;
    private String message;
    private long timestamp;
    private boolean isPending = true;

    public ChatMessage(String message) {
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.timestamp = new Date().getTime();
    }

    public ChatMessage(ChatMessage chatMessage) {
        this.id = chatMessage.id;
        this.message = chatMessage.message;
        this.timestamp = chatMessage.timestamp;
        this.isPending = chatMessage.isPending;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isPending() {
        return isPending;
    }

    @Override
    public String toString() {
        return message;
    }

    public ChatMessage setIsPending(boolean isPending) {
        ChatMessage chatMessage = new ChatMessage(this);
        chatMessage.isPending = isPending;
        return chatMessage;
    }
}
