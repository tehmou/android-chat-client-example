package com.tehmou.book.androidchatclient;

import android.app.Application;

public class ChatApplication extends Application {
    private ChatModel chatModel;

    @Override
    public void onCreate() {
        super.onCreate();
        chatModel = new ChatModel();
        chatModel.onCreate();
        chatModel.connect();
    }

    public ChatModel getChatModel() {
        return chatModel;
    }
}
