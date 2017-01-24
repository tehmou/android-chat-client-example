package com.tehmou.book.androidchatclient;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.Collection;

import rx.Observable;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.CompositeSubscription;

public class ChatModel {
    private static final String TAG = ChatModel.class.getSimpleName();

    private final Gson gson = new Gson();
    private Socket socket;
    private final ChatStore chatStore = new ChatStore();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public void onCreate() {
        // Create WebSocket
        socket = createSocket();
        socket.on("connect", args ->
                Log.d(TAG, "Socket connected"));
        subscriptions.add(
                createListener(socket)
                        .map(json -> gson.fromJson(json, ChatMessage.class))
                        .map(chatMessage -> chatMessage.setIsPending(false))
                        .subscribe(chatStore::put)
        );
    }

    public void onDestroy() {
        subscriptions.clear();
    }

    public void connect() {
        // Connect WebSocket
        socket.connect();
    }

    public void disconnect() {
        // Disconnect WebSocket
        socket.disconnect();
    }

    public void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message);
        chatStore.put(chatMessage);
        socket.emit("chat message", gson.toJson(chatMessage));
    }

    public Observable<Collection<ChatMessage>> getChatMessages() {
        return chatStore.getStream();
    }

    public static Observable<String> createListener(Socket socket) {
        return Observable.create(subscriber -> {
            Emitter.Listener listener = args -> subscriber.onNext((String) args[0]);
            socket.on("chat message", listener);
            subscriber.add(BooleanSubscription.create(
                    () -> socket.off("chat message", listener)
            ));
        });
    }

    public static Socket createSocket() {
        Socket socket = null;
        try {
            socket = IO.socket(Config.SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error creating socket", e);
        }
        return socket;
    }
}
