package treevisualizer.gui;

import treevisualizer.tree.BinaryTree;
import treevisualizer.tree.GenericTree;
import treevisualizer.tree.RedBlackTree;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private AppFrame appFrame;

    public MainMenuPanel(AppFrame appFrame) {
        this.appFrame = appFrame;
        setBackground(new Color(30, 30, 30));
        setLayout(new GridBagLayout());
        buildUI();
    }

    private void buildUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel title = new JLabel("Tree Visualizer", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(new Color(100, 200, 255));
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 20, 30, 20);
        add(title, gbc);

        JLabel subtitle = new JLabel("Select a tree type to visualize:", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(new Color(180, 180, 180));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 20, 20);
        add(subtitle, gbc);

        gbc.insets = new Insets(10, 40, 10, 40);

        JButton genericBtn = createMenuButton("Generic Tree", new Color(70, 130, 180));
        gbc.gridy = 2;
        add(genericBtn, gbc);
        genericBtn.addActionListener(e -> appFrame.showVisualization(new GenericTree(), "Generic Tree"));

        JButton binaryBtn = createMenuButton("Binary Tree (BST)", new Color(60, 160, 80));
        gbc.gridy = 3;
        add(binaryBtn, gbc);
        binaryBtn.addActionListener(e -> appFrame.showVisualization(new BinaryTree(), "Binary Tree"));

        JButton rbBtn = createMenuButton("Red-Black Tree", new Color(180, 50, 50));
        gbc.gridy = 4;
        add(rbBtn, gbc);
        rbBtn.addActionListener(e -> appFrame.showVisualization(new RedBlackTree(), "Red-Black Tree"));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);

        JButton helpBtn = createSmallButton("Help", new Color(100, 100, 150));
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Tree Visualizer\n\nThis application visualizes tree data structures:\n" +
                "• Generic Tree: N-ary tree with any number of children\n" +
                "• Binary Tree: Binary Search Tree (BST)\n" +
                "• Red-Black Tree: Self-balancing BST\n\n" +
                "Use the operations toolbar to insert, delete, search and traverse nodes.\n" +
                "Watch step-by-step animations with the control panel.",
                "Help", JOptionPane.INFORMATION_MESSAGE));
        bottomPanel.add(helpBtn);

        JButton quitBtn = createSmallButton("Quit", new Color(150, 50, 50));
        quitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });
        bottomPanel.add(quitBtn);

        gbc.gridy = 5;
        gbc.insets = new Insets(30, 20, 20, 20);
        add(bottomPanel, gbc);
    }

    private JButton createMenuButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(320, 55));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createSmallButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
