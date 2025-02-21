package com.example.psproject;

import com.example.psproject.controller.PowershellController;
import com.example.psproject.properties.Param;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties(Param.class)
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // Укажите размеры основного окна

        stage.setResizable(false);
        stage.setTitle("Main Window");
        stage.setScene(scene);

        // Обновляем размеры окна после загрузки сцены
        stage.sizeToScene();

        // Обработчик для того, чтобы вычислить положение окна после его отображения
        stage.setOnShown(event -> {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 3);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
