package com.tehmou.book.androidchatclient;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class ChatViewModel {
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final Observable<List<ChatMessage>> chatMessageObservable;
    private final BehaviorSubject<List<String>> messageList = BehaviorSubject.create();

    public ChatViewModel(Observable<List<ChatMessage>> chatMessageObservable) {
        this.chatMessageObservable = chatMessageObservable;
    }

    public void subscribe() {
        subscriptions.add(chatMessageObservable
                .flatMap(list ->
                        Observable.fromIterable(list).map(ChatMessage::toString).toList().toObservable())
                .subscribe(messageList::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public Observable<List<String>> getMessageList() {
        return messageList.hide();
    }
}
