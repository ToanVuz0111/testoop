package treevisualizer.tree;

import treevisualizer.model.BinaryTreeNode;
import treevisualizer.model.TreeNode;
import treevisualizer.operation.OperationResult;
import treevisualizer.operation.OperationStep;

import java.util.*;

public class BinaryTree extends AbstractTree {

    public BinaryTree() {
        super();
    }

    protected BinaryTreeNode newNode(int value) {
        return new BinaryTreeNode(value);
    }

    @Override
    public OperationResult create() {
        OperationResult result = new OperationResult("Create", "CREATE_BINARY");
        root = null;
        size = 0;
        result.addStep(new OperationStep("Binary Search Tree created (empty)", 0, Collections.emptyList(), "green", "CREATE_BINARY"));
        calculateLayout();
        return result;
    }

    @Override
    public OperationResult insert(int... params) {
        OperationResult result = new OperationResult("Insert", "INSERT_BINARY");
        if (params.length < 1) {
            result.setSuccess(false);
            result.setErrorMessage("No value provided");
            return result;
        }
        int value = params[0];

        result.addStep(new OperationStep("Starting insertion of " + value, 0, Collections.emptyList(), "yellow", "INSERT_BINARY"));

        if (root == null) {
            root = newNode(value);
            size++;
            result.addStep(new OperationStep("Tree is empty. Creating root with value " + value, 1, List.of(value), "green", "INSERT_BINARY"));
            calculateLayout();
            return result;
        }

        result.addStep(new OperationStep("Tree is not empty. Starting at root.", 4, List.of(root.getValue()), "yellow", "INSERT_BINARY"));

        BinaryTreeNode curr = (BinaryTreeNode) root;
        BinaryTreeNode parent = null;

        while (curr != null) {
            result.addStep(new OperationStep("Comparing " + value + " with " + curr.getValue(), 5, List.of(curr.getValue()), "yellow", "INSERT_BINARY"));
            parent = curr;
            if (value < curr.getValue()) {
                result.addStep(new OperationStep(value + " < " + curr.getValue() + ", going left", 6, List.of(curr.getValue()), "yellow", "INSERT_BINARY"));
                curr = curr.getLeft();
            } else if (value > curr.getValue()) {
                result.addStep(new OperationStep(value + " > " + curr.getValue() + ", going right", 11, List.of(curr.getValue()), "yellow", "INSERT_BINARY"));
                curr = curr.getRight();
            } else {
                result.addStep(new OperationStep("Duplicate value " + value + " - not inserted", 16, List.of(curr.getValue()), "red", "INSERT_BINARY"));
                return result;
            }
        }

        BinaryTreeNode newNode = newNode(value);
        newNode.setParent(parent);
        if (value < parent.getValue()) {
            parent.setLeft(newNode);
            result.addStep(new OperationStep("Inserting " + value + " as left child of " + parent.getValue(), 8, List.of(value), "green", "INSERT_BINARY"));
        } else {
            parent.setRight(newNode);
            result.addStep(new OperationStep("Inserting " + value + " as right child of " + parent.getValue(), 13, List.of(value), "green", "INSERT_BINARY"));
        }
        size++;
        calculateLayout();
        return result;
    }

    @Override
    public OperationResult delete(int value) {
        OperationResult result = new OperationResult("Delete", "DELETE_BINARY");

        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Tree is empty");
            return result;
        }

        result.addStep(new OperationStep("Searching for node " + value, 0, List.of(root.getValue()), "yellow", "DELETE_BINARY"));

        BinaryTreeNode[] nodeAndParent = findNodeBST((BinaryTreeNode) root, value, result);
        BinaryTreeNode node = nodeAndParent[0];
        BinaryTreeNode parent = nodeAndParent[1];

        if (node == null) {
            result.setSuccess(false);
            result.setErrorMessage("Value " + value + " not found");
            result.addStep(new OperationStep("Value " + value + " not found", 3, Collections.emptyList(), "red", "DELETE_BINARY"));
            return result;
        }

        result.addStep(new OperationStep("Found node " + value + ", deleting...", 4, List.of(value), "red", "DELETE_BINARY"));

