module com.example.psproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.beans;


    opens com.example.psproject to javafx.fxml;
    exports com.example.psproject;
}