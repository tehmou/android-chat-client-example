package com.tehmou.book.androidchatclient;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatModel {
    private static final String TAG = ChatModel.class.getSimpleName();

    private final Gson gson = new Gson();
    private Retrofit retrofit;
    private ChatMessageApi chatMessageApi;

    private Socket socket;
    private final ChatStore chatStore = new ChatStore();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    public void onCreate() {
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Config.SERVER_URL)
                .build();
        chatMessageApi = retrofit.create(ChatMessageApi.class);

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
            subscriber.setDisposable(Disposables.fromAction(
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

    public void loadOldMessages() {
        chatMessageApi.messages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messages -> {
                    for (String messageJson : messages) {
                        ChatMessage chatMessage = gson.fromJson(messageJson, ChatMessage.class);
                        chatStore.put(new ChatMessage(chatMessage).setIsPending(false));
                    }
                });
    }
}
