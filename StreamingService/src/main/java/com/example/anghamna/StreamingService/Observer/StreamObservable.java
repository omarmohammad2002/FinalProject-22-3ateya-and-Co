package com.example.anghamna.StreamingService.Observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class StreamObservable {

    private final List<StreamObserver> observers = new ArrayList<>();

    public void registerObserver(StreamObserver observer) {
        observers.add(observer);
    }
    public void unregisterObserver(StreamObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(UUID songId) {
        for (StreamObserver observer : observers) {
            observer.onStream(songId);
        }
    }
}
