package com.example.psproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainWindowController {

    @FXML
    public void openPowershellWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/psproject/powershellWindow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("PowerShell Window");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void openWindow2() {
        openWindow("Window2.fxml", "Окно 2");
    }

    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
