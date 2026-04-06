package treevisualizer.model;

import java.util.ArrayList;
import java.util.List;

public class GenericTreeNode extends TreeNode {
    private List<GenericTreeNode> children;

    public GenericTreeNode(int value) {
        super(value);
        this.children = new ArrayList<>();
    }

    @Override
    public TreeNode[] getChildren() {
        return children.toArray(new TreeNode[0]);
    }

    public List<GenericTreeNode> getChildList() {
        return children;
    }

    public void addChild(GenericTreeNode child) {
        children.add(child);
    }

    public void removeChild(int value) {
        children.removeIf(c -> c.getValue() == value);
    }
}
