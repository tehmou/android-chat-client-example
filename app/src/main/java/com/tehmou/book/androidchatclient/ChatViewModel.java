package com.tehmou.book.androidchatclient;

import com.google.gson.Gson;

import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ChatViewModel {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final Observable<List<ChatMessage>> chatMessageObservable;
    private final BehaviorSubject<List<String>> messageList = BehaviorSubject.create();

    public ChatViewModel(Observable<List<ChatMessage>> chatMessageObservable) {
        this.chatMessageObservable = chatMessageObservable;
    }

    public void subscribe() {
        subscriptions.add(chatMessageObservable
                .flatMap(list ->
                        Observable.from(list).map(ChatMessage::toString).toList())
                .subscribe(messageList::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public Observable<List<String>> getMessageList() {
        return messageList.asObservable();
    }
}
