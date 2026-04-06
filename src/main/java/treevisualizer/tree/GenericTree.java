package treevisualizer.tree;

import treevisualizer.model.GenericTreeNode;
import treevisualizer.model.TreeNode;
import treevisualizer.operation.OperationResult;
import treevisualizer.operation.OperationStep;

import java.util.*;

public class GenericTree extends AbstractTree {

    public GenericTree() {
        super();
    }

    @Override
    public OperationResult create() {
        OperationResult result = new OperationResult("Create", "CREATE_GENERIC");
        root = null;
        size = 0;
        result.addStep(new OperationStep("Tree created (empty)", 0, Collections.emptyList(), "green", "CREATE_GENERIC"));
        calculateLayout();
        return result;
    }

    @Override
    public OperationResult insert(int... params) {
        OperationResult result = new OperationResult("Insert", "INSERT_GENERIC");
        if (params.length == 1) {
            // Insert as root or use -1 as sentinel for "no parent"
            int value = params[0];
            if (root == null) {
                GenericTreeNode newNode = new GenericTreeNode(value);
                root = newNode;
                size++;
                result.addStep(new OperationStep("Tree is empty. Creating root with value " + value, 0, Collections.emptyList(), "green", "INSERT_GENERIC"));
                result.addStep(new OperationStep("Insertion complete. Root = " + value, 6, List.of(value), "green", "INSERT_GENERIC"));
                calculateLayout();
                return result;
            }
            // default: add as child of root
            params = new int[]{root.getValue(), value};
        }
        if (params.length < 2) {
            result.setSuccess(false);
            result.setErrorMessage("Insert requires parentValue and newValue");
            return result;
        }
        int parentValue = params[0];
        int newValue = params[1];

        result.addStep(new OperationStep("Starting insertion of value " + newValue,
                0, root != null ? List.of(root.getValue()) : Collections.emptyList(), "yellow", "INSERT_GENERIC"));

        GenericTreeNode parent = findNode((GenericTreeNode) root, parentValue);
        if (parent == null) {
            result.setSuccess(false);
            result.setErrorMessage("Parent node " + parentValue + " not found");
            result.addStep(new OperationStep("Parent node " + parentValue + " not found", 3, Collections.emptyList(), "red", "INSERT_GENERIC"));
            return result;
        }

        result.addStep(new OperationStep("Searching for parent node with value " + parentValue,
                1, List.of(parentValue), "yellow", "INSERT_GENERIC"));

        result.addStep(new OperationStep("Found parent node " + parentValue + ", adding child " + newValue,
                2, List.of(parentValue), "green", "INSERT_GENERIC"));

        GenericTreeNode newNode = new GenericTreeNode(newValue);
        parent.addChild(newNode);
        size++;

        result.addStep(new OperationStep("Insertion complete. Node " + newValue + " added as child of " + parentValue,
                6, List.of(newValue), "green", "INSERT_GENERIC"));

        calculateLayout();
        return result;
    }

    @Override
    public OperationResult delete(int value) {
        OperationResult result = new OperationResult("Delete", "DELETE_GENERIC");

        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Tree is empty");
            return result;
        }

        if (root.getValue() == value) {
            result.addStep(new OperationStep("Deleting root node " + value, 0, List.of(value), "red", "DELETE_GENERIC"));
            size = 0;
            root = null;
            result.addStep(new OperationStep("Root deleted. Tree is now empty.", 3, Collections.emptyList(), "green", "DELETE_GENERIC"));
            calculateLayout();
            return result;
        }

        result.addStep(new OperationStep("Searching for node " + value, 0, List.of(root.getValue()), "yellow", "DELETE_GENERIC"));

