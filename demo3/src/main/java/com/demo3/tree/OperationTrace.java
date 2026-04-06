package com.demo3.tree;

import java.util.List;

public record OperationTrace(List<OperationStep> steps) {
    public static OperationTrace single(
            String title,
            String message,
            List<String> pseudocode,
            int activeLine,
            TreeSnapshot snapshot
    ) {
        return new OperationTrace(List.of(new OperationStep(title, message, pseudocode, activeLine, snapshot)));
    }
}
