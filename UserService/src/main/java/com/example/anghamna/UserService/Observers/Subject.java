package com.example.anghamna.UserService.Observers;

import java.util.UUID;

public interface Subject {
    public void notifyObservers(UUID userId);
}
