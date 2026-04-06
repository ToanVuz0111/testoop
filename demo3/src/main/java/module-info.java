module com.demo3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.demo3 to javafx.fxml;
    exports com.demo3;
}