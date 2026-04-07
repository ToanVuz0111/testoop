package com.demo3.service;

import com.demo3.tree.BinarySearchTreeModel;
import com.demo3.tree.GenericTreeModel;
import com.demo3.tree.OperationTrace;
import com.demo3.tree.RedBlackTreeModel;
import com.demo3.tree.TreeModel;
import com.demo3.tree.TreeType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

public class TreeOperationService {
    private TreeModel currentModel = new GenericTreeModel();
    private TreeType currentTreeType = TreeType.GENERIC;
    private OperationTrace currentTrace = OperationTrace.single(
            "Welcome",
            "Create or select a tree type to begin visualizing operations.",
            java.util.List.of("Choose a tree type", "Run an operation", "Step through the result"),
            0,
            new GenericTreeModel().snapshot()
    );
    private final Deque<TreeModel> undoStack = new ArrayDeque<>();
    private final Deque<TreeModel> redoStack = new ArrayDeque<>();

    public OperationTrace getCurrentTrace() {
        return currentTrace;
    }

    public TreeType getCurrentTreeType() {
        return currentTreeType;
    }

    public TreeModel getCurrentModel() {
        return currentModel;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public OperationTrace switchTree(TreeType treeType) {
        currentTreeType = treeType;
        currentModel = createModel(treeType);
        undoStack.clear();
        redoStack.clear();
        currentTrace = OperationTrace.single(
                treeType.getDisplayName(),
                "New " + treeType.getDisplayName() + " ready. Use the controls to create and manipulate nodes.",
                java.util.List.of(
                        "Create empty tree",
                        "Insert, delete, update, search, or traverse",
                        "Use step controls to inspect the result"
                ),
                0,
                currentModel.snapshot()
        );
        return currentTrace;
    }

    public OperationTrace create() {
        return perform(TreeModel::create);
    }

    public OperationTrace insert(Integer parent, int value) {
        return perform(model -> model.insert(parent, value));
    }

    public OperationTrace delete(int value) {
        return perform(model -> model.delete(value));
    }

    public OperationTrace update(int currentValue, int newValue) {
        return perform(model -> model.update(currentValue, newValue));
    }

    public OperationTrace search(int value) {
        return perform(model -> model.search(value));
    }

    public OperationTrace traverse(com.demo3.tree.TraversalType traversalType) {
        return perform(model -> model.traverse(traversalType));
    }

    public OperationTrace undo() {
        if (undoStack.isEmpty()) {
            return currentTrace;
        }
        redoStack.push(currentModel.copy());
        currentModel = undoStack.pop();
        currentTreeType = currentModel.type();
        currentTrace = OperationTrace.single(
                "Undo",
                "Restored the previous tree state.",
                java.util.List.of("Restore previous snapshot", "Render tree"),
                1,
                currentModel.snapshot()
        );
        return currentTrace;
    }

    public OperationTrace redo() {
        if (redoStack.isEmpty()) {
            return currentTrace;
        }
        undoStack.push(currentModel.copy());
        currentModel = redoStack.pop();
        currentTreeType = currentModel.type();
        currentTrace = OperationTrace.single(
                "Redo",
                "Reapplied the next tree state.",
                java.util.List.of("Restore next snapshot", "Render tree"),
                1,
                currentModel.snapshot()
        );
        return currentTrace;
    }

    private OperationTrace perform(Function<TreeModel, OperationTrace> action) {
        undoStack.push(currentModel.copy());
        redoStack.clear();
        currentTrace = action.apply(currentModel);
        return currentTrace;
    }

    private TreeModel createModel(TreeType treeType) {
        return switch (treeType) {
            case GENERIC -> new GenericTreeModel();
            case BINARY_SEARCH -> new BinarySearchTreeModel();
            case RED_BLACK -> new RedBlackTreeModel();
        };
    }
}
