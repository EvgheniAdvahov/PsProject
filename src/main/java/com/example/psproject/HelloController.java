package com.example.psproject;

import com.example.psproject.properties.Param;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
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

    private Process powerShellProcess;
    private BufferedWriter commandWriter;
    private BufferedReader outputReader;



//    @FXML
//    private void initialize() {
//        // Установка действия на кнопку добавления
//        addButton.setOnAction(event -> addItemToList());
//        clearButton.setOnAction(event -> clearItemInList());
//        displayButton.setOnAction(event -> displayToRichText());
//        pingButton.setOnAction(event -> pingToRichText());
//    }

    private volatile boolean isProcessRunning = true;  // Флаг для контроля процесса
    private Thread outputThread;


    public void initialize() {
        try {
            // Запуск PowerShell в интерактивном режиме
            powerShellProcess = new ProcessBuilder("pwsh", "-NoExit", "-Command", "-")
                    .redirectErrorStream(true)
                    .start();

            commandWriter = new BufferedWriter(new OutputStreamWriter(powerShellProcess.getOutputStream()));
            outputReader = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));

            // Поток для чтения данных из PowerShell
            outputThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = outputReader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> MyTextArea.appendText(finalLine + "\n"));
                    }
                } catch (IOException e) {
                    if (powerShellProcess.isAlive()) {
                        e.printStackTrace();
                    }
                }
            });
            outputThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRunCommand() {
        // Проверяем, если процесс завершен или не существует, создаем новый
        if (powerShellProcess == null || !powerShellProcess.isAlive()) {
            initializePowerShellProcess();  // Перезапускаем процесс
        }

        String command = MyTextField.getText();
        if (command.isEmpty()) {
            MyTextArea.appendText("Please enter a command.\n");
            return;
        }

        try {
            // Отправка команды в PowerShell
            commandWriter.write(command);
            commandWriter.newLine();
            commandWriter.flush();
            MyTextField.clear();
        } catch (IOException e) {
            MyTextArea.appendText("Error sending command: " + e.getMessage() + "\n");
        }
    }

    private void initializePowerShellProcess() {
        try {
            // Закрыть старые потоки и процесс перед созданием нового
            closeStreams();

            // Запуск PowerShell в интерактивном режиме
            powerShellProcess = new ProcessBuilder("pwsh", "-NoExit", "-Command", "-")
                    .redirectErrorStream(true)
                    .start();

            commandWriter = new BufferedWriter(new OutputStreamWriter(powerShellProcess.getOutputStream()));
            outputReader = new BufferedReader(new InputStreamReader(powerShellProcess.getInputStream()));

            // Поток для чтения данных из PowerShell
            outputThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = outputReader.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> MyTextArea.appendText(finalLine + "\n"));
                    }
                } catch (IOException e) {
                    if (powerShellProcess.isAlive()) {
                        e.printStackTrace();
                    }
                }
            });
            outputThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void onStopCommand() {
        if (powerShellProcess != null && powerShellProcess.isAlive()) {
            powerShellProcess.destroy();  // Прерывает выполнение команды
            MyTextArea.appendText("Command stopped.\n");
        }

        // Останавливаем поток чтения, если он существует
        if (outputThread != null && outputThread.isAlive()) {
            try {
                outputThread.interrupt();  // Прерываем поток чтения
                outputThread.join();       // Дожидаемся завершения потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Закрытие потоков
        closeStreams();
        powerShellProcess = null;  // Убираем ссылку на процесс
        outputThread = null;       // Убираем ссылку на поток
    }


    private void closeStreams() {
        try {
            if (commandWriter != null) {
                commandWriter.close();
                commandWriter = null;  // Обнуляем ссылку на writer
            }
            if (outputReader != null) {
                outputReader.close();
                outputReader = null;  // Обнуляем ссылку на reader
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    public void shutdown() {
        try {
            if (powerShellProcess != null) {
                // Отправить команду завершения PowerShell
                commandWriter.write("exit");
                commandWriter.newLine();
                commandWriter.flush();

                // Дождаться завершения процесса
                powerShellProcess.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Закрыть потоки
            try {
                if (commandWriter != null) commandWriter.close();
                if (outputReader != null) outputReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Принудительное уничтожение процесса (если не завершился)
            if (powerShellProcess != null && powerShellProcess.isAlive()) {
                powerShellProcess.destroy();
            }
        }
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