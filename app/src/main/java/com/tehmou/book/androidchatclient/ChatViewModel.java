package com.tehmou.book.androidchatclient;

import android.support.v4.util.Pair;

import com.google.gson.Gson;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class ChatViewModel {
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final Observable<List<ChatMessage>> chatMessageObservable;
    private final Observable<String> searchTextObservable;
    private final BehaviorSubject<List<String>> messageList = BehaviorSubject.create();

    public ChatViewModel(Observable<List<ChatMessage>> chatMessageObservable,
                         Observable<String> searchTextObservable) {
        this.chatMessageObservable = chatMessageObservable;
        this.searchTextObservable = searchTextObservable;
    }

    public void subscribe() {
        Observable<List<ChatMessage>> filteredChatMessages =
                Observable.combineLatest(
                        chatMessageObservable, searchTextObservable, Pair::new)
                .switchMap(pair -> Observable.from(pair.first)
                        .filter(chatMessage -> chatMessage.getMessage().contains(pair.second))
                        .toList());

        subscriptions.add(
                filteredChatMessages
                        .flatMap(list ->
                                Observable.from(list)
                                        .map(ChatMessage::toString)
                                        .toList())
                        .subscribe(messageList::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public Observable<List<String>> getMessageList() {
        return messageList.asObservable();
    }
}
