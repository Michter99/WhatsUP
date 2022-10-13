module com.example.whatsup {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.whatsup to javafx.fxml;
    exports com.example.whatsup;
}