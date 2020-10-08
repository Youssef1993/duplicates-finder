package com.duplicates.finder;

import com.duplicates.finder.config.Screen;
import com.duplicates.finder.config.StageManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ResourceBundle;

@SpringBootApplication
public class DuplicatesFinderApplication extends Application{
    private ConfigurableApplicationContext applicationContext;
    private StageManager stageManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(true);
        stageManager.initStage(primaryStage);
        stageManager.loadScreen(Screen.HOMEPAGE);
        primaryStage.show();
    }

    @Override
    public void init() {
        applicationContext = SpringApplication.run(DuplicatesFinderApplication.class);
        stageManager = applicationContext.getBean(StageManager.class);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void stop(){
        applicationContext.close();
    }

    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("application");
    }

}
