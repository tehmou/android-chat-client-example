package com.tehmou.book.androidchatclient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ChatStore {
    private List<ChatMessage> cache =
            new ArrayList<>();
    private PublishSubject<List<ChatMessage>> subject =
            PublishSubject.create();

    public void put(ChatMessage value) {
        cache.add(value);
        subject.onNext(cache);
    }

    public Observable<List<ChatMessage>> getStream() {
        return subject.hide();
    }
}
