package treevisualizer.model;

public class BinaryTreeNode extends TreeNode {
    protected BinaryTreeNode left, right, parent;

    public BinaryTreeNode(int value) {
        super(value);
    }

    @Override
    public TreeNode[] getChildren() {
        if (left != null && right != null) return new TreeNode[]{left, right};
        if (left != null) return new TreeNode[]{left};
        if (right != null) return new TreeNode[]{right};
        return new TreeNode[0];
    }

    public BinaryTreeNode getLeft() { return left; }
    public void setLeft(BinaryTreeNode left) { this.left = left; }
    public BinaryTreeNode getRight() { return right; }
    public void setRight(BinaryTreeNode right) { this.right = right; }
    public BinaryTreeNode getParent() { return parent; }
    public void setParent(BinaryTreeNode parent) { this.parent = parent; }

    public BinaryTreeNode deepCopy() {
        BinaryTreeNode copy = new BinaryTreeNode(value);
        copy.setX(x);
        copy.setY(y);
        if (left != null) {
            BinaryTreeNode leftCopy = left.deepCopy();
            leftCopy.setParent(copy);
            copy.setLeft(leftCopy);
        }
        if (right != null) {
            BinaryTreeNode rightCopy = right.deepCopy();
            rightCopy.setParent(copy);
            copy.setRight(rightCopy);
        }
        return copy;
    }
}
