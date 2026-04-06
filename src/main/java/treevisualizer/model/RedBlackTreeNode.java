package treevisualizer.model;

public class RedBlackTreeNode extends BinaryTreeNode {
    private NodeColor color;

    public RedBlackTreeNode(int value) {
        super(value);
        this.color = NodeColor.RED;
    }

    public NodeColor getColor() { return color; }
    public void setColor(NodeColor color) { this.color = color; }

    @Override
    public RedBlackTreeNode getLeft() { return (RedBlackTreeNode) left; }
    @Override
    public RedBlackTreeNode getRight() { return (RedBlackTreeNode) right; }
    @Override
    public RedBlackTreeNode getParent() { return (RedBlackTreeNode) parent; }

    @Override
    public RedBlackTreeNode deepCopy() {
        RedBlackTreeNode copy = new RedBlackTreeNode(value);
        copy.setColor(color);
        copy.setX(x);
        copy.setY(y);
        if (left != null) {
            RedBlackTreeNode leftCopy = ((RedBlackTreeNode) left).deepCopy();
            leftCopy.setParent(copy);
            copy.setLeft(leftCopy);
        }
        if (right != null) {
            RedBlackTreeNode rightCopy = ((RedBlackTreeNode) right).deepCopy();
            rightCopy.setParent(copy);
            copy.setRight(rightCopy);
        }
        return copy;
    }
}