        deleteNode(node, parent);
        size--;
        result.addStep(new OperationStep("Deletion of " + value + " complete", 5, Collections.emptyList(), "green", "DELETE_BINARY"));
        calculateLayout();
        return result;
    }

    protected void deleteNode(BinaryTreeNode node, BinaryTreeNode parent) {
        if (node.getLeft() == null && node.getRight() == null) {
            replaceChild(parent, node, null);
        } else if (node.getLeft() == null) {
            replaceChild(parent, node, node.getRight());
            if (node.getRight() != null) node.getRight().setParent(parent);
        } else if (node.getRight() == null) {
            replaceChild(parent, node, node.getLeft());
            if (node.getLeft() != null) node.getLeft().setParent(parent);
        } else {
            // Find inorder successor (min of right subtree)
            BinaryTreeNode successorParent = node;
            BinaryTreeNode successor = node.getRight();
            while (successor.getLeft() != null) {
                successorParent = successor;
                successor = successor.getLeft();
            }
            node.setValue(successor.getValue());
            deleteNode(successor, successorParent);
        }
    }

    protected void replaceChild(BinaryTreeNode parent, BinaryTreeNode child, BinaryTreeNode replacement) {
        if (parent == null) {
            root = replacement;
            if (replacement != null) ((BinaryTreeNode) replacement).setParent(null);
        } else if (parent.getLeft() == child) {
            parent.setLeft((BinaryTreeNode) replacement);
            if (replacement != null) ((BinaryTreeNode) replacement).setParent(parent);
        } else {
            parent.setRight((BinaryTreeNode) replacement);
            if (replacement != null) ((BinaryTreeNode) replacement).setParent(parent);
        }
    }

    @Override
    public OperationResult update(int oldValue, int newValue) {
        OperationResult result = new OperationResult("Update", "UPDATE_BINARY");
        result.addStep(new OperationStep("Deleting old value " + oldValue, 0, Collections.emptyList(), "yellow", "UPDATE_BINARY"));
        OperationResult delResult = delete(oldValue);
        if (!delResult.isSuccess()) {
            result.setSuccess(false);
            result.setErrorMessage(delResult.getErrorMessage());
            return result;
        }
        result.addStep(new OperationStep("Inserting new value " + newValue, 1, Collections.emptyList(), "yellow", "UPDATE_BINARY"));
        OperationResult insResult = insert(newValue);
        if (!insResult.isSuccess()) {
            result.setSuccess(false);
            result.setErrorMessage(insResult.getErrorMessage());
            return result;
        }
        result.addStep(new OperationStep("Update complete: " + oldValue + " → " + newValue, 2, List.of(newValue), "green", "UPDATE_BINARY"));
        calculateLayout();
        return result;
    }

    @Override
    public OperationResult traverse(String algorithm) {
        OperationResult result = new OperationResult("Traverse (" + algorithm + ")", "TRAVERSE_BINARY");

        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Tree is empty");
            return result;
        }

        List<Integer> order = new ArrayList<>();
        switch (algorithm) {
            case "DFS_PREORDER" -> preorder((BinaryTreeNode) root, result, order, "TRAVERSE_BINARY");
            case "DFS_INORDER" -> inorder((BinaryTreeNode) root, result, order, "TRAVERSE_BINARY");
            case "DFS_POSTORDER" -> postorder((BinaryTreeNode) root, result, order, "TRAVERSE_BINARY");
            case "BFS" -> bfs((BinaryTreeNode) root, result, order, "TRAVERSE_BINARY");
            default -> preorder((BinaryTreeNode) root, result, order, "TRAVERSE_BINARY");
        }

        result.addStep(new OperationStep("Traversal complete. Order: " + order, 9, Collections.emptyList(), "green", "TRAVERSE_BINARY"));
        return result;
    }

    protected void preorder(BinaryTreeNode node, OperationResult result, List<Integer> order, String codeKey) {
        if (node == null) return;
        order.add(node.getValue());
        result.addStep(new OperationStep("Visiting node " + node.getValue() + " (preorder)", 2, List.of(node.getValue()), "yellow", codeKey));
        preorder(node.getLeft(), result, order, codeKey);
        preorder(node.getRight(), result, order, codeKey);
    }

    protected void inorder(BinaryTreeNode node, OperationResult result, List<Integer> order, String codeKey) {
        if (node == null) return;
        inorder(node.getLeft(), result, order, codeKey);
        order.add(node.getValue());
        result.addStep(new OperationStep("Visiting node " + node.getValue() + " (inorder)", 4, List.of(node.getValue()), "yellow", codeKey));
        inorder(node.getRight(), result, order, codeKey);
    }

    protected void postorder(BinaryTreeNode node, OperationResult result, List<Integer> order, String codeKey) {
        if (node == null) return;
        postorder(node.getLeft(), result, order, codeKey);
        postorder(node.getRight(), result, order, codeKey);
        order.add(node.getValue());
        result.addStep(new OperationStep("Visiting node " + node.getValue() + " (postorder)", 6, List.of(node.getValue()), "yellow", codeKey));
    }

    protected void bfs(BinaryTreeNode root, OperationResult result, List<Integer> order, String codeKey) {
        Queue<BinaryTreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            BinaryTreeNode node = queue.poll();
            order.add(node.getValue());
            result.addStep(new OperationStep("Visiting node " + node.getValue() + " (BFS)", 8, List.of(node.getValue()), "yellow", codeKey));
            if (node.getLeft() != null) queue.add(node.getLeft());
            if (node.getRight() != null) queue.add(node.getRight());
        }
    }

    @Override
    public OperationResult search(int value) {
        OperationResult result = new OperationResult("Search", "SEARCH_BINARY");

        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Tree is empty");
            return result;
        }

        result.addStep(new OperationStep("Starting search for " + value + " at root", 0, List.of(root.getValue()), "yellow", "SEARCH_BINARY"));

        BinaryTreeNode curr = (BinaryTreeNode) root;
        while (curr != null) {
            result.addStep(new OperationStep("Comparing " + value + " with " + curr.getValue(), 2, List.of(curr.getValue()), "yellow", "SEARCH_BINARY"));
            if (value == curr.getValue()) {
                result.addStep(new OperationStep("Found " + value + "!", 3, List.of(value), "green", "SEARCH_BINARY"));
                return result;
            } else if (value < curr.getValue()) {
                result.addStep(new OperationStep(value + " < " + curr.getValue() + ", going left", 5, List.of(curr.getValue()), "yellow", "SEARCH_BINARY"));
                curr = curr.getLeft();
            } else {
                result.addStep(new OperationStep(value + " > " + curr.getValue() + ", going right", 7, List.of(curr.getValue()), "yellow", "SEARCH_BINARY"));
                curr = curr.getRight();
            }
        }

        result.setSuccess(false);
        result.setErrorMessage("Value " + value + " not found");
        result.addStep(new OperationStep("Value " + value + " not found", 9, Collections.emptyList(), "red", "SEARCH_BINARY"));
        return result;
    }

    protected BinaryTreeNode[] findNodeBST(BinaryTreeNode root, int value, OperationResult result) {
        BinaryTreeNode curr = root;
        BinaryTreeNode parent = null;
        while (curr != null) {
            if (result != null) {
                result.addStep(new OperationStep("Visiting node " + curr.getValue(), 1, List.of(curr.getValue()), "yellow", "DELETE_BINARY"));
            }
            if (value == curr.getValue()) return new BinaryTreeNode[]{curr, parent};
            parent = curr;
            if (value < curr.getValue()) curr = curr.getLeft();
            else curr = curr.getRight();
        }
        return new BinaryTreeNode[]{null, null};
    }

    @Override
    public void calculateLayout() {
        if (root == null) return;
        int[] counter = {0};
        assignInorderPosition((BinaryTreeNode) root, counter, 1);
        normalizePositions((BinaryTreeNode) root, 600, 60, 1);
    }

    private void assignInorderPosition(BinaryTreeNode node, int[] counter, int depth) {
        if (node == null) return;
        assignInorderPosition(node.getLeft(), counter, depth + 1);
        node.setX(counter[0]++);
        node.setY(depth);
        assignInorderPosition(node.getRight(), counter, depth + 1);
    }

    private void normalizePositions(BinaryTreeNode node, int centerX, int topY, int depth) {
        if (root == null) return;
        int minX = findMin((BinaryTreeNode) root);
        int maxX = findMax((BinaryTreeNode) root);
        int range = Math.max(maxX - minX, 1);
        applyPositions((BinaryTreeNode) root, minX, range, centerX, topY);
    }

    private void applyPositions(BinaryTreeNode node, int minX, int range, int centerX, int topY) {
        if (node == null) return;
        int panelWidth = 1100;
        int xPos = (int) ((node.getX() - minX) * (double) panelWidth / Math.max(range, 1)) + 50;
        int yPos = node.getY() * 80 + topY - 80;
        node.setX(xPos);
        node.setY(yPos);
        applyPositions(node.getLeft(), minX, range, centerX, topY);
        applyPositions(node.getRight(), minX, range, centerX, topY);
    }

    private int findMin(BinaryTreeNode node) {
        if (node == null) return Integer.MAX_VALUE;
        return Math.min(node.getX(), Math.min(findMin(node.getLeft()), findMin(node.getRight())));
    }

    private int findMax(BinaryTreeNode node) {
        if (node == null) return Integer.MIN_VALUE;
        return Math.max(node.getX(), Math.max(findMax(node.getLeft()), findMax(node.getRight())));
    }
}
