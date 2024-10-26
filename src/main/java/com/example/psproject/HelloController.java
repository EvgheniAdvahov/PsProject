package com.example.psproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HelloController {
    @FXML
    private TextArea MyTextArea;

    @FXML
    private TextField MyTextField;

    @FXML
    protected void onButtonGetProcClick() {
        // Вызов PowerShell команды "Get-Process"
        String result = executePowerShellCommand("Get-Process");
        MyTextArea.setText(result);
    }

    @FXML
    protected void onButtonRunClick() {
//        String result = executePowerShellCommand("Get-Process");
        String result = executePowerShellCommand(MyTextField.getText());
        System.out.println(MyTextField.getText());
        MyTextArea.setText(result);
    }

    // Метод для выполнения команды PowerShell
    private String executePowerShellCommand(String command) {
        StringBuilder output = new StringBuilder();

        try {
            // Используем PowerShell для выполнения команды
            // linux pwsh
            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
            processBuilder.redirectErrorStream(true);

            // Запуск процесса
            Process process = processBuilder.start();

            // Чтение вывода команды
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Ожидание завершения процесса
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            output.append("Ошибка выполнения команды: ").append(e.getMessage());
        }

        return output.toString();
    }


}