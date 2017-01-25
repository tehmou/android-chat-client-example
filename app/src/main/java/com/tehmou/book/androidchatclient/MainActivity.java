package com.tehmou.book.androidchatclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ChatViewModel chatViewModel;
    private final CompositeDisposable viewSubscriptions = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChatModel chatModel = ((ChatApplication) getApplication()).getChatModel();
        chatModel.onCreate();
        chatModel.connect();

        chatViewModel = new ChatViewModel(chatModel.getChatMessages());
        chatViewModel.subscribe();

        ListView listView = (ListView) findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        viewSubscriptions.add(chatViewModel.getMessageList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    arrayAdapter.clear();
                    arrayAdapter.addAll(list);
                }));

        EditText editText = (EditText) findViewById(R.id.edit_text);
        findViewById(R.id.send_button)
                .setOnClickListener(event -> {
                    chatModel.sendMessage(editText.getText().toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatViewModel.unsubscribe();
        viewSubscriptions.clear();
    }
}
