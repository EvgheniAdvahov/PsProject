package com.example.psproject;

import com.example.psproject.properties.Param;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;




public class HelloController {


    private Param param;
    @FXML
    private ListView<String> myListView;

    @FXML
    private Button addButton;

    @FXML
    private TextArea MyTextArea;

    @FXML
    private TextField MyTextField;

    @FXML
    private Button clearButton;

    @FXML
    private Button displayButton;

    @FXML
    private Button pingButton;


    @FXML
    private void initialize() {
        // Установка действия на кнопку добавления
        addButton.setOnAction(event -> addItemToList());
        clearButton.setOnAction(event -> clearItemInList());
        displayButton.setOnAction(event -> displayToRichText());
        pingButton.setOnAction(event -> pingToRichText());
    }

    private void pingToRichText() {
        int itemCount = myListView.getItems().size();
        if (itemCount == 0) return;

        String command;
        if (itemCount > 1) {
            StringJoiner joiner = new StringJoiner(",", "@(", ")");
            myListView.getItems().forEach(item -> joiner.add("\"" + item + "\""));
            command = "Test-Connection " + joiner;
        } else {
            command = param.getPING() + myListView.getItems().get(0);
        }

        String result = executePowerShellCommand(command);
        MyTextArea.setText(result);
        System.out.println(result);
    }




    private void displayToRichText() {
        // Используем StringJoiner для автоматического добавления запятых
        StringJoiner joiner = new StringJoiner(",", "@(", ")");
        for (String item : myListView.getItems()) {
            joiner.add("\"" + item + "\""); // Добавляем элементы в кавычках
        }
        // Очистка поля ввода и установка текста в TextArea
        MyTextField.clear();
        MyTextArea.setText(joiner.toString());
    }




    private void addItemToList() {
        String text = MyTextField.getText();
        if (!text.isEmpty()) {
            myListView.getItems().add(text);
            MyTextField.clear(); // очищает поле после добавления
        }
    }

    private void clearItemInList(){
        myListView.getItems().clear();
    }


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
//            ProcessBuilder processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
            ProcessBuilder processBuilder = new ProcessBuilder("pwsh", "-Command", command);
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