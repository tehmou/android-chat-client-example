package com.tehmou.book.androidchatclient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ChatStore {
    private Map<String, ChatMessage> cache =
            new HashMap<>();
    private PublishSubject<Collection<ChatMessage>> subject =
            PublishSubject.create();

    public void put(ChatMessage value) {
        cache.put(value.getId(), value);
        subject.onNext(cache.values());
    }

    public Observable<Collection<ChatMessage>> getStream() {
        return subject.hide();
    }
}
