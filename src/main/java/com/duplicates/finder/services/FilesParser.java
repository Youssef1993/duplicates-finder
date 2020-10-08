package com.duplicates.finder.services;

import com.duplicates.finder.services.dto.FilesFilterer;
import com.duplicates.finder.services.dto.FileProcessingEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilesParser {

    private final ApplicationEventPublisher publisher;
    private final Map<String, List<File>> processedFiles = new HashMap<>();

    public FilesParser(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public Map<String, List<File>> fetchDuplicates(File file, FilesFilterer filesFilterer) {
        processFile(file, filesFilterer);
        publisher.publishEvent(new FileProcessingEvent(this, ""));
        return getRedundantFiles();
    }

    private void processFile(File file, FilesFilterer filesFilterer) {
        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            publisher.publishEvent(new FileProcessingEvent(this, file.getAbsolutePath()));
            for (String s : file.list()) {
                File child = new File(file.getAbsolutePath() + File.separator + s);
                processFile(child, filesFilterer);
            }
        } else {
            addProcessedFile(file, filesFilterer);
        }
    }

    private Map<String, List<File>> getRedundantFiles() {
        Map<String, List<File>> redundantFiles = new HashMap<>();
        processedFiles.keySet()
                .stream()
                .filter(key -> processedFiles.get(key).size() > 1)
                .forEach((key) -> redundantFiles.put(key, processedFiles.get(key)));
        processedFiles.clear();
        return redundantFiles;
    }

    private void addProcessedFile(File file, FilesFilterer filesFilterer) {
        if (!filesFilterer.isFileProcessable(file)) {
            return;
        }
        publisher.publishEvent(new FileProcessingEvent(this, file.getAbsolutePath()));
        if (!processedFiles.containsKey(file.getName())) {
            processedFiles.put(file.getName(), new ArrayList<>());
        }
        processedFiles.get(file.getName()).add(file);
    }
}
