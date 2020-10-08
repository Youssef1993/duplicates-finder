package com.duplicates.finder.services.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class FileProcessingEvent extends ApplicationEvent {
    @Getter
    private final String message;
    public FileProcessingEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
