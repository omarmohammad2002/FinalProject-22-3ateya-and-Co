package com.example.anghamna.StreamingService.Commands;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
public interface AudioStreamingCommand {
    ResponseEntity<InputStreamResource> execute(UUID songId, String rangeHeader) throws Exception;
}

