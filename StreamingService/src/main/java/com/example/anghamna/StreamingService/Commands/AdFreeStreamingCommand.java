package com.example.anghamna.StreamingService.Commands;

import com.example.anghamna.StreamingService.Services.StreamService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.UUID;
import com.example.anghamna.StreamingService.Services.StreamService;
import org.springframework.stereotype.Service;

@Service
public class AdFreeStreamingCommand implements AudioStreamingCommand {

    private final StreamService streamService;

    public AdFreeStreamingCommand(StreamService streamService) {
        this.streamService = streamService;
    }

    @Override
    public ResponseEntity<InputStreamResource> execute(UUID songId, String rangeHeader) throws Exception {
        return streamService.execute(songId, rangeHeader, false);
    }
}


