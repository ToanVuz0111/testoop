package com.demo3.view;

import com.demo3.tree.SnapshotNode;
import com.demo3.tree.TreeSnapshot;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.Map;

public class TreeCanvasView extends Region {
    private final Canvas canvas = new Canvas(760, 640);

    public TreeCanvasView() {
        getChildren().add(canvas);
        setMinSize(420, 340);
    }

    public void render(TreeSnapshot snapshot) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.web("#e3edf7"));
        gc.setLineWidth(1);
        for (int i = 1; i < 5; i++) {
            double y = (canvas.getHeight() / 5) * i;
            gc.strokeLine(20, y, canvas.getWidth() - 20, y);
        }

        if (snapshot.root() == null) {
            gc.setFill(Color.web("#6c8197"));
            gc.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 20));
            gc.fillText("Tree is empty", canvas.getWidth() / 2 - 55, canvas.getHeight() / 2);
            return;
        }

        Map<SnapshotNode, Point> positions = new HashMap<>();
        layout(snapshot.root(), 35, canvas.getWidth() - 35, 0, positions);
        drawEdges(gc, snapshot.root(), positions);
        drawNodes(gc, snapshot.root(), positions);
    }

    private void drawEdges(GraphicsContext gc, SnapshotNode node, Map<SnapshotNode, Point> positions) {
        Point point = positions.get(node);
        for (SnapshotNode child : node.children()) {
            Point childPoint = positions.get(child);
            gc.setStroke(Color.web("#8aa4bc"));
            gc.setLineWidth(2);
            gc.strokeLine(point.x, point.y, childPoint.x, childPoint.y);
            drawEdges(gc, child, positions);
        }
    }

    private void drawNodes(GraphicsContext gc, SnapshotNode node, Map<SnapshotNode, Point> positions) {
        Point point = positions.get(node);
        double radius = Math.max(22, 14 + node.label().length() * 3.5);
        gc.setFill(Color.web(node.color()));
        gc.fillOval(point.x - radius, point.y - radius, radius * 2, radius * 2);
        gc.setStroke(node.highlighted() ? Color.web("#ff8a00") : Color.web("#1d3557"));
        gc.setLineWidth(node.highlighted() ? 4 : 2);
        gc.strokeOval(point.x - radius, point.y - radius, radius * 2, radius * 2);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        double textOffset = Math.max(4, node.label().length() * 3.7);
        gc.fillText(node.label(), point.x - textOffset, point.y + 4);
        for (SnapshotNode child : node.children()) {
            drawNodes(gc, child, positions);
        }
    }

    private void layout(SnapshotNode node, double left, double right, int depth, Map<SnapshotNode, Point> positions) {
        double y = 70 + depth * 96;
        double x = (left + right) / 2;
        positions.put(node, new Point(x, y));
        if (node.children().isEmpty()) {
            return;
        }

        int totalLeaves = countLeaves(node);
        double cursor = left;
        for (SnapshotNode child : node.children()) {
            int childLeaves = countLeaves(child);
            double width = (right - left) * childLeaves / Math.max(1, totalLeaves);
            layout(child, cursor, cursor + width, depth + 1, positions);
            cursor += width;
        }
    }

    private int countLeaves(SnapshotNode node) {
        if (node.children().isEmpty()) {
            return 1;
        }
        int sum = 0;
        for (SnapshotNode child : node.children()) {
            sum += countLeaves(child);
        }
        return sum;
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
    }

    private record Point(double x, double y) {
    }
}
