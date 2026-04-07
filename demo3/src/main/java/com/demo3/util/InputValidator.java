package com.demo3.util;

import javafx.scene.control.TextField;

public final class InputValidator {
    private InputValidator() {
    }

    public static Integer parseRequiredInt(TextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public static Integer parseOptionalInt(TextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        return parseRequiredInt(field);
    }
}
