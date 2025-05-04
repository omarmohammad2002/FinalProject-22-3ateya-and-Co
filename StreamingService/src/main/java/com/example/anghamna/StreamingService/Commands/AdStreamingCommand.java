package com.example.anghamna.StreamingService.Commands;

import com.example.anghamna.StreamingService.Services.StreamService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdStreamingCommand implements AudioStreamingCommand {

    private final StreamService streamService;

    public AdStreamingCommand(StreamService streamService) {
        this.streamService = streamService;
    }

    @Override
    public ResponseEntity<InputStreamResource> execute(UUID songId, String rangeHeader) throws Exception {
        return streamService.execute(songId, rangeHeader, true);
    }
}


