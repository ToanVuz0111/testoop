package com.demo3.tree;

public enum TreeType {
    GENERIC("Generic Tree"),
    BINARY_SEARCH("Binary Search Tree"),
    RED_BLACK("Red-Black Tree");

    private final String displayName;

    TreeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
