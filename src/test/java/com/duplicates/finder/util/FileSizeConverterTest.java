package com.duplicates.finder.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileSizeConverterTest {

    @Test
    public void testConvertMegaBytesToBytes() {
        assertEquals(FileSizeConverter.convertToBytes(1, SizeUnit.MB), 1_000_000);
        assertEquals(FileSizeConverter.convertToBytes(5, SizeUnit.MB), 5_000_000);
        assertEquals(FileSizeConverter.convertToBytes(10, SizeUnit.MB), 10_000_000);
    }

    @Test
    public void testConvertKiloBytesToBytes() {
        assertEquals(FileSizeConverter.convertToBytes(1, SizeUnit.KB), 1000);
        assertEquals(FileSizeConverter.convertToBytes(6, SizeUnit.KB), 6000);
        assertEquals(FileSizeConverter.convertToBytes(120, SizeUnit.KB), 120_000);
    }

    @Test
    public void testConvertGigaBytesToBytes() {
        assertEquals(FileSizeConverter.convertToBytes(1, SizeUnit.GB), 1_000_000_000);
    }

    @Test
    public void testGetReadableFormat() {
        assertEquals(FileSizeConverter.getHumanReadableFormat(1000), "1 KB");
        assertEquals(FileSizeConverter.getHumanReadableFormat(1_000_000_000), "1 GB");
        assertEquals(FileSizeConverter.getHumanReadableFormat(120_000), "120 KB");
        assertEquals(FileSizeConverter.getHumanReadableFormat(10_000_000), "10 MB");
    }

}