module com.demo3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.demo3 to javafx.fxml;
    opens com.demo3.controller to javafx.fxml;
    exports com.demo3;
    exports com.demo3.view;
}
