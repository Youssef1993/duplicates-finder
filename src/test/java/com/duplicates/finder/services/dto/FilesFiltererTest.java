package com.duplicates.finder.services.dto;

import com.duplicates.finder.util.FileSizeConverter;
import com.duplicates.finder.util.FileUtils;
import com.duplicates.finder.util.SizeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FilesFiltererTest {

    @Test
    void testFileShouldNotBePassedDueToSizeBiggerThanMaxSize() {
        try(MockedStatic<FileSizeConverter> converterMocked = mockStatic(FileSizeConverter.class)) {
            converterMocked.when(() -> FileSizeConverter.convertToBytes(1, SizeUnit.MB)).thenReturn(1024L);
            FilesFilterer filterer = new FilesFilterer();
            filterer.setMaxSize(1, SizeUnit.MB);
            File file = Mockito.mock(File.class);
            when(file.length()).thenReturn(4000L);
            when(file.exists()).thenReturn(true);
            assertFalse(filterer.isFileProcessable(file));
            verify(file, Mockito.times(1)).length();
        }
    }

    @Test
    void testFileShouldNotBePassedDueToSizeLessThanMinSize() {
        try(MockedStatic<FileSizeConverter> converterMocked = mockStatic(FileSizeConverter.class)) {
            converterMocked.when(() -> FileSizeConverter.convertToBytes(1, SizeUnit.MB)).thenReturn(1024L);
            FilesFilterer filterer = new FilesFilterer();
            filterer.setMinSize(1, SizeUnit.MB);
            File file = Mockito.mock(File.class);
            when(file.length()).thenReturn(100L);
            when(file.exists()).thenReturn(true);
            assertFalse(filterer.isFileProcessable(file));
            verify(file, Mockito.times(1)).length();
        }
    }

    @Test
    void testFileShouldBePassedDueToSizeBiggerThanMinSize() {
        try(MockedStatic<FileSizeConverter> converterMocked = mockStatic(FileSizeConverter.class)) {
            converterMocked.when(() -> FileSizeConverter.convertToBytes(1, SizeUnit.MB)).thenReturn(1024L);
            FilesFilterer filterer = new FilesFilterer();
            filterer.setMinSize(1, SizeUnit.MB);
            File file = Mockito.mock(File.class);
            when(file.length()).thenReturn(5000L);
            when(file.exists()).thenReturn(true);
            assertTrue(filterer.isFileProcessable(file));
            verify(file, Mockito.times(1)).length();
        }
    }

    @Test
    void testFileShouldBePassedDueToSizeLessThanMaxSize() {
        try(MockedStatic<FileSizeConverter> converterMocked = mockStatic(FileSizeConverter.class)) {
            converterMocked.when(() -> FileSizeConverter.convertToBytes(1, SizeUnit.MB)).thenReturn(1024L);
            FilesFilterer filterer = new FilesFilterer();
            filterer.setMaxSize(1, SizeUnit.MB);
            File file = Mockito.mock(File.class);
            when(file.length()).thenReturn(500L);
            when(file.exists()).thenReturn(true);
            assertTrue(filterer.isFileProcessable(file));
            verify(file, Mockito.times(1)).length();
        }
    }

    @Test
    void testFileShouldBePassedDueToSizeWithinSizeBounds() {
        try(MockedStatic<FileSizeConverter> converterMocked = mockStatic(FileSizeConverter.class)) {
            converterMocked.when(() -> FileSizeConverter.convertToBytes(1, SizeUnit.MB)).thenReturn(1024L);
            converterMocked.when(() -> FileSizeConverter.convertToBytes(2, SizeUnit.MB)).thenReturn(2048L);
            FilesFilterer filterer = new FilesFilterer();
            filterer.setMaxSize(2, SizeUnit.MB);
            filterer.setMinSize(1, SizeUnit.MB);
            File file = Mockito.mock(File.class);
            when(file.length()).thenReturn(2014L);
            when(file.exists()).thenReturn(true);
            assertTrue(filterer.isFileProcessable(file));
            verify(file, Mockito.times(1)).length();
        }
    }

    @Test
    void testFileShouldNotPassFueToExtensionInDisabledExtensions() {
        try(MockedStatic<FileUtils> fileUtilsMocked = mockStatic(FileUtils.class)) {
            File file = Mockito.mock(File.class);
            when(file.exists()).thenReturn(true);
            when(file.length()).thenReturn(1L);
            when(file.getName()).thenReturn("file.disabled");
            fileUtilsMocked.when(() -> FileUtils.getExtensionByStringHandling("file.disabled")).thenReturn(Optional.of("disabled"));
            FilesFilterer filterer = new FilesFilterer();
            filterer.addDisabledExtension("disabled");
            assertFalse(filterer.isFileProcessable(file));
        }
    }

    @Test
    void testFileShouldPassFueToExtensionNotInDisabledExtensions() {
        try(MockedStatic<FileUtils> fileUtilsMocked = mockStatic(FileUtils.class)) {
            File file = Mockito.mock(File.class);
            when(file.exists()).thenReturn(true);
            when(file.length()).thenReturn(1L);
            when(file.getName()).thenReturn("file.enabled");
            fileUtilsMocked.when(() -> FileUtils.getExtensionByStringHandling("file.enabled")).thenReturn(Optional.of("enabled"));
            FilesFilterer filterer = new FilesFilterer();
            filterer.addDisabledExtension("disabled");
            assertTrue(filterer.isFileProcessable(file));
        }
    }

    @Test
    void testFileShouldNotPassFueToExtensionNotInEnabledExtensions() {
        try(MockedStatic<FileUtils> fileUtilsMocked = mockStatic(FileUtils.class)) {
            File file = Mockito.mock(File.class);
            when(file.exists()).thenReturn(true);
            when(file.length()).thenReturn(1L);
            when(file.getName()).thenReturn("file.somethingElse");
            fileUtilsMocked.when(() -> FileUtils.getExtensionByStringHandling("file.somethingElse")).thenReturn(Optional.of("somethingElse"));
            FilesFilterer filterer = new FilesFilterer();
            filterer.addTargetExtension("enabled");
            assertFalse(filterer.isFileProcessable(file));
        }
    }

    @Test
    void testFileShouldPassFueToExtensionInEnabledExtensions() {
        try(MockedStatic<FileUtils> fileUtilsMocked = mockStatic(FileUtils.class)) {
            File file = Mockito.mock(File.class);
            when(file.exists()).thenReturn(true);
            when(file.length()).thenReturn(1L);
            when(file.getName()).thenReturn("file.enabled");
            fileUtilsMocked.when(() -> FileUtils.getExtensionByStringHandling("file.enabled")).thenReturn(Optional.of("enabled"));
            FilesFilterer filterer = new FilesFilterer();
            filterer.addTargetExtension("enabled");
            assertTrue(filterer.isFileProcessable(file));
        }
    }


}