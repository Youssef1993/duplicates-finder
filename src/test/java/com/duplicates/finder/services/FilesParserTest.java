package com.duplicates.finder.services;

import com.duplicates.finder.services.dto.FilesFilterer;
import com.duplicates.finder.services.dto.FileProcessingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilesParserTest {

    @Mock
    ApplicationEventPublisher publisher;
    @Mock
    FilesFilterer filesFilterer;

    private FilesParser filesParser;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.filesParser = new FilesParser(publisher);
    }

    @Test
    void shouldNotProcessFilterNonMatchingFile() {
        File file = Mockito.mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.isDirectory()).thenReturn(false);
        when(file.getAbsolutePath()).thenReturn("some_path");
        when(filesFilterer.isFileProcessable(file)).thenReturn(false);
        doNothing().when(publisher).publishEvent(any());
        Map<String, List<File>> duplicates = filesParser.fetchDuplicates(file, filesFilterer);
        assertTrue(duplicates.isEmpty());
        verify(publisher, times(1)).publishEvent(any());
    }

    @Test
    void shouldProcessFilterMatchingFile() {
        File file = Mockito.mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.isDirectory()).thenReturn(false);
        when(file.getAbsolutePath()).thenReturn("some_path");
        when(filesFilterer.isFileProcessable(file)).thenReturn(true);
        doNothing().when(publisher).publishEvent(any());
        Map<String, List<File>> duplicates = filesParser.fetchDuplicates(file, filesFilterer);
        assertTrue(duplicates.isEmpty());
        verify(publisher, times(2)).publishEvent(any());
    }
}