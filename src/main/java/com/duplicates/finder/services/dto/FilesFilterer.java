package com.duplicates.finder.services.dto;

import com.duplicates.finder.util.FileSizeConverter;
import com.duplicates.finder.util.FileUtils;
import com.duplicates.finder.util.SizeUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesFilterer {

    private long minSizeInBytes;
    private long maxSizeInBytes;
    private final List<String> targetedExtensions = new ArrayList<>();
    private final List<String> disabledExtensions = new ArrayList<>();

    public void addTargetExtension(String extension) {
        targetedExtensions.add(extension.toLowerCase());
    }

    public void addDisabledExtension(String extension) {
        disabledExtensions.add(extension.toLowerCase());
    }

    public boolean isFileProcessable(File file) {
        if (!file.exists()) {
            return false;
        }
        long fileSize = file.length();
        return isAboveMinSize(fileSize) && isUnderMaxSize(fileSize) &&
                isExtensionTargeted(file.getName()) && isNotInDisabledExtensions(file.getName());
    }

    public void setMinSize(long minSize, SizeUnit unit) {
        this.minSizeInBytes = FileSizeConverter.convertToBytes(minSize, unit);
    }

    public void setMaxSize(long maxSize, SizeUnit unit) {
        this.maxSizeInBytes = FileSizeConverter.convertToBytes(maxSize, unit);
    }

    private boolean isAboveMinSize(long fileSize) {
        return minSizeInBytes < fileSize;
    }

    private boolean isUnderMaxSize(long fileSize) {
        return maxSizeInBytes <= 0 || fileSize < maxSizeInBytes;
    }

    public boolean isExtensionTargeted(String fileName) {
        return targetedExtensions.isEmpty() || FileUtils.getExtensionByStringHandling(fileName.toLowerCase())
                .map(targetedExtensions::contains)
                .orElse(false);
    }

    public boolean isNotInDisabledExtensions(String fileName) {
        return disabledExtensions.isEmpty() || FileUtils.getExtensionByStringHandling(fileName.toLowerCase())
                .map(extension -> !disabledExtensions.contains(extension))
                .orElse(true);
    }
}
