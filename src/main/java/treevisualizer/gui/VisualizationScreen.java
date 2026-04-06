package treevisualizer.gui;

import treevisualizer.model.TreeNode;
import treevisualizer.operation.OperationResult;
import treevisualizer.operation.OperationStep;
import treevisualizer.tree.AbstractTree;
import treevisualizer.tree.Tree;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class VisualizationScreen extends JPanel {
    private AppFrame appFrame;
    private Tree tree;
    private String treeType;

    private TreeVisualizationPanel vizPanel;
    private CodePanel codePanel;
    private ControlBarPanel controlBarPanel;

    private OperationResult currentResult;
    private int currentStepIndex = -1;

    private final Deque<Object[]> undoStack = new ArrayDeque<>();
    private final Deque<Object[]> redoStack = new ArrayDeque<>();
    private static final int MAX_UNDO = 30;

    private JLabel operationLabel;

    public VisualizationScreen(AppFrame appFrame, Tree tree, String treeType) {
        this.appFrame = appFrame;
        this.tree = tree;
        this.treeType = treeType;

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 40));

        buildToolbar();
        buildCenter();
        buildControlBar();
    }

    private void buildToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        toolbar.setBackground(new Color(45, 45, 55));
        toolbar.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel typeLabel = new JLabel(treeType + "  |  ");
        typeLabel.setForeground(new Color(150, 200, 255));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        toolbar.add(typeLabel);

        String[] ops = {"Create", "Insert", "Delete", "Update", "Traverse", "Search"};
        Color[] colors = {
            new Color(60, 120, 60), new Color(50, 100, 170), new Color(160, 50, 50),
            new Color(140, 100, 30), new Color(80, 60, 160), new Color(40, 120, 120)
        };

        for (int i = 0; i < ops.length; i++) {
            final String op = ops[i];
            JButton btn = createToolbarButton(op, colors[i]);
            btn.addActionListener(e -> handleOperation(op));
            toolbar.add(btn);
        }

        toolbar.add(Box.createHorizontalStrut(20));

        operationLabel = new JLabel("");
        operationLabel.setForeground(new Color(200, 200, 200));
        operationLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        toolbar.add(operationLabel);

        JButton backBtn = createToolbarButton("← Back", new Color(80, 80, 80));
        backBtn.addActionListener(e -> {
            controlBarPanel.stopAnimation();
            appFrame.showMainMenu();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(backBtn);

        JPanel topWrapper = new JPanel(new BorderLayout());
        topWrapper.setBackground(new Color(45, 45, 55));
        topWrapper.add(toolbar, BorderLayout.WEST);
        topWrapper.add(rightPanel, BorderLayout.EAST);

        add(topWrapper, BorderLayout.NORTH);
    }

    private void buildCenter() {
        vizPanel = new TreeVisualizationPanel();
        codePanel = new CodePanel();
        codePanel.setPreferredSize(new Dimension(280, 400));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, vizPanel, codePanel);
        splitPane.setDividerLocation(900);
        splitPane.setResizeWeight(1.0);
        splitPane.setBackground(new Color(30, 30, 40));
        splitPane.setBorder(null);
        splitPane.setDividerSize(4);

        add(splitPane, BorderLayout.CENTER);
    }

    private void buildControlBar() {
        controlBarPanel = new ControlBarPanel(this);
        add(controlBarPanel, BorderLayout.SOUTH);
    }

    private JButton createToolbarButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(85, 30));
        return btn;
    }

    private void handleOperation(String op) {
        controlBarPanel.stopAnimation();

        // Snapshot state before mutating operations
        boolean mutates = !op.equals("Traverse") && !op.equals("Search");
        Object[] snapshot = mutates ? ((AbstractTree) tree).snapshot() : null;

        OperationResult result = null;
        switch (op) {
            case "Create" -> result = tree.create();
            case "Insert" -> result = handleInsert();
            case "Delete" -> result = handleDelete();
            case "Update" -> result = handleUpdate();
            case "Traverse" -> result = handleTraverse();
            case "Search" -> result = handleSearch();
        }

        if (result == null) return;

        if (!result.isSuccess() && result.getSteps().isEmpty()) {
            JOptionPane.showMessageDialog(this, result.getErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (snapshot != null && result.isSuccess()) {
            if (undoStack.size() >= MAX_UNDO) undoStack.pollFirst();
            undoStack.push(snapshot);
            redoStack.clear();
        }

        executeOperation(result);
    }

    private OperationResult handleInsert() {
        if ("Generic Tree".equals(treeType)) {
            if (tree.getRoot() == null) {
                String val = JOptionPane.showInputDialog(this, "Enter root value:", "Insert Root", JOptionPane.PLAIN_MESSAGE);
                if (val == null || val.isBlank()) return null;
                try { return tree.insert(Integer.parseInt(val.trim())); }
                catch (NumberFormatException e) { showError("Invalid number"); return null; }
            } else {
                JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
                panel.add(new JLabel("Parent value:"));
                JTextField parentField = new JTextField();
                panel.add(parentField);
                panel.add(new JLabel("New value:"));
                JTextField valueField = new JTextField();
                panel.add(valueField);
                int res = JOptionPane.showConfirmDialog(this, panel, "Insert Node", JOptionPane.OK_CANCEL_OPTION);
                if (res != JOptionPane.OK_OPTION) return null;
                try {
                    int parent = Integer.parseInt(parentField.getText().trim());
                    int value = Integer.parseInt(valueField.getText().trim());
                    return tree.insert(parent, value);
                } catch (NumberFormatException e) { showError("Invalid number"); return null; }
            }
        } else {
            String val = JOptionPane.showInputDialog(this, "Enter value to insert:", "Insert", JOptionPane.PLAIN_MESSAGE);
            if (val == null || val.isBlank()) return null;
            try { return tree.insert(Integer.parseInt(val.trim())); }
            catch (NumberFormatException e) { showError("Invalid number"); return null; }
        }
    }

    private OperationResult handleDelete() {
        String val = JOptionPane.showInputDialog(this, "Enter value to delete:", "Delete", JOptionPane.PLAIN_MESSAGE);
        if (val == null || val.isBlank()) return null;
        try { return tree.delete(Integer.parseInt(val.trim())); }
        catch (NumberFormatException e) { showError("Invalid number"); return null; }
    }

    private OperationResult handleUpdate() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Old value:"));
        JTextField oldField = new JTextField();
        panel.add(oldField);
        panel.add(new JLabel("New value:"));
        JTextField newField = new JTextField();
        panel.add(newField);
        int res = JOptionPane.showConfirmDialog(this, panel, "Update Node", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return null;
        try {
            int oldVal = Integer.parseInt(oldField.getText().trim());
            int newVal = Integer.parseInt(newField.getText().trim());
            return tree.update(oldVal, newVal);
        } catch (NumberFormatException e) { showError("Invalid number"); return null; }
    }

    private OperationResult handleTraverse() {
        String[] options = {"DFS_PREORDER", "DFS_INORDER", "DFS_POSTORDER", "BFS"};
        String choice = (String) JOptionPane.showInputDialog(this, "Select traversal algorithm:",
                "Traverse", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return null;
        return tree.traverse(choice);
    }

    private OperationResult handleSearch() {
        String val = JOptionPane.showInputDialog(this, "Enter value to search:", "Search", JOptionPane.PLAIN_MESSAGE);
        if (val == null || val.isBlank()) return null;
        try { return tree.search(Integer.parseInt(val.trim())); }
        catch (NumberFormatException e) { showError("Invalid number"); return null; }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void executeOperation(OperationResult result) {
        currentResult = result;
        currentStepIndex = 0;
        operationLabel.setText("Operation: " + result.getOperationName());
        vizPanel.setTree(tree.getRoot());
        vizPanel.clearHighlights();
        controlBarPanel.updateProgress(0, result.getSteps().size());
        applyStep(0);
    }

    private void applyStep(int index) {
        if (currentResult == null || index < 0 || index >= currentResult.getSteps().size()) return;

        OperationStep step = currentResult.getSteps().get(index);
        controlBarPanel.updateProgress(index + 1, currentResult.getSteps().size());
        controlBarPanel.setStatus(step.getDescription());

        vizPanel.setTree(tree.getRoot());
        vizPanel.setHighlight(step.getHighlightedNodeValues(), step.getHighlightColor());

        String codeKey = step.getPseudocodeKey();
        if (codeKey == null) codeKey = currentResult.getPseudocodeKey();
        codePanel.setOperation(codeKey, step.getHighlightedCodeLine());
    }

    public boolean stepForward() {
        if (currentResult == null) return false;
        if (currentStepIndex < currentResult.getSteps().size() - 1) {
            currentStepIndex++;
            applyStep(currentStepIndex);
            return currentStepIndex < currentResult.getSteps().size() - 1;
        }
        return false;
    }

    public void stepBackward() {
        if (currentResult == null) return;
        if (currentStepIndex > 0) {
            currentStepIndex--;
            applyStep(currentStepIndex);
        }
    }

    public void resetAnimation() {
        if (currentResult == null) return;
        currentStepIndex = 0;
        applyStep(0);
    }

    public void goToEnd() {
        if (currentResult == null) return;
        currentStepIndex = currentResult.getSteps().size() - 1;
        applyStep(currentStepIndex);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(((AbstractTree) tree).snapshot());
            ((AbstractTree) tree).restore(undoStack.pop());
            currentResult = null;
            vizPanel.setTree(tree.getRoot());
            vizPanel.clearHighlights();
            controlBarPanel.updateProgress(0, 0);
            controlBarPanel.setStatus("Undo");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(((AbstractTree) tree).snapshot());
            ((AbstractTree) tree).restore(redoStack.pop());
            currentResult = null;
            vizPanel.setTree(tree.getRoot());
            vizPanel.clearHighlights();
            controlBarPanel.updateProgress(0, 0);
            controlBarPanel.setStatus("Redo");
        }
    }
}
