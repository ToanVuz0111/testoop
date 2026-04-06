package treevisualizer.tree;

import treevisualizer.model.BinaryTreeNode;
import treevisualizer.model.NodeColor;
import treevisualizer.model.RedBlackTreeNode;
import treevisualizer.model.TreeNode;
import treevisualizer.operation.OperationResult;
import treevisualizer.operation.OperationStep;

import java.util.*;

public class RedBlackTree extends BinaryTree {

    public RedBlackTree() {
        super();
    }

    @Override
    protected BinaryTreeNode newNode(int value) {
        return new RedBlackTreeNode(value);
    }

    @Override
    public OperationResult create() {
        OperationResult result = new OperationResult("Create", "CREATE_RB");
        root = null;
        size = 0;
        result.addStep(new OperationStep("Red-Black Tree created (empty)", 0, Collections.emptyList(), "green", "CREATE_RB"));
        calculateLayout();
        return result;
    }

    @Override
    public OperationResult insert(int... params) {
        if (params.length < 1) {
            OperationResult result = new OperationResult("Insert", "INSERT_RB");
            result.setSuccess(false);
            result.setErrorMessage("No value provided");
            return result;
        }
        int value = params[0];
        OperationResult result = new OperationResult("Insert", "INSERT_RB");

        result.addStep(new OperationStep("Inserting " + value + " using BST insert", 0, Collections.emptyList(), "yellow", "INSERT_RB"));

        // Standard BST insert
        RedBlackTreeNode newNode = (RedBlackTreeNode) newNode(value);
        newNode.setColor(NodeColor.RED);

        if (root == null) {
            root = newNode;
            size++;
            result.addStep(new OperationStep("Root inserted: " + value, 1, List.of(value), "yellow", "INSERT_RB"));
        } else {
            RedBlackTreeNode curr = (RedBlackTreeNode) root;
            RedBlackTreeNode parent = null;

            while (curr != null) {
                result.addStep(new OperationStep("Comparing " + value + " with " + curr.getValue(), 3, List.of(curr.getValue()), "yellow", "INSERT_RB"));
                parent = curr;
                if (value < curr.getValue()) {
                    curr = curr.getLeft();
                } else if (value > curr.getValue()) {
                    curr = curr.getRight();
                } else {
                    result.addStep(new OperationStep("Duplicate value " + value, 4, List.of(curr.getValue()), "red", "INSERT_RB"));
                    return result;
                }
            }

            newNode.setParent(parent);
            if (value < parent.getValue()) {
                parent.setLeft(newNode);
            } else {
                parent.setRight(newNode);
            }
            size++;
        }

        result.addStep(new OperationStep("Node " + value + " inserted as RED", 5, List.of(value), "yellow", "INSERT_RB"));

        // Fix violations
        fixInsert(newNode, result);
        // Root is always black
        ((RedBlackTreeNode) root).setColor(NodeColor.BLACK);

        result.addStep(new OperationStep("Root set to BLACK", 6, List.of(root.getValue()), "blue", "INSERT_RB"));
        result.addStep(new OperationStep("Insertion of " + value + " complete", 7, List.of(value), "green", "INSERT_RB"));

        calculateLayout();
        return result;
    }

