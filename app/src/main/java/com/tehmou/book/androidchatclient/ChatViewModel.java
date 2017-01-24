package com.tehmou.book.androidchatclient;

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
                        Observable.from(list)
                                .map(ChatViewModel::formatMessage)
                                .toList())
                .subscribe(messageList::onNext));
    }

    private static String formatMessage(ChatMessage chatMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append(chatMessage.getMessage());
        if (chatMessage.isPending()) {
            builder.append(" (pending)");
        }
        return builder.toString();
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public Observable<List<String>> getMessageList() {
        return messageList.asObservable();
    }
}
