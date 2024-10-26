module com.example.psproject {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.psproject to javafx.fxml;
    exports com.example.psproject;
}