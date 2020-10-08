package com.duplicates.finder.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class PromptUtil {

    public static void showErrorMessage(String header, String message) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error Message");
        error.setHeaderText(header);
        error.setContentText(message);
        error.show();
    }

    public static Optional<ButtonType> showConfirmationDialog(String header, String message) {
        Alert confirmationDialogue = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialogue.setTitle("Confirmation Dialog");
        confirmationDialogue.setHeaderText(header);
        confirmationDialogue.setContentText(message);
        return confirmationDialogue.showAndWait();
    }
}
