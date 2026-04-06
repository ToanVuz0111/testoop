package treevisualizer.tree;

import treevisualizer.model.TreeNode;
import treevisualizer.operation.OperationResult;

public interface Tree {
    OperationResult create();
    OperationResult insert(int... params);
    OperationResult delete(int value);
    OperationResult update(int oldValue, int newValue);
    OperationResult traverse(String algorithm);
    OperationResult search(int value);
    TreeNode getRoot();
    int getSize();
    int getHeight();
}
