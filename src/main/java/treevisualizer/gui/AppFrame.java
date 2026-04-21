package treevisualizer.gui;

import treevisualizer.tree.Tree;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private MainMenuPanel mainMenuPanel;
    private VisualizationScreen visualizationScreen;

    public AppFrame() {
        setTitle("Tree Visualizer");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        mainMenuPanel = new MainMenuPanel(this);
        contentPanel.add(mainMenuPanel, "MENU");

        setContentPane(contentPanel);
        cardLayout.show(contentPanel, "MENU");
    }

    public void showMainMenu() {
        if (visualizationScreen != null) {
            contentPanel.remove(visualizationScreen);
        }
        cardLayout.show(contentPanel, "MENU");
    }

    public void showVisualization(Tree tree, String treeType) {
        visualizationScreen = new VisualizationScreen(this, tree, treeType);
        contentPanel.add(visualizationScreen, "VISUALIZATION");
        cardLayout.show(contentPanel, "VISUALIZATION");
    }
}
