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
}
