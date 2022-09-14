module com.example.whatsup {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.whatsup to javafx.fxml;
    exports com.example.whatsup;
}