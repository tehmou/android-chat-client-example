package com.tehmou.book.androidchatclient;

import java.util.Date;
import java.util.UUID;

public class ChatMessage {
    private String id;
    private String message;
    private long timestamp;

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

    @Override
    public String toString() {
        return message;
    }
}
