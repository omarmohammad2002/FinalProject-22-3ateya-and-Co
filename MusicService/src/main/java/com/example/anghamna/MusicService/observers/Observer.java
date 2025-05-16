package com.example.anghamna.MusicService.observers;

import java.util.UUID;

public interface Observer {
    public void onSongDeleted(UUID songId);
}
