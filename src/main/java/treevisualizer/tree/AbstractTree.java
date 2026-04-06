package treevisualizer.tree;

import treevisualizer.model.BinaryTreeNode;
import treevisualizer.model.GenericTreeNode;
import treevisualizer.model.RedBlackTreeNode;
import treevisualizer.model.TreeNode;

public abstract class AbstractTree implements Tree {
    protected TreeNode root;
    protected int size;

    public AbstractTree() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public TreeNode getRoot() { return root; }

    @Override
    public int getSize() { return size; }

    @Override
    public int getHeight() {
        return calculateHeight(root);
    }

    protected int calculateHeight(TreeNode node) {
        if (node == null) return 0;
        int maxChildHeight = 0;
        for (TreeNode child : node.getChildren()) {
            int h = calculateHeight(child);
            if (h > maxChildHeight) maxChildHeight = h;
        }
        return 1 + maxChildHeight;
    }

    /** Returns a deep copy of root (and saved size) for undo snapshots. */
    public Object[] snapshot() {
        if (root == null) return new Object[]{null, 0};
        if (root instanceof RedBlackTreeNode rbn) return new Object[]{rbn.deepCopy(), size};
        if (root instanceof BinaryTreeNode bn) return new Object[]{bn.deepCopy(), size};
        if (root instanceof GenericTreeNode gn) return new Object[]{gn.deepCopy(), size};
        return new Object[]{null, 0};
    }

    /** Restores tree state from a previously taken snapshot. */
    public void restore(Object[] snapshot) {
        root = (TreeNode) snapshot[0];
        size = (int) snapshot[1];
        calculateLayout();
    }

    public abstract void calculateLayout();
}
