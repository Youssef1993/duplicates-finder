package com.duplicates.finder.config;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class StageManagerImpl implements StageManager {

    private Stage stage;
    private static final Logger LOG = getLogger(StageManagerImpl.class);
    private final SpringFXMLLoader springFXMLLoader;
    private final ResourceLoader resourceLoader;

    @Autowired
    public StageManagerImpl(SpringFXMLLoader springFXMLLoader, ResourceLoader resourceLoader) {
        this.springFXMLLoader = springFXMLLoader;
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void initStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void loadScreen(Screen screen) {
        Parent viewRootNodeHierarchy = loadViewNodeHierarchy(screen.getFxmlFile());
        show(viewRootNodeHierarchy, screen.getTitle());
    }

    private void show(Parent rootnode, String title){

        try {
            Scene scene = prepareScene(rootnode);
            scene.getStylesheets().add(resourceLoader.getResource("classpath:css/style.css").getURI().toString());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (Exception exception) {
            logAndExit ("Unable to show scene for title" + title,  exception);
        }
    }

    private Scene prepareScene(Parent rootNode){
        Scene scene = stage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        return scene;
    }

    private Parent loadViewNodeHierarchy(String fxmlFilePath) {
        Parent rootNode = null;
        try {
            rootNode = springFXMLLoader.load(fxmlFilePath);
            Objects.requireNonNull(rootNode, "A Root FXML node must not be null");
        } catch (Exception exception) {
            logAndExit("Unable to load FXML view " + fxmlFilePath, exception);
        }
        return rootNode;
    }


    private void logAndExit(String errorMsg, Exception exception) {
        LOG.error(errorMsg, exception.getMessage());
        exception.printStackTrace();
        Platform.exit();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}
