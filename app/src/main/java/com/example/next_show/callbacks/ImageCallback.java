package com.example.next_show.callbacks;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

public interface ImageCallback {
    public void onSuccess(JsonHttpResponseHandler.JSON json);

    public void onFailure(int i);
}
