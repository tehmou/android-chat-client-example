package com.tehmou.book.androidchatclient;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ChatViewModel {
    private final BehaviorSubject<List<String>> messageList = BehaviorSubject.create();

    public ChatViewModel(Observable<String> chatMessageObservable) {
        Gson gson = new Gson();
        chatMessageObservable
                .map(json -> gson.fromJson(json, ChatMessage.class))
                .scan(new ArrayList<>(), ChatViewModel::arrayAccumulatorFunction)
                .flatMap(list ->
                        Observable.from(list).map(ChatMessage::toString).toList())
                .subscribe(messageList::onNext);
    }

    private static List<ChatMessage> arrayAccumulatorFunction(
            List<ChatMessage> previousMessagesList,
            ChatMessage newMessage) {
        ArrayList<ChatMessage> newMessagesList = new ArrayList<>(previousMessagesList);
        newMessagesList.add(newMessage);
        return newMessagesList;
    }

    public Observable<List<String>> getMessageList() {
        return messageList.asObservable();
    }
}
