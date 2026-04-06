package treevisualizer.tree;

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

    public abstract void calculateLayout();
}
