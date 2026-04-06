package com.demo3.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class GenericTreeModel implements TreeModel {
    private GenericNode root;

    @Override
    public TreeType type() {
        return TreeType.GENERIC;
    }

    @Override
    public TreeModel copy() {
        GenericTreeModel copy = new GenericTreeModel();
        copy.root = cloneNode(root);
        return copy;
    }

    @Override
    public TreeSnapshot snapshot() {
        return snapshot(Set.of());
    }

    @Override
    public OperationTrace create() {
        root = null;
        return OperationTrace.single("Create", "Created a new empty Generic Tree.", List.of("root = null", "render empty tree"), 1, snapshot());
    }

    @Override
    public OperationTrace insert(Integer parentValue, int newValue) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "if tree is empty: create root",
                "else find parent node",
                "validate unique value",
                "append new child to parent",
                "render updated tree"
        );
        if (contains(newValue)) {
            return OperationTrace.single("Insert", "Value " + newValue + " already exists.", pseudocode, 2, snapshot(Set.of(newValue)));
        }
        if (root == null) {
            steps.add(new OperationStep("Insert", "Tree is empty, create the first node.", pseudocode, 0, snapshot()));
            root = new GenericNode(newValue);
            steps.add(new OperationStep("Insert", "Tree was empty, so " + newValue + " became the root.", pseudocode, 4, snapshot(Set.of(newValue))));
            return new OperationTrace(steps);
        }
        if (parentValue == null) {
            return OperationTrace.single("Insert", "Generic Tree insertion needs a parent value.", pseudocode, 1, snapshot());
        }
        GenericNode parent = findNode(root, parentValue);
        if (parent == null) {
            return OperationTrace.single("Insert", "Parent " + parentValue + " was not found.", pseudocode, 1, snapshot());
        }
        steps.add(new OperationStep("Insert", "Found parent " + parentValue + ".", pseudocode, 1, snapshot(Set.of(parentValue))));
        parent.children.add(new GenericNode(newValue));
        steps.add(new OperationStep("Insert", "Inserted " + newValue + " under parent " + parentValue + ".", pseudocode, 3, snapshot(Set.of(parentValue, newValue))));
        steps.add(new OperationStep("Insert", "Rendering updated Generic Tree.", pseudocode, 4, snapshot(Set.of(newValue))));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace delete(int value) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "if root is target: clear tree",
                "search target and its parent",
                "remove target subtree from parent",
                "render updated tree"
        );
        if (root == null) {
            return OperationTrace.single("Delete", "Tree is already empty.", pseudocode, 0, snapshot());
        }
        if (root.value == value) {
            steps.add(new OperationStep("Delete", "Target matches the root.", pseudocode, 0, snapshot(Set.of(value))));
            root = null;
            steps.add(new OperationStep("Delete", "Deleted the root and cleared the tree.", pseudocode, 3, snapshot()));
            return new OperationTrace(steps);
        }
        GenericNode parent = findParent(root, value);
        if (parent == null) {
            return OperationTrace.single("Delete", "Value " + value + " was not found.", pseudocode, 1, snapshot());
        }
        steps.add(new OperationStep("Delete", "Found parent " + parent.value + " for target " + value + ".", pseudocode, 1, snapshot(Set.of(parent.value, value))));
        parent.children.removeIf(child -> child.value == value);
        steps.add(new OperationStep("Delete", "Removed subtree rooted at " + value + ".", pseudocode, 2, snapshot(Set.of(parent.value))));
        steps.add(new OperationStep("Delete", "Rendering updated Generic Tree.", pseudocode, 3, snapshot()));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace update(int currentValue, int newValue) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "find target node",
                "validate new value uniqueness",
                "replace node value",
                "render updated tree"
        );
        GenericNode node = findNode(root, currentValue);
        if (node == null) {
            return OperationTrace.single("Update", "Value " + currentValue + " was not found.", pseudocode, 0, snapshot());
        }
        steps.add(new OperationStep("Update", "Located node " + currentValue + ".", pseudocode, 0, snapshot(Set.of(currentValue))));
        if (currentValue != newValue && contains(newValue)) {
            return OperationTrace.single("Update", "Value " + newValue + " already exists.", pseudocode, 1, snapshot(Set.of(currentValue)));
        }
        node.value = newValue;
        steps.add(new OperationStep("Update", "Updated " + currentValue + " to " + newValue + ".", pseudocode, 2, snapshot(Set.of(newValue))));
        steps.add(new OperationStep("Update", "Rendering updated Generic Tree.", pseudocode, 3, snapshot(Set.of(newValue))));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace search(int value) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "start from root",
                "visit nodes level by level",
                "if current value matches: highlight it",
                "otherwise continue until exhausted"
        );
        if (root == null) {
            return OperationTrace.single("Search", "Tree is empty.", pseudocode, 0, snapshot());
        }
        Queue<GenericNode> queue = new ArrayDeque<>();
        queue.add(root);
        steps.add(new OperationStep("Search", "Start searching from the root.", pseudocode, 0, snapshot(Set.of(root.value))));
        while (!queue.isEmpty()) {
            GenericNode current = queue.remove();
            steps.add(new OperationStep("Search", "Visiting " + current.value + ".", pseudocode, 1, snapshot(Set.of(current.value))));
            if (current.value == value) {
                steps.add(new OperationStep("Search", "Found " + value + ".", pseudocode, 2, snapshot(Set.of(value))));
                return new OperationTrace(steps);
            }
            queue.addAll(current.children);
        }
        steps.add(new OperationStep("Search", "Reached the end without finding " + value + ".", pseudocode, 3, snapshot()));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace traverse(TraversalType traversalType) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = traversalType == TraversalType.DFS
                ? List.of("visit current node", "recursively visit each child")
                : List.of("enqueue root", "dequeue node", "visit node", "enqueue all children");

        if (root == null) {
            return OperationTrace.single("Traverse", "Tree is empty.", pseudocode, 0, snapshot());
        }

        if (traversalType == TraversalType.DFS) {
            dfs(root, steps, pseudocode);
        } else {
            Queue<GenericNode> queue = new ArrayDeque<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                GenericNode current = queue.remove();
                steps.add(new OperationStep("Traverse BFS", "Visited " + current.value + ".", pseudocode, 2, snapshot(Set.of(current.value))));
                queue.addAll(current.children);
            }
        }
        return new OperationTrace(steps);
    }

    private void dfs(GenericNode node, List<OperationStep> steps, List<String> pseudocode) {
        steps.add(new OperationStep("Traverse DFS", "Visited " + node.value + ".", pseudocode, 0, snapshot(Set.of(node.value))));
        for (GenericNode child : node.children) {
            dfs(child, steps, pseudocode);
        }
    }

    private boolean contains(int value) {
        return findNode(root, value) != null;
    }

    private GenericNode findNode(GenericNode node, int value) {
        if (node == null) {
            return null;
        }
        if (node.value == value) {
            return node;
        }
        for (GenericNode child : node.children) {
            GenericNode found = findNode(child, value);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private GenericNode findParent(GenericNode current, int childValue) {
        if (current == null) {
            return null;
        }
        for (GenericNode child : current.children) {
            if (child.value == childValue) {
                return current;
            }
            GenericNode nested = findParent(child, childValue);
            if (nested != null) {
                return nested;
            }
        }
        return null;
    }

    private TreeSnapshot snapshot(Set<Integer> highlights) {
        return new TreeSnapshot(buildSnapshot(root, highlights));
    }

    private SnapshotNode buildSnapshot(GenericNode node, Set<Integer> highlights) {
        if (node == null) {
            return null;
        }
        List<SnapshotNode> children = new ArrayList<>();
        for (GenericNode child : node.children) {
            children.add(buildSnapshot(child, highlights));
        }
        return new SnapshotNode(String.valueOf(node.value), "#2d6a4f", highlights.contains(node.value), children);
    }

    private GenericNode cloneNode(GenericNode node) {
        if (node == null) {
            return null;
        }
        GenericNode copy = new GenericNode(node.value);
        for (GenericNode child : node.children) {
            copy.children.add(cloneNode(child));
        }
        return copy;
    }

    private static final class GenericNode {
        private int value;
        private final List<GenericNode> children = new ArrayList<>();

        private GenericNode(int value) {
            this.value = value;
        }
    }
}