        boolean deleted = deleteNode((GenericTreeNode) root, value, result);
        if (!deleted) {
            result.setSuccess(false);
            result.setErrorMessage("Node " + value + " not found");
            result.addStep(new OperationStep("Node " + value + " not found", 2, Collections.emptyList(), "red", "DELETE_GENERIC"));
        } else {
            result.addStep(new OperationStep("Deletion complete", 4, Collections.emptyList(), "green", "DELETE_GENERIC"));
            calculateLayout();
        }
        return result;
    }

    private boolean deleteNode(GenericTreeNode current, int value, OperationResult result) {
        for (GenericTreeNode child : new ArrayList<>(current.getChildList())) {
            result.addStep(new OperationStep("Checking node " + child.getValue(), 1, List.of(child.getValue()), "yellow", "DELETE_GENERIC"));
            if (child.getValue() == value) {
                current.removeChild(value);
                size -= countSubtree(child);
                return true;
            }
            if (deleteNode(child, value, result)) return true;
        }
        return false;
    }

    private int countSubtree(GenericTreeNode node) {
        if (node == null) return 0;
        int count = 1;
        for (GenericTreeNode child : node.getChildList()) {
            count += countSubtree(child);
        }
        return count;
    }

    @Override
    public OperationResult update(int oldValue, int newValue) {
        OperationResult result = new OperationResult("Update", "UPDATE_GENERIC");
        result.addStep(new OperationStep("Searching for node " + oldValue, 0, root != null ? List.of(root.getValue()) : Collections.emptyList(), "yellow", "UPDATE_GENERIC"));

        GenericTreeNode node = findNode((GenericTreeNode) root, oldValue);
        if (node == null) {
            result.setSuccess(false);
            result.setErrorMessage("Node " + oldValue + " not found");
            return result;
        }

        result.addStep(new OperationStep("Found node " + oldValue + ", updating to " + newValue, 2, List.of(oldValue), "yellow", "UPDATE_GENERIC"));
        node.setValue(newValue);
        result.addStep(new OperationStep("Update complete: " + oldValue + " → " + newValue, 3, List.of(newValue), "green", "UPDATE_GENERIC"));
        calculateLayout();
        return result;
    }

    @Override
    public OperationResult traverse(String algorithm) {
        OperationResult result = new OperationResult("Traverse (" + algorithm + ")", "TRAVERSE_GENERIC");

        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Tree is empty");
            return result;
        }

        List<Integer> visitOrder = new ArrayList<>();

        if ("BFS".equals(algorithm)) {
            bfsTraverse((GenericTreeNode) root, result, visitOrder);
        } else {
            dfsPreorder((GenericTreeNode) root, result, visitOrder);
        }

        result.addStep(new OperationStep("Traversal complete. Order: " + visitOrder, 6, Collections.emptyList(), "green", "TRAVERSE_GENERIC"));
        return result;
    }

    private void dfsPreorder(GenericTreeNode node, OperationResult result, List<Integer> order) {
        if (node == null) return;
        order.add(node.getValue());
        result.addStep(new OperationStep("Visiting node " + node.getValue(), 2, List.of(node.getValue()), "yellow", "TRAVERSE_GENERIC"));
        for (GenericTreeNode child : node.getChildList()) {
            dfsPreorder(child, result, order);
        }
    }

    private void bfsTraverse(GenericTreeNode root, OperationResult result, List<Integer> order) {
        Queue<GenericTreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            GenericTreeNode node = queue.poll();
            order.add(node.getValue());
            result.addStep(new OperationStep("Visiting node " + node.getValue(), 3, List.of(node.getValue()), "yellow", "TRAVERSE_GENERIC"));
            for (GenericTreeNode child : node.getChildList()) {
                queue.add(child);
            }
        }
    }

    @Override
    public OperationResult search(int value) {
        OperationResult result = new OperationResult("Search", "SEARCH_GENERIC");

        if (root == null) {
            result.setSuccess(false);
            result.setErrorMessage("Tree is empty");
            return result;
        }

        result.addStep(new OperationStep("Starting search for value " + value, 0, List.of(root.getValue()), "yellow", "SEARCH_GENERIC"));

        boolean found = searchNode((GenericTreeNode) root, value, result);
        if (!found) {
            result.setSuccess(false);
            result.setErrorMessage("Value " + value + " not found");
            result.addStep(new OperationStep("Value " + value + " not found in tree", 4, Collections.emptyList(), "red", "SEARCH_GENERIC"));
        }
        return result;
    }

    private boolean searchNode(GenericTreeNode node, int value, OperationResult result) {
        if (node == null) return false;
        result.addStep(new OperationStep("Checking node " + node.getValue(), 1, List.of(node.getValue()), "yellow", "SEARCH_GENERIC"));
        if (node.getValue() == value) {
            result.addStep(new OperationStep("Found value " + value + "!", 3, List.of(value), "green", "SEARCH_GENERIC"));
            return true;
        }
        for (GenericTreeNode child : node.getChildList()) {
            if (searchNode(child, value, result)) return true;
        }
        return false;
    }

    private GenericTreeNode findNode(GenericTreeNode node, int value) {
        if (node == null) return null;
        if (node.getValue() == value) return node;
        for (GenericTreeNode child : node.getChildList()) {
            GenericTreeNode found = findNode(child, value);
            if (found != null) return found;
        }
        return null;
    }

    @Override
    public void calculateLayout() {
        if (root == null) return;
        int startX = 600;
        int startY = 60;
        int levelHeight = 80;
        assignPositions((GenericTreeNode) root, startX, startY, levelHeight, 500);
    }

    private void assignPositions(GenericTreeNode node, int x, int y, int levelHeight, int spread) {
        node.setX(x);
        node.setY(y);
        List<GenericTreeNode> children = node.getChildList();
        if (children.isEmpty()) return;
        int n = children.size();
        int childSpread = Math.max(80, spread / Math.max(n, 1));
        int totalWidth = (n - 1) * childSpread;
        int startX = x - totalWidth / 2;
        for (int i = 0; i < n; i++) {
            assignPositions(children.get(i), startX + i * childSpread, y + levelHeight, levelHeight, childSpread);
        }
    }
}
