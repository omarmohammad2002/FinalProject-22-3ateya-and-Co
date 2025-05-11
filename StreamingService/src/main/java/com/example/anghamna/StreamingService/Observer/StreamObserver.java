package com.example.anghamna.StreamingService.Observer;

import java.util.UUID;

public interface StreamObserver
{
    void onStream(UUID songId) ;
}
