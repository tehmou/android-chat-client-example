package com.tehmou.book.androidchatclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create and connect WebSocket
        socket = createSocket();
        socket.on("connect", args ->
                Log.d(TAG, "Socket connected"));
        socket.connect();

        socket.on("chat message", args ->
                Log.d(TAG, "chat message: " + args[0].toString()));

        Gson gson = new Gson();
        ChatMessage chatMessage = new ChatMessage("hello world");
        socket.emit("chat message", gson.toJson(chatMessage));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disconnect WebSocket
        socket.disconnect();
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
