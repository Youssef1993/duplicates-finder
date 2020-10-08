package com.duplicates.finder.gui.dataHolders;

import com.duplicates.finder.util.FileSizeConverter;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.util.Objects;

public class DuplicatedFile {
    private final SimpleStringProperty path = new SimpleStringProperty();
    private final SimpleStringProperty size = new SimpleStringProperty();
    private File file;

    public DuplicatedFile(File file) {
        this.path.set(file.getAbsolutePath());
        this.size.set(FileSizeConverter.getHumanReadableFormat(file.length()));
        this.file = file;
    }

    public DuplicatedFile(String fileName) {
        this.path.set(fileName);
    }

    public String getPath() {
        return path.get();
    }

    public SimpleStringProperty pathProperty() {
        return path;
    }

    public File getFile() {
        return file;
    }

    public String getSize() {
        return size.get();
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuplicatedFile that = (DuplicatedFile) o;
        return Objects.equals(path.get(), that.path.get());
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return "DuplicatedFile{" +
                "path=" + path +
                '}';
    }
}
