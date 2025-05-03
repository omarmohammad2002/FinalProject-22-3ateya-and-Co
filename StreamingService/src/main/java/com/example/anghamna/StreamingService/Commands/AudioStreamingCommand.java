package com.example.anghamna.StreamingService.Commands;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.* ;
public interface AudioStreamingCommand {
    ResponseEntity<InputStreamResource> execute(UUID songId, String rangeHeader ) throws Exception;
}

