package com.demo3.util;

import javafx.scene.control.Alert;

public final class DialogHelper {
    private DialogHelper() {
    }

    public static void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
