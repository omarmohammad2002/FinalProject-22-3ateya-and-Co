package com.example.anghamna.UserService.Observers;

import java.util.UUID;

public interface Observer {
    void onUserDeleted(UUID userId);
}
