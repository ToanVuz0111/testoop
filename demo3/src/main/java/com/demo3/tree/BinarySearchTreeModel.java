package com.demo3.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BinarySearchTreeModel implements TreeModel {
    private BinaryNode root;

    @Override
    public TreeType type() {
        return TreeType.BINARY_SEARCH;
    }

    @Override
    public TreeModel copy() {
        BinarySearchTreeModel copy = new BinarySearchTreeModel();
        copy.root = cloneNode(root, null);
        return copy;
    }

    @Override
    public TreeSnapshot snapshot() {
        return snapshot(Set.of());
    }

    @Override
    public OperationTrace create() {
        root = null;
        return OperationTrace.single("Create", "Created a new empty Binary Search Tree.", List.of("root = null", "render empty tree"), 1, snapshot());
    }

    @Override
    public OperationTrace insert(Integer parentValue, int newValue) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "start at root",
                "move left or right by BST ordering",
                "insert when a null child is reached",
                "render updated tree"
        );
        if (root == null) {
            steps.add(new OperationStep("Insert", "Tree is empty, so the new value becomes the root.", pseudocode, 0, snapshot()));
            root = new BinaryNode(newValue);
            steps.add(new OperationStep("Insert", "Inserted " + newValue + " as the root.", pseudocode, 3, snapshot(Set.of(newValue))));
            return new OperationTrace(steps);
        }
        BinaryNode current = root;
        BinaryNode parent = null;
        while (current != null) {
            parent = current;
            steps.add(new OperationStep("Insert", "Compare with node " + current.value + ".", pseudocode, 1, snapshot(Set.of(current.value))));
            if (newValue == current.value) {
                return OperationTrace.single("Insert", "Value " + newValue + " already exists.", pseudocode, 1, snapshot(Set.of(newValue)));
            }
            current = newValue < current.value ? current.left : current.right;
        }
        BinaryNode node = new BinaryNode(newValue);
        node.parent = parent;
        if (newValue < parent.value) {
            parent.left = node;
        } else {
            parent.right = node;
        }
        steps.add(new OperationStep("Insert", "Inserted " + newValue + " under parent " + parent.value + ".", pseudocode, 2, snapshot(Set.of(newValue, parent.value))));
        steps.add(new OperationStep("Insert", "Rendering updated BST.", pseudocode, 3, snapshot(Set.of(newValue))));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace delete(int value) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "find node by BST search",
                "if node has at most one child: splice it out",
                "else replace with inorder successor",
                "render updated tree"
        );
        BinaryNode target = findNode(value);
        if (target == null) {
            return OperationTrace.single("Delete", "Value " + value + " was not found.", pseudocode, 0, snapshot());
        }
        steps.add(new OperationStep("Delete", "Found node " + value + ".", pseudocode, 0, snapshot(Set.of(value))));
        if (target.left == null) {
            transplant(target, target.right);
            steps.add(new OperationStep("Delete", "Node has no left child, transplant right subtree.", pseudocode, 1, snapshot()));
        } else if (target.right == null) {
            transplant(target, target.left);
            steps.add(new OperationStep("Delete", "Node has no right child, transplant left subtree.", pseudocode, 1, snapshot()));
        } else {
            BinaryNode successor = minimum(target.right);
            steps.add(new OperationStep("Delete", "Node has two children, successor is " + successor.value + ".", pseudocode, 2, snapshot(Set.of(value, successor.value))));
            if (successor.parent != target) {
                transplant(successor, successor.right);
                successor.right = target.right;
                successor.right.parent = successor;
            }
            transplant(target, successor);
            successor.left = target.left;
            successor.left.parent = successor;
        }
        steps.add(new OperationStep("Delete", "Deleted " + value + " from the BST.", pseudocode, 3, snapshot()));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace update(int currentValue, int newValue) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "delete the current value",
                "insert the new value using BST ordering",
                "render updated tree"
        );
        if (findNode(currentValue) == null) {
            return OperationTrace.single("Update", "Value " + currentValue + " was not found.", pseudocode, 0, snapshot());
        }
        if (currentValue != newValue && findNode(newValue) != null) {
            return OperationTrace.single("Update", "Value " + newValue + " already exists.", pseudocode, 1, snapshot(Set.of(currentValue)));
        }
        steps.add(new OperationStep("Update", "Delete the old value " + currentValue + ".", pseudocode, 0, snapshot(Set.of(currentValue))));
        delete(currentValue);
        steps.add(new OperationStep("Update", "Insert the new value " + newValue + ".", pseudocode, 1, snapshot()));
        insert(null, newValue);
        steps.add(new OperationStep("Update", "Updated " + currentValue + " to " + newValue + ".", pseudocode, 2, snapshot(Set.of(newValue))));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace search(int value) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "start at root",
                "go left if target is smaller",
                "go right if target is larger",
                "stop when found or null"
        );
        BinaryNode current = root;
        if (current == null) {
            return OperationTrace.single("Search", "Tree is empty.", pseudocode, 0, snapshot());
        }
        while (current != null) {
            steps.add(new OperationStep("Search", "Checking node " + current.value + ".", pseudocode, 0, snapshot(Set.of(current.value))));
            if (value == current.value) {
                steps.add(new OperationStep("Search", "Found " + value + ".", pseudocode, 3, snapshot(Set.of(value))));
                return new OperationTrace(steps);
            }
            if (value < current.value) {
                steps.add(new OperationStep("Search", value + " is smaller than " + current.value + ", move left.", pseudocode, 1, snapshot(Set.of(current.value))));
                current = current.left;
            } else {
                steps.add(new OperationStep("Search", value + " is larger than " + current.value + ", move right.", pseudocode, 2, snapshot(Set.of(current.value))));
                current = current.right;
            }
        }
        steps.add(new OperationStep("Search", "Reached a null child. " + value + " was not found.", pseudocode, 3, snapshot()));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace traverse(TraversalType traversalType) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = traversalType == TraversalType.DFS
                ? List.of("visit node", "traverse left", "traverse right")
                : List.of("enqueue root", "dequeue node", "visit node", "enqueue children");
        if (root == null) {
            return OperationTrace.single("Traverse", "Tree is empty.", pseudocode, 0, snapshot());
        }
        if (traversalType == TraversalType.DFS) {
            preorder(root, steps, pseudocode);
        } else {
            Queue<BinaryNode> queue = new ArrayDeque<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                BinaryNode current = queue.remove();
                steps.add(new OperationStep("Traverse BFS", "Visited " + current.value + ".", pseudocode, 2, snapshot(Set.of(current.value))));
                if (current.left != null) {
                    queue.add(current.left);
                }
                if (current.right != null) {
                    queue.add(current.right);
                }
            }
        }
        return new OperationTrace(steps);
    }

    private void preorder(BinaryNode node, List<OperationStep> steps, List<String> pseudocode) {
        if (node == null) {
            return;
        }
        steps.add(new OperationStep("Traverse DFS", "Visited " + node.value + ".", pseudocode, 0, snapshot(Set.of(node.value))));
        preorder(node.left, steps, pseudocode);
        preorder(node.right, steps, pseudocode);
    }

    private BinaryNode findNode(int value) {
        BinaryNode current = root;
        while (current != null) {
            if (value == current.value) {
                return current;
            }
            current = value < current.value ? current.left : current.right;
        }
        return null;
    }

    private BinaryNode minimum(BinaryNode node) {
        BinaryNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private void transplant(BinaryNode target, BinaryNode replacement) {
        if (target.parent == null) {
            root = replacement;
        } else if (target == target.parent.left) {
            target.parent.left = replacement;
        } else {
            target.parent.right = replacement;
        }
        if (replacement != null) {
            replacement.parent = target.parent;
        }
    }

    private TreeSnapshot snapshot(Set<Integer> highlights) {
        return new TreeSnapshot(buildSnapshot(root, highlights));
    }

    private SnapshotNode buildSnapshot(BinaryNode node, Set<Integer> highlights) {
        if (node == null) {
            return null;
        }
        List<SnapshotNode> children = new ArrayList<>();
        if (node.left != null) {
            children.add(buildSnapshot(node.left, highlights));
        }
        if (node.right != null) {
            children.add(buildSnapshot(node.right, highlights));
        }
        return new SnapshotNode(String.valueOf(node.value), "#33658a", highlights.contains(node.value), children);
    }

    private BinaryNode cloneNode(BinaryNode node, BinaryNode parent) {
        if (node == null) {
            return null;
        }
        BinaryNode copy = new BinaryNode(node.value);
        copy.parent = parent;
        copy.left = cloneNode(node.left, copy);
        copy.right = cloneNode(node.right, copy);
        return copy;
    }

    private static final class BinaryNode {
        private final int value;
        private BinaryNode left;
        private BinaryNode right;
        private BinaryNode parent;

        private BinaryNode(int value) {
            this.value = value;
        }
    }
}
