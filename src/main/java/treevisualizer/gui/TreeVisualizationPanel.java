package treevisualizer.gui;

import treevisualizer.model.BinaryTreeNode;
import treevisualizer.model.NodeColor;
import treevisualizer.model.RedBlackTreeNode;
import treevisualizer.model.TreeNode;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TreeVisualizationPanel extends JPanel {
    private TreeNode root;
    private Set<Integer> highlightedValues = new HashSet<>();
    private String highlightColor = "yellow";
    private static final int RADIUS = 22;
    private String message = "Tree is empty. Use 'Create' to start.";

    public TreeVisualizationPanel() {
        setBackground(new Color(20, 20, 30));
        setPreferredSize(new Dimension(900, 600));
    }

    public void setTree(TreeNode root) {
        this.root = root;
        if (root == null) {
            message = "Tree is empty.";
        } else {
            message = null;
        }
        repaint();
    }

    public void setHighlight(List<Integer> values, String color) {
        highlightedValues = new HashSet<>(values);
        highlightColor = color != null ? color : "yellow";
        repaint();
    }

    public void clearHighlights() {
        highlightedValues.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (root == null) {
            g2.setColor(new Color(100, 100, 120));
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            FontMetrics fm = g2.getFontMetrics();
            String msg = message != null ? message : "Tree is empty.";
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            g2.drawString(msg, x, getHeight() / 2);
            return;
        }

        // Draw edges first
        drawEdges(g2, root);
        // Then draw nodes
        drawNodes(g2, root);
    }

    private void drawEdges(Graphics2D g2, TreeNode node) {
        if (node == null) return;
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(new Color(100, 100, 130));
        for (TreeNode child : node.getChildren()) {
            if (child != null) {
                g2.drawLine(node.getX(), node.getY(), child.getX(), child.getY());
                drawEdges(g2, child);
            }
        }
    }

    private void drawNodes(Graphics2D g2, TreeNode node) {
        if (node == null) return;

        boolean isHighlighted = highlightedValues.contains(node.getValue());
        Color fillColor = getNodeColor(node, isHighlighted);
        Color borderColor = fillColor.brighter();

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillOval(node.getX() - RADIUS + 2, node.getY() - RADIUS + 2, RADIUS * 2, RADIUS * 2);

        // Draw node circle
        g2.setColor(fillColor);
        g2.fillOval(node.getX() - RADIUS, node.getY() - RADIUS, RADIUS * 2, RADIUS * 2);

        // Draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(isHighlighted ? 3f : 1.5f));
        g2.drawOval(node.getX() - RADIUS, node.getY() - RADIUS, RADIUS * 2, RADIUS * 2);

        // Draw value text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        String valStr = String.valueOf(node.getValue());
        int textX = node.getX() - fm.stringWidth(valStr) / 2;
        int textY = node.getY() + fm.getAscent() / 2 - 1;
        g2.drawString(valStr, textX, textY);

        // Draw RB color indicator
        if (node instanceof RedBlackTreeNode rbNode) {
            String colorMark = rbNode.getColor() == NodeColor.RED ? "R" : "B";
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(rbNode.getColor() == NodeColor.RED ? new Color(255, 180, 180) : new Color(180, 180, 255));
            g2.drawString(colorMark, node.getX() + RADIUS - 10, node.getY() - RADIUS + 10);
        }

        // Draw children
        for (TreeNode child : node.getChildren()) {
            drawNodes(g2, child);
        }
    }

    private Color getNodeColor(TreeNode node, boolean isHighlighted) {
        if (node instanceof RedBlackTreeNode rbNode) {
            if (isHighlighted) {
                return getHighlightColor();
            }
            return rbNode.getColor() == NodeColor.RED ? new Color(200, 50, 50) : new Color(40, 40, 60);
        }

        if (isHighlighted) {
            return getHighlightColor();
        }
        return new Color(70, 130, 180);
    }

    private Color getHighlightColor() {
        return switch (highlightColor) {
            case "green" -> new Color(50, 205, 50);
            case "red" -> new Color(220, 50, 50);
            case "blue" -> new Color(50, 100, 220);
            default -> new Color(255, 220, 0);
        };
    }
}
