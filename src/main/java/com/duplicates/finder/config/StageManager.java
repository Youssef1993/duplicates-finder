package com.duplicates.finder.config;

import javafx.stage.Stage;

public interface StageManager {
    void initStage(Stage stage);
    void loadScreen(Screen screen);
    Stage getStage();
}
