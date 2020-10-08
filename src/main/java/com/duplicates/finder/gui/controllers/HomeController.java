package com.duplicates.finder.gui.controllers;

import com.duplicates.finder.config.StageManager;
import com.duplicates.finder.gui.dataHolders.DuplicatedFile;
import com.duplicates.finder.gui.util.PromptUtil;
import com.duplicates.finder.services.FilesParser;
import com.duplicates.finder.services.dto.FilesFilterer;
import com.duplicates.finder.services.dto.FileProcessingEvent;
import com.duplicates.finder.util.SizeUnit;
import com.sun.javafx.PlatformUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@Service
public class HomeController implements ApplicationListener<FileProcessingEvent> {

    private final StageManager stageManager;
    private final FilesParser filesParser;
    private final ResourceLoader resourceLoader;

    private final Pattern pattern = Pattern.compile("[0-9]*");
    private final ObservableList<DuplicatedFile> duplicatedFiles = FXCollections.observableArrayList();
    private File chosenFile;
    private TableView<DuplicatedFile> tableView;


    @FXML
    private TextField chosenFolderField;
    @FXML
    private TextField minSizeField;
    @FXML
    private ComboBox<String> minSizeUnitCombo;
    @FXML
    private TextField maxSizeField;
    @FXML
    private ComboBox<String> maxSizeUnitCombo;
    @FXML
    private RadioButton enabledExtensionRadioButton;
    @FXML
    private RadioButton disabledExtensionRadioButton;
    @FXML
    private TextField enabledExtensionsField;
    @FXML
    private TextField disabledExtensionsField;
    @FXML
    private Button startButton;
    @FXML
    private FlowPane outputArea;
    @FXML
    private Label bottomLabel;

    public HomeController(StageManager stageManager, FilesParser filesParser, ResourceLoader resourceLoader) {
        this.stageManager = stageManager;
        this.filesParser = filesParser;
        this.resourceLoader = resourceLoader;
    }

    @FXML
    public void initialize() {
        initSizeUnitComboBoxes();
        initExtensionsFilter();
        startButton.setDisable(true);
    }

    private void initExtensionsFilter() {
        ToggleGroup extensionsToggleGroup = new ToggleGroup();
        enabledExtensionRadioButton.setToggleGroup(extensionsToggleGroup);
        disabledExtensionRadioButton.setToggleGroup(extensionsToggleGroup);
        disabledExtensionRadioButton.setSelected(true);
        enabledExtensionsField.setDisable(true);
    }

    private void initSizeUnitComboBoxes() {
        ObservableList<String> sizeUnits = FXCollections.observableArrayList(SizeUnit.KB.name(), SizeUnit.MB.name(), SizeUnit.GB.name());
        maxSizeUnitCombo.setItems(sizeUnits);
        minSizeUnitCombo.setItems(sizeUnits);
        maxSizeUnitCombo.setValue("MB");
        minSizeUnitCombo.setValue("MB");
    }

    private FilesFilterer readFilterValues() {
        FilesFilterer filesFilterer = new FilesFilterer();
        if (!StringUtils.isEmpty(minSizeUnitCombo.getValue()) && !StringUtils.isEmpty(minSizeField.getText()) && pattern.matcher(minSizeField.getText()).matches()) {
            long minSize = Long.parseLong(minSizeField.getText());
            filesFilterer.setMinSize(minSize, SizeUnit.valueOf(minSizeUnitCombo.getValue()));
        }
        if (!StringUtils.isEmpty(maxSizeUnitCombo.getValue()) && !StringUtils.isEmpty(maxSizeField.getText()) && pattern.matcher(maxSizeField.getText()).matches()) {
            long maxSize = Long.parseLong(maxSizeField.getText());
            filesFilterer.setMaxSize(maxSize, SizeUnit.valueOf(maxSizeUnitCombo.getValue()));
        }
        if (enabledExtensionRadioButton.isSelected()) {
            processCommaSeparatedExtensions(enabledExtensionsField.getText(), filesFilterer::addTargetExtension);
        } else {
            processCommaSeparatedExtensions(disabledExtensionsField.getText(), filesFilterer::addDisabledExtension);

        }
        return filesFilterer;
    }

    private void processCommaSeparatedExtensions(String mainString, Consumer<String> consumer) {
        String[] extensions = mainString.split(",");
        for (String extension : extensions) {
            consumer.accept(extension.trim());
        }
    }

    @FXML
    public void changeExtensionsField() {
        enabledExtensionsField.setDisable(!enabledExtensionsField.isDisabled());
        disabledExtensionsField.setDisable(!disabledExtensionsField.isDisabled());
        if (enabledExtensionRadioButton.isSelected()) {
            disabledExtensionsField.setText("");
        } else {
            enabledExtensionsField.setText("");
        }
    }

