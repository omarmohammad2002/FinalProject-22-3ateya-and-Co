package com.example.anghamna.StreamingService.Commands;

import org.springframework.stereotype.Service;

@Service
public class CommandInvoker {

    private final AdFreeStreamingCommand adFree;
    private final AdStreamingCommand adSupported;

    public CommandInvoker(
            AdFreeStreamingCommand adFree,
            AdStreamingCommand adSupported
    ) {
        this.adFree = adFree;
        this.adSupported = adSupported;
    }

    public AudioStreamingCommand getCommand(String userType) {
        return switch (userType.toLowerCase()) {
            case "premium" -> adFree;
            case "free" -> adSupported;
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        };
    }
}
