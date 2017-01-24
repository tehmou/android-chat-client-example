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
}
