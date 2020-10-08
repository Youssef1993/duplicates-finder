package com.duplicates.finder.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void getExtensionByStringHandling() {
        assertFalse(FileUtils.getExtensionByStringHandling("fileWithoutExtension").isPresent());
        assertEquals(FileUtils.getExtensionByStringHandling("file.ext"), Optional.of("ext"));
        assertEquals(FileUtils.getExtensionByStringHandling("file.something.ext"), Optional.of("ext"));
    }
}