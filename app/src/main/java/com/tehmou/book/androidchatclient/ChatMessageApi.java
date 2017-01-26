package com.tehmou.book.androidchatclient;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ChatMessageApi {
    @GET("/messages")
    Observable<List<String>> messages();
}
