package com.example.next_show.callbacks;

import com.example.next_show.models.Show;

import java.util.List;

public interface ResponseCallback {
    public void onSuccess(List<Show> shows);

    public void onFailure(int code);
}
