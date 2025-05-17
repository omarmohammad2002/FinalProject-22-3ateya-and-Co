package com.example.anghamna.MusicService.observers;

import java.util.UUID;

public interface Subject {
    public void notifyObservers(UUID songId);
}