    private void fixInsert(RedBlackTreeNode node, OperationResult result) {
        while (node != root && node.getParent() != null && node.getParent().getColor() == NodeColor.RED) {
            RedBlackTreeNode parent = node.getParent();
            RedBlackTreeNode grandparent = parent.getParent();
            if (grandparent == null) break;

            if (parent == grandparent.getLeft()) {
                RedBlackTreeNode uncle = grandparent.getRight();
                if (uncle != null && uncle.getColor() == NodeColor.RED) {
                    // Case 1: Uncle is RED - recolor
                    parent.setColor(NodeColor.BLACK);
                    uncle.setColor(NodeColor.BLACK);
                    grandparent.setColor(NodeColor.RED);
                    result.addStep(new OperationStep("Recoloring: parent, uncle BLACK, grandparent RED",
                            8, List.of(parent.getValue(), uncle.getValue(), grandparent.getValue()), "yellow", "INSERT_RB"));
                    node = grandparent;
                } else {
                    if (node == parent.getRight()) {
                        // Case 2: node is right child - left rotate
                        node = parent;
                        rotateLeft(node);
                        parent = node.getParent();
                        result.addStep(new OperationStep("Left rotation at " + node.getValue(), 10, List.of(node.getValue()), "blue", "INSERT_RB"));
                    }
                    // Case 3: node is left child - right rotate
                    parent.setColor(NodeColor.BLACK);
                    grandparent.setColor(NodeColor.RED);
                    rotateRight(grandparent);
                    result.addStep(new OperationStep("Right rotation at " + grandparent.getValue() + ", recoloring",
                            12, List.of(grandparent.getValue()), "blue", "INSERT_RB"));
                }
            } else {
                RedBlackTreeNode uncle = grandparent.getLeft();
                if (uncle != null && uncle.getColor() == NodeColor.RED) {
                    // Case 1 mirror
                    parent.setColor(NodeColor.BLACK);
                    uncle.setColor(NodeColor.BLACK);
                    grandparent.setColor(NodeColor.RED);
                    result.addStep(new OperationStep("Recoloring (mirror): parent, uncle BLACK, grandparent RED",
                            8, List.of(parent.getValue(), uncle.getValue(), grandparent.getValue()), "yellow", "INSERT_RB"));
                    node = grandparent;
                } else {
                    if (node == parent.getLeft()) {
                        // Case 2 mirror
                        node = parent;
                        rotateRight(node);
                        parent = node.getParent();
                        result.addStep(new OperationStep("Right rotation at " + node.getValue(), 10, List.of(node.getValue()), "blue", "INSERT_RB"));
                    }
                    // Case 3 mirror
                    parent.setColor(NodeColor.BLACK);
                    grandparent.setColor(NodeColor.RED);
                    rotateLeft(grandparent);
                    result.addStep(new OperationStep("Left rotation at " + grandparent.getValue() + ", recoloring",
                            12, List.of(grandparent.getValue()), "blue", "INSERT_RB"));
                }
            }
        }
    }

    private void rotateLeft(RedBlackTreeNode x) {
        RedBlackTreeNode y = x.getRight();
        if (y == null) return;
        x.setRight(y.getLeft());
        if (y.getLeft() != null) y.getLeft().setParent(x);
        y.setParent(x.getParent());
        if (x.getParent() == null) {
            root = y;
        } else if (x == x.getParent().getLeft()) {
            x.getParent().setLeft(y);
        } else {
            x.getParent().setRight(y);
        }
        y.setLeft(x);
        x.setParent(y);
    }

    private void rotateRight(RedBlackTreeNode x) {
        RedBlackTreeNode y = x.getLeft();
        if (y == null) return;
        x.setLeft(y.getRight());
        if (y.getRight() != null) y.getRight().setParent(x);
        y.setParent(x.getParent());
        if (x.getParent() == null) {
            root = y;
        } else if (x == x.getParent().getRight()) {
            x.getParent().setRight(y);
        } else {
            x.getParent().setLeft(y);
        }
        y.setRight(x);
        x.setParent(y);
    }

    @Override
    public OperationResult delete(int value) {
        OperationResult result = new OperationResult("Delete", "DELETE_RB");
        result.addStep(new OperationStep("Deleting " + value + " from Red-Black Tree", 0, Collections.emptyList(), "yellow", "DELETE_RB"));

        BinaryTreeNode[] nodeAndParent = findNodeBST((BinaryTreeNode) root, value, result);
        BinaryTreeNode node = nodeAndParent[0];

        if (node == null) {
            result.setSuccess(false);
            result.setErrorMessage("Value " + value + " not found");
            return result;
        }

        result.addStep(new OperationStep("Found node " + value + ", proceeding with deletion", 2, List.of(value), "red", "DELETE_RB"));

        // Use parent class delete logic, then fix
        BinaryTreeNode parent = nodeAndParent[1];
        deleteNode(node, parent);
        size--;

        // Ensure root is black
        if (root != null) {
            ((RedBlackTreeNode) root).setColor(NodeColor.BLACK);
        }

        result.addStep(new OperationStep("Deletion of " + value + " complete. Tree rebalanced.", 5, Collections.emptyList(), "green", "DELETE_RB"));
        calculateLayout();
        return result;
    }
}
