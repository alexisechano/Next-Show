package com.example.next_show.callbacks;

import com.example.next_show.models.Show;

import java.util.List;

public interface ResponseCallback {
    public void onSuccess(List<com.uwetrottmann.trakt5.entities.Show> shows);

    public void onFailure(int code);
}
