package com.demo3.tree;

public interface TreeModel {
    TreeType type();

    TreeModel copy();

    TreeSnapshot snapshot();

    OperationTrace create();

    OperationTrace insert(Integer parentValue, int newValue);

    OperationTrace delete(int value);

    OperationTrace update(int currentValue, int newValue);

    OperationTrace search(int value);

    OperationTrace traverse(TraversalType traversalType);
}
