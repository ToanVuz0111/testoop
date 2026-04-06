package com.demo3.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class RedBlackTreeModel implements TreeModel {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private final RBNode nil = new RBNode(0, BLACK);
    private RBNode root = nil;

    public RedBlackTreeModel() {
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;
    }

    @Override
    public TreeType type() {
        return TreeType.RED_BLACK;
    }

    @Override
    public TreeModel copy() {
        RedBlackTreeModel copy = new RedBlackTreeModel();
        copy.root = cloneNode(root, copy.nil, copy.nil);
        return copy;
    }

    @Override
    public TreeSnapshot snapshot() {
        return snapshot(Set.of());
    }

    @Override
    public OperationTrace create() {
        root = nil;
        return OperationTrace.single("Create", "Created a new empty Red-Black Tree.", List.of("root = NIL", "render empty tree"), 1, snapshot());
    }

    @Override
    public OperationTrace insert(Integer parentValue, int newValue) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "insert node using BST order",
                "color the new node red",
                "fix red-black violations with rotations/recoloring",
                "color the root black"
        );
        if (findNode(newValue) != nil) {
            return OperationTrace.single("Insert", "Value " + newValue + " already exists.", pseudocode, 0, snapshot(Set.of(newValue)));
        }
        RBNode node = new RBNode(newValue, RED);
        node.left = nil;
        node.right = nil;

        RBNode parent = nil;
        RBNode current = root;
        while (current != nil) {
            parent = current;
            steps.add(step("Insert", "Compare " + newValue + " with node " + current.value + ".", pseudocode, 0, Set.of(current.value)));
            current = node.value < current.value ? current.left : current.right;
        }
        node.parent = parent;
        if (parent == nil) {
            root = node;
            steps.add(step("Insert", "Tree was empty, inserted " + newValue + " as the root.", pseudocode, 0, Set.of(newValue)));
        } else if (node.value < parent.value) {
            parent.left = node;
            steps.add(step("Insert", "Attached " + newValue + " as the left child of " + parent.value + ".", pseudocode, 0, Set.of(parent.value, newValue)));
        } else {
            parent.right = node;
            steps.add(step("Insert", "Attached " + newValue + " as the right child of " + parent.value + ".", pseudocode, 0, Set.of(parent.value, newValue)));
        }
        steps.add(step("Insert", "New node " + newValue + " starts red.", pseudocode, 1, Set.of(newValue)));
        fixInsert(node, steps, pseudocode);
        root.color = BLACK;
        steps.add(step("Insert", "Ensure the root stays black.", pseudocode, 3, Set.of(root.value)));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace delete(int value) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "find node by BST search",
                "remove node or swap with successor",
                "if a black node was removed: fix double-black",
                "render updated tree"
        );
        RBNode target = findNode(value);
        if (target == nil) {
            return OperationTrace.single("Delete", "Value " + value + " was not found.", pseudocode, 0, snapshot());
        }
        steps.add(step("Delete", "Found node " + value + ".", pseudocode, 0, Set.of(value)));

        RBNode removed = target;
        boolean removedColor = removed.color;
        RBNode movedUp;

        if (target.left == nil) {
            movedUp = target.right;
            transplant(target, target.right);
            steps.add(step("Delete", "Target has no left child, replace it with its right subtree.", pseudocode, 1, highlights(target, movedUp)));
        } else if (target.right == nil) {
            movedUp = target.left;
            transplant(target, target.left);
            steps.add(step("Delete", "Target has no right child, replace it with its left subtree.", pseudocode, 1, highlights(target, movedUp)));
        } else {
            removed = minimum(target.right);
            removedColor = removed.color;
            movedUp = removed.right;
            steps.add(step("Delete", "Target has two children, successor is " + removed.value + ".", pseudocode, 1, Set.of(target.value, removed.value)));
            if (removed.parent == target) {
                movedUp.parent = removed;
            } else {
                transplant(removed, removed.right);
                removed.right = target.right;
                removed.right.parent = removed;
                steps.add(step("Delete", "Move successor upward before replacing target.", pseudocode, 1, highlights(removed, movedUp)));
            }
            transplant(target, removed);
            removed.left = target.left;
            removed.left.parent = removed;
            removed.color = target.color;
            steps.add(step("Delete", "Successor " + removed.value + " takes the position of " + target.value + ".", pseudocode, 1, Set.of(removed.value)));
        }

        if (removedColor == BLACK) {
            steps.add(step("Delete", "Removed a black node, start double-black fix.", pseudocode, 2, highlights(movedUp)));
            fixDelete(movedUp, steps, pseudocode);
        }
        if (root != nil) {
            root.color = BLACK;
        }
        steps.add(step("Delete", "Deletion complete. Red-Black properties restored.", pseudocode, 3, root == nil ? Set.of() : Set.of(root.value)));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace update(int currentValue, int newValue) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "delete the existing value",
                "insert the new value",
                "restore red-black properties",
                "render updated tree"
        );
        if (findNode(currentValue) == nil) {
            return OperationTrace.single("Update", "Value " + currentValue + " was not found.", pseudocode, 0, snapshot());
        }
        if (currentValue != newValue && findNode(newValue) != nil) {
            return OperationTrace.single("Update", "Value " + newValue + " already exists.", pseudocode, 1, snapshot(Set.of(currentValue)));
        }
        steps.add(step("Update", "Delete " + currentValue + " first.", pseudocode, 0, Set.of(currentValue)));
        delete(currentValue);
        steps.add(step("Update", "Insert " + newValue + " back into the tree.", pseudocode, 1, Set.of()));
        insert(null, newValue);
        steps.add(step("Update", "Updated " + currentValue + " to " + newValue + ".", pseudocode, 3, Set.of(newValue)));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace search(int value) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = List.of(
                "start at root",
                "move left if target is smaller",
                "move right if target is larger",
                "stop when target or NIL is reached"
        );
        RBNode current = root;
        if (current == nil) {
            return OperationTrace.single("Search", "Tree is empty.", pseudocode, 0, snapshot());
        }
        while (current != nil) {
            steps.add(step("Search", "Checking node " + current.value + ".", pseudocode, 0, Set.of(current.value)));
            if (current.value == value) {
                steps.add(step("Search", "Found " + value + ".", pseudocode, 3, Set.of(value)));
                return new OperationTrace(steps);
            }
            if (value < current.value) {
                steps.add(step("Search", value + " is smaller than " + current.value + ", move left.", pseudocode, 1, Set.of(current.value)));
                current = current.left;
            } else {
                steps.add(step("Search", value + " is larger than " + current.value + ", move right.", pseudocode, 2, Set.of(current.value)));
                current = current.right;
            }
        }
        steps.add(step("Search", "Reached NIL. " + value + " was not found.", pseudocode, 3, Set.of()));
        return new OperationTrace(steps);
    }

    @Override
    public OperationTrace traverse(TraversalType traversalType) {
        List<OperationStep> steps = new ArrayList<>();
        List<String> pseudocode = traversalType == TraversalType.DFS
                ? List.of("visit node", "traverse left", "traverse right")
                : List.of("enqueue root", "dequeue node", "visit node", "enqueue children");
        if (root == nil) {
            return OperationTrace.single("Traverse", "Tree is empty.", pseudocode, 0, snapshot());
        }
        if (traversalType == TraversalType.DFS) {
            preorder(root, steps, pseudocode);
        } else {
            Queue<RBNode> queue = new ArrayDeque<>();
            queue.add(root);
            while (!queue.isEmpty()) {
                RBNode current = queue.remove();
                steps.add(new OperationStep("Traverse BFS", "Visited " + current.value + ".", pseudocode, 2, snapshot(Set.of(current.value))));
                if (current.left != nil) {
                    queue.add(current.left);
                }
                if (current.right != nil) {
                    queue.add(current.right);
                }
            }
        }
        return new OperationTrace(steps);
    }

    private void preorder(RBNode node, List<OperationStep> steps, List<String> pseudocode) {
        if (node == nil) {
            return;
        }
        steps.add(new OperationStep("Traverse DFS", "Visited " + node.value + ".", pseudocode, 0, snapshot(Set.of(node.value))));
        preorder(node.left, steps, pseudocode);
        preorder(node.right, steps, pseudocode);
    }

    private RBNode findNode(int value) {
        RBNode current = root;
        while (current != nil) {
            if (value == current.value) {
                return current;
            }
            current = value < current.value ? current.left : current.right;
        }
        return nil;
    }

    private void fixInsert(RBNode node, List<OperationStep> steps, List<String> pseudocode) {
        RBNode current = node;
        while (current.parent.color == RED) {
            if (current.parent == current.parent.parent.left) {
                RBNode uncle = current.parent.parent.right;
                if (uncle.color == RED) {
                    steps.add(step("Insert Fix", "Parent and uncle are red. Recolor around grandparent " + current.parent.parent.value + ".", pseudocode, 2, highlights(current, current.parent, uncle, current.parent.parent)));
                    current.parent.color = BLACK;
                    uncle.color = BLACK;
                    current.parent.parent.color = RED;
                    current = current.parent.parent;
                } else {
                    if (current == current.parent.right) {
                        steps.add(step("Insert Fix", "Left-Right case detected. Rotate left at parent " + current.parent.value + ".", pseudocode, 2, highlights(current, current.parent)));
                        current = current.parent;
                        rotateLeft(current);
                    }
                    steps.add(step("Insert Fix", "Left-Left case. Recolor and rotate right at grandparent " + current.parent.parent.value + ".", pseudocode, 2, highlights(current, current.parent, current.parent.parent)));
                    current.parent.color = BLACK;
                    current.parent.parent.color = RED;
                    rotateRight(current.parent.parent);
                }
            } else {
                RBNode uncle = current.parent.parent.left;
                if (uncle.color == RED) {
                    steps.add(step("Insert Fix", "Parent and uncle are red. Recolor around grandparent " + current.parent.parent.value + ".", pseudocode, 2, highlights(current, current.parent, uncle, current.parent.parent)));
                    current.parent.color = BLACK;
                    uncle.color = BLACK;
                    current.parent.parent.color = RED;
                    current = current.parent.parent;
                } else {
                    if (current == current.parent.left) {
                        steps.add(step("Insert Fix", "Right-Left case detected. Rotate right at parent " + current.parent.value + ".", pseudocode, 2, highlights(current, current.parent)));
                        current = current.parent;
                        rotateRight(current);
                    }
                    steps.add(step("Insert Fix", "Right-Right case. Recolor and rotate left at grandparent " + current.parent.parent.value + ".", pseudocode, 2, highlights(current, current.parent, current.parent.parent)));
                    current.parent.color = BLACK;
                    current.parent.parent.color = RED;
                    rotateLeft(current.parent.parent);
                }
            }
            if (current == root) {
                break;
            }
        }
    }

    private void fixDelete(RBNode node, List<OperationStep> steps, List<String> pseudocode) {
        RBNode current = node;
        while (current != root && current.color == BLACK) {
            if (current == current.parent.left) {
                RBNode sibling = current.parent.right;
                if (sibling.color == RED) {
                    steps.add(step("Delete Fix", "Sibling " + sibling.value + " is red. Recolor and rotate left at parent " + current.parent.value + ".", pseudocode, 2, highlights(current, sibling, current.parent)));
                    sibling.color = BLACK;
                    current.parent.color = RED;
                    rotateLeft(current.parent);
                    sibling = current.parent.right;
                }
                if (sibling.left.color == BLACK && sibling.right.color == BLACK) {
                    steps.add(step("Delete Fix", "Sibling and both nephews are black. Push the double-black upward.", pseudocode, 2, highlights(current, sibling, current.parent)));
                    sibling.color = RED;
                    current = current.parent;
                } else {
                    if (sibling.right.color == BLACK) {
                        steps.add(step("Delete Fix", "Far nephew is black and near nephew is red. Rotate right at sibling " + sibling.value + ".", pseudocode, 2, highlights(sibling, sibling.left, current.parent)));
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = current.parent.right;
                    }
                    steps.add(step("Delete Fix", "Rotate left at parent " + current.parent.value + " to eliminate the double-black.", pseudocode, 2, highlights(current, sibling, current.parent)));
                    sibling.color = current.parent.color;
                    current.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(current.parent);
                    current = root;
                }
            } else {
                RBNode sibling = current.parent.left;
                if (sibling.color == RED) {
                    steps.add(step("Delete Fix", "Sibling " + sibling.value + " is red. Recolor and rotate right at parent " + current.parent.value + ".", pseudocode, 2, highlights(current, sibling, current.parent)));
                    sibling.color = BLACK;
                    current.parent.color = RED;
                    rotateRight(current.parent);
                    sibling = current.parent.left;
                }
                if (sibling.right.color == BLACK && sibling.left.color == BLACK) {
                    steps.add(step("Delete Fix", "Sibling and both nephews are black. Push the double-black upward.", pseudocode, 2, highlights(current, sibling, current.parent)));
                    sibling.color = RED;
                    current = current.parent;
                } else {
                    if (sibling.left.color == BLACK) {
                        steps.add(step("Delete Fix", "Far nephew is black and near nephew is red. Rotate left at sibling " + sibling.value + ".", pseudocode, 2, highlights(sibling, sibling.right, current.parent)));
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = current.parent.left;
                    }
                    steps.add(step("Delete Fix", "Rotate right at parent " + current.parent.value + " to eliminate the double-black.", pseudocode, 2, highlights(current, sibling, current.parent)));
                    sibling.color = current.parent.color;
                    current.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(current.parent);
                    current = root;
                }
            }
        }
        current.color = BLACK;
        if (current != nil) {
            steps.add(step("Delete Fix", "Color node " + current.value + " black to finish rebalancing.", pseudocode, 2, Set.of(current.value)));
        }
    }

    private void rotateLeft(RBNode node) {
        RBNode pivot = node.right;
        node.right = pivot.left;
        if (pivot.left != nil) {
            pivot.left.parent = node;
        }
        pivot.parent = node.parent;
        if (node.parent == nil) {
            root = pivot;
        } else if (node == node.parent.left) {
            node.parent.left = pivot;
        } else {
            node.parent.right = pivot;
        }
        pivot.left = node;
        node.parent = pivot;
    }

    private void rotateRight(RBNode node) {
        RBNode pivot = node.left;
        node.left = pivot.right;
        if (pivot.right != nil) {
            pivot.right.parent = node;
        }
        pivot.parent = node.parent;
        if (node.parent == nil) {
            root = pivot;
        } else if (node == node.parent.right) {
            node.parent.right = pivot;
        } else {
            node.parent.left = pivot;
        }
        pivot.right = node;
        node.parent = pivot;
    }

    private void transplant(RBNode target, RBNode replacement) {
        if (target.parent == nil) {
            root = replacement;
        } else if (target == target.parent.left) {
            target.parent.left = replacement;
        } else {
            target.parent.right = replacement;
        }
        replacement.parent = target.parent;
    }

    private RBNode minimum(RBNode node) {
        RBNode current = node;
        while (current.left != nil) {
            current = current.left;
        }
        return current;
    }

    private TreeSnapshot snapshot(Set<Integer> highlights) {
        return new TreeSnapshot(buildSnapshot(root, highlights));
    }

    private OperationStep step(String title, String message, List<String> pseudocode, int activeLine, Set<Integer> highlights) {
        return new OperationStep(title, message, pseudocode, activeLine, snapshot(highlights));
    }

    private Set<Integer> highlights(RBNode... nodes) {
        Set<Integer> values = new java.util.HashSet<>();
        for (RBNode node : nodes) {
            if (node != null && node != nil) {
                values.add(node.value);
            }
        }
        return values;
    }

    private SnapshotNode buildSnapshot(RBNode node, Set<Integer> highlights) {
        if (node == nil) {
            return null;
        }
        List<SnapshotNode> children = new ArrayList<>();
        if (node.left != nil) {
            children.add(buildSnapshot(node.left, highlights));
        }
        if (node.right != nil) {
            children.add(buildSnapshot(node.right, highlights));
        }
        return new SnapshotNode(String.valueOf(node.value), node.color == RED ? "#c1121f" : "#1f2933", highlights.contains(node.value), children);
    }

    private RBNode cloneNode(RBNode node, RBNode parent, RBNode nilNode) {
        if (node == nil) {
            return nilNode;
        }
        RBNode copy = new RBNode(node.value, node.color);
        copy.parent = parent;
        copy.left = cloneNode(node.left, copy, nilNode);
        copy.right = cloneNode(node.right, copy, nilNode);
        return copy;
    }

    private static final class RBNode {
        private final int value;
        private boolean color;
        private RBNode left;
        private RBNode right;
        private RBNode parent;

        private RBNode(int value, boolean color) {
            this.value = value;
            this.color = color;
        }
    }
}
