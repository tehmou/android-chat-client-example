package com.tehmou.book.androidchatclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.BooleanSubscription;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Socket socket;
    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create and connect WebSocket
        socket = createSocket();
        socket.on("connect", args ->
                Log.d(TAG, "Socket connected"));
        socket.connect();

        Gson gson = new Gson();

        chatViewModel = new ChatViewModel(createListener(socket));

        ListView listView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        chatViewModel.getMessageList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    arrayAdapter.clear();
                    arrayAdapter.addAll(list);
                });

        EditText editText = (EditText) findViewById(R.id.edit_text);
        findViewById(R.id.send_button)
                .setOnClickListener(event -> {
                    ChatMessage chatMessage = new ChatMessage(editText.getText().toString());
                    socket.emit("chat message", gson.toJson(chatMessage));
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Disconnect WebSocket
        socket.disconnect();
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
