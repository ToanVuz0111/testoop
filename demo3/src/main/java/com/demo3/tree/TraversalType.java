package com.demo3.tree;

public enum TraversalType {
    DFS("DFS"),
    BFS("BFS");

    private final String label;

    TraversalType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