    @FXML
    public void chooseFolder() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        File selectedFile = fileChooser.showDialog(stageManager.getStage());
        if (selectedFile == null) {
            return;
        }
        chosenFile = selectedFile;
        chosenFolderField.setText(selectedFile.getAbsolutePath());
        startButton.setDisable(false);
    }

    @FXML
    public void startFetching() {
        showLoadingGif();
        new Thread(() -> {
            Map<String, List<File>> redundantFiles = filesParser.fetchDuplicates(chosenFile, readFilterValues());
            initializeDuplicatesTableView(redundantFiles);
        }).start();

    }

    @FXML
    public void clearFilters() {
        minSizeField.setText("");
        maxSizeField.setText("");
        enabledExtensionsField.setText("");
        disabledExtensionsField.setText("");
    }

    @SneakyThrows
    private void showLoadingGif() {
        outputArea.getChildren().clear();
        ImageView loadingGif = new ImageView();
        loadingGif.setImage(new Image(resourceLoader.getResource("classpath:images/loading.gif").getURI().toString()));
        outputArea.getChildren().add(loadingGif);
    }

    private void initializeDuplicatesTableView(Map<String, List<File>> duplicatedFilesMap) {
        duplicatedFiles.clear();
        for (String fileName : duplicatedFilesMap.keySet()) {
            duplicatedFiles.add(new DuplicatedFile(fileName));
            duplicatedFilesMap.get(fileName).stream()
                    .map(DuplicatedFile::new)
                    .forEach(duplicatedFiles::add);
            duplicatedFiles.add(new DuplicatedFile(""));
        }
        initDuplicatesTable();
        Platform.runLater(this::showDuplicatesTable);
    }

    private void initDuplicatesTable() {
        if (tableView != null) {
            return;
        }
        tableView = new TableView<>(duplicatedFiles);
        tableView.setPrefWidth(1100);
        tableView.getSelectionModel()
                .getSelectedItems()
                .addListener((ListChangeListener<DuplicatedFile>) change ->
                        bottomLabel.setText(tableView.getSelectionModel().getSelectedItem().getPath().replaceAll("/", " > ")));
        TableColumn<DuplicatedFile, String> pathColumn = new TableColumn<>("File");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathColumn.setPrefWidth(600);

        TableColumn<DuplicatedFile, String> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.setPrefWidth(80);

        TableColumn<DuplicatedFile, File> actionColumn = new TableColumn<>("Action");
        actionColumn.setSortable(false);
        actionColumn.setPrefWidth(400);
        actionColumn.setCellValueFactory(features -> new SimpleObjectProperty<>(features.getValue().getFile()));
        actionColumn.setCellFactory(personBooleanTableColumn -> new FilesActionCell());
        tableView.getColumns().setAll(pathColumn, sizeColumn, actionColumn);
    }

    private void showDuplicatesTable() {
        outputArea.getChildren().clear();
        outputArea.getChildren().add(tableView);
    }

    @Override
    public void onApplicationEvent(FileProcessingEvent fileProcessingEvent) {
        Platform.runLater(() -> {
            bottomLabel.setText(fileProcessingEvent.getMessage().replaceAll("/", " > "));
        });
    }

    private class FilesActionCell extends TableCell<DuplicatedFile, File> {
        final Button deleteButton = new Button("");
        final Button executeButton = new Button("");
        final FlowPane stackPane = new FlowPane();

        FilesActionCell() {
            stackPane.setPadding(new Insets(0));
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setHgap(5);
            stackPane.setPrefHeight(27);
            stackPane.getChildren().add(deleteButton);
            stackPane.getChildren().add(executeButton);
            deleteButton.setOnAction(e -> removeFile(getItem()));
            executeButton.setOnAction(e -> executeFile(getItem()));

            try {
                Image deleteIcon = new Image(resourceLoader.getResource("classpath:images/delete-icon.png").getURI().toString(), 35, 25, false, true);
                deleteButton.setGraphic(new ImageView(deleteIcon));
                deleteButton.setTooltip(new Tooltip("Delete"));
            } catch (IOException e) {
                deleteButton.setText("Delete");
            }
            try {
                Image executeIcon = new Image(resourceLoader.getResource("classpath:images/btn-blue-play.png").getURI().toString(), 25, 25, false, true);
                executeButton.setGraphic(new ImageView(executeIcon));
                executeButton.setTooltip(new Tooltip("Execute File"));
            } catch (IOException e) {
                executeButton.setText("Run");
            }

        }

        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(stackPane);
            } else {
                setGraphic(null);
            }
        }
    }

    private void removeFile(File file) {
        Optional<ButtonType> result = PromptUtil.showConfirmationDialog("Deletion confirmation", "Are you sure you want to delete this file ?");
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }
        if (file.delete()) {
            duplicatedFiles.remove(new DuplicatedFile(file.getAbsolutePath()));
        } else {
            PromptUtil.showErrorMessage("Error while deleting file", "Could not delete file, please check permissions!");
        }
    }

    private void executeFile(File file) {
        try {
            if (PlatformUtil.isLinux() || PlatformUtil.isUnix())
                Runtime.getRuntime().exec("xdg-open " + file.toPath().toUri());
            else if (PlatformUtil.isWindows()) {
                Runtime.getRuntime().exec("explorer " + file.toPath().toUri());
            }
        } catch (Exception e) {
            PromptUtil.showErrorMessage("Error while opening file", "Could not open file, please check permissions!");
        }

    }
}
