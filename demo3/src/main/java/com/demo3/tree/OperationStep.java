package com.demo3.tree;

import java.util.List;

public record OperationStep(
        String title,
        String message,
        List<String> pseudocode,
        int activeLine,
        TreeSnapshot snapshot
) {
}
