package com.example.anghamna.MusicService.observers;

import java.util.UUID;

public interface Subject {
    public void registerObserver(Observer o);
    public void removeObserver(Observer o);
    public void notifyObservers(UUID songId);
}
