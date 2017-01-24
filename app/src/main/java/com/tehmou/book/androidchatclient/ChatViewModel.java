package com.tehmou.book.androidchatclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class ChatViewModel {
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final Observable<Collection<ChatMessage>> chatMessageObservable;
    private final BehaviorSubject<List<String>> messageList = BehaviorSubject.create();

    public ChatViewModel(Observable<Collection<ChatMessage>> chatMessageObservable) {
        this.chatMessageObservable = chatMessageObservable;
    }

    public void subscribe() {
        subscriptions.add(chatMessageObservable
                .map(list -> {
                    List<ChatMessage> sortedList = new ArrayList<>(list);
                    Collections.sort(sortedList, this::chatMessageComparator);
                    return sortedList;
                })
                .flatMap(list ->
                        Observable.fromIterable(list)
                                .map(ChatViewModel::formatMessage)
                                .toList()
                                .toObservable())
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

    private int chatMessageComparator(ChatMessage a, ChatMessage b) {
        if (a.getTimestamp() > b.getTimestamp()) {
            return 1;
        } else if (a.getTimestamp() < b.getTimestamp()) {
            return -1;
        }
        return 0;
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public Observable<List<String>> getMessageList() {
        return messageList.hide();
    }
}
