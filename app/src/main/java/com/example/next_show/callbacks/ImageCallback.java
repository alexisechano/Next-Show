package com.example.next_show.callbacks;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import okhttp3.Headers;

public interface ImageCallback {
    public void onSuccess(JsonHttpResponseHandler.JSON json);

    public void onFailure(int i);
}
