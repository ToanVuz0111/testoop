package com.demo3.tree;

import java.util.List;

public record SnapshotNode(String label, String color, boolean highlighted, List<SnapshotNode> children) {
}
