package com.duplicates.finder.util;

public class FileSizeConverter {

    public static long convertToBytes(long size, SizeUnit unit) {
        switch (unit) {
            case KB:
                return convertKiloBytesToBytes(size);
            case MB:
                return convertMegaBytesToBytes(size);
            case GB:
                return convertGigaBytesToBytes(size);
        }
        return size;
    }

    private static long convertGigaBytesToBytes(Long sizeInGigaBytes) {
        return convertMegaBytesToBytes(sizeInGigaBytes) * 1000;
    }

    private static long convertMegaBytesToBytes(Long sizeInMegabytes) {
        return convertKiloBytesToBytes(sizeInMegabytes) * 1000;
    }

    private static long convertKiloBytesToBytes(Long sizeInKiloBytes) {
        return sizeInKiloBytes * 1000;
    }

    public static String getHumanReadableFormat(long fileSizeInBytes) {
        long sizeInKiloBytes = fileSizeInBytes / 1000;
        if ((sizeInKiloBytes / 1000) < 1) {
            return sizeInKiloBytes + " " + SizeUnit.KB.name();
        }
        long sizeInMegaBytes = sizeInKiloBytes / 1000;
        if (sizeInMegaBytes / 1000 < 1) {
            return sizeInMegaBytes + (sizeInKiloBytes % 1000 > 0 ? "." + sizeInKiloBytes % 1000 : "") + " " + SizeUnit.MB.name();
        }
        return (sizeInMegaBytes / 1000) + (sizeInMegaBytes % 1000 > 0 ? "." + sizeInMegaBytes % 1000 : "") + " " + SizeUnit.GB.name();
    }
}
