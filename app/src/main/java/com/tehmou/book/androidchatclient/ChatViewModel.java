package com.tehmou.book.androidchatclient;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class ChatViewModel {
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private final Observable<String> chatMessageObservable;
    private final BehaviorSubject<List<String>> messageList = BehaviorSubject.create();

    public ChatViewModel(Observable<String> chatMessageObservable) {
        this.chatMessageObservable = chatMessageObservable;
    }

    public void subscribe() {
        Gson gson = new Gson();
        subscriptions.add(chatMessageObservable
                .map(json -> gson.fromJson(json, ChatMessage.class))
                .scan(new ArrayList<>(), ChatViewModel::arrayAccumulatorFunction)
                .flatMap(list ->
                        Observable.fromIterable(list).map(ChatMessage::toString).toList().toObservable())
                .subscribe(messageList::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    private static List<ChatMessage> arrayAccumulatorFunction(
            List<ChatMessage> previousMessagesList,
            ChatMessage newMessage) {
        ArrayList<ChatMessage> newMessagesList = new ArrayList<>(previousMessagesList);
        newMessagesList.add(newMessage);
        return newMessagesList;
    }

    public Observable<List<String>> getMessageList() {
        return messageList.hide();
    }
}
