package com.demo3.view;

import com.demo3.tree.OperationStep;
import com.demo3.tree.TreeSnapshot;
import com.demo3.tree.TreeType;
import com.demo3.tree.TraversalType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TreeVisualizerView {
    private final StackPane root = new StackPane();
    private final VBox mainMenuView = new VBox(18);
    private final BorderPane visualizerView = new BorderPane();
    private final VBox codePanel = new VBox(8);
    private final TreeCanvasView treeCanvas = new TreeCanvasView();
    private final Map<TreeType, Button> openTreeButtons = new EnumMap<>(TreeType.class);

    private final Button helpMenuButton = new Button("Help");
    private final Button quitButton = new Button("Quit");
    private final Button backToMenuButton = new Button("Back to Menu");
    private final Button helpVisualizerButton = new Button("Help");
    private final Button createButton = new Button("Create Empty Tree");
    private final Button insertButton = new Button("Insert");
    private final Button deleteButton = new Button("Delete");
    private final Button updateButton = new Button("Update");
    private final Button searchButton = new Button("Search");
    private final Button traverseButton = new Button("Traverse");
    private final Button undoButton = new Button("Undo");
    private final Button redoButton = new Button("Redo");
    private final Button playPauseButton = new Button("Play");
    private final Button stepBackButton = new Button("Step Back");
    private final Button stepForwardButton = new Button("Step Forward");

    private final ComboBox<TreeType> treeSelector = new ComboBox<>();
    private final ComboBox<TraversalType> traversalSelector = new ComboBox<>();
    private final TextField parentField = new TextField();
    private final TextField valueField = new TextField();
    private final TextField newValueField = new TextField();
    private final Slider speedSlider = new Slider(0.4, 2.2, 1.0);
    private final ProgressBar progressBar = new ProgressBar(0);

    private final Label statusLabel = new Label("Choose a tree type to begin.");
    private final Label stepLabel = new Label("No operation selected.");
    private final Label traceCounterLabel = new Label("Step 0 / 0");
    private final Label validationLabel = new Label("Ready.");
    private final Label legendLabel = new Label("Legend: orange border = highlighted node, red/black fill = Red-Black color.");

    public TreeVisualizerView() {
        buildMainMenu();
        buildVisualizer();
        root.getChildren().addAll(mainMenuView, visualizerView);
        showMainMenu();
    }

    private void buildMainMenu() {
        Label title = new Label("Tree Operation Visualizer");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 30));

        Label subtitle = new Label(
                "Visualize Generic Tree, Binary Search Tree, and Red-Black Tree operations\n" +
                "with step-by-step pseudocode, playback, and state history."
        );
        subtitle.setStyle("-fx-font-size: 15px; -fx-text-fill: #36506c;");

        Label checklist = new Label(
                "This build already covers: menu selection, tree panel, code panel, playback,\n" +
                "undo/redo, create/insert/delete/update/search/traverse, and manual tree models."
        );
        checklist.setStyle("-fx-font-size: 13px; -fx-text-fill: #53718b;");

        mainMenuView.setAlignment(Pos.CENTER);
        mainMenuView.setPadding(new Insets(40));
        mainMenuView.setStyle("-fx-background-color: linear-gradient(to bottom, #f6fbff, #dfefff);");
        mainMenuView.getChildren().addAll(title, subtitle, checklist);

        for (TreeType type : TreeType.values()) {
            Button button = new Button("Open " + type.getDisplayName());
            button.setPrefWidth(340);
            button.setPrefHeight(50);
            button.setStyle("-fx-font-size: 15px; -fx-background-color: #0f5ea8; -fx-text-fill: white;");
            openTreeButtons.put(type, button);
            mainMenuView.getChildren().add(button);
        }

        helpMenuButton.setPrefWidth(160);
        quitButton.setPrefWidth(160);
        HBox footer = new HBox(10, helpMenuButton, quitButton);
        footer.setAlignment(Pos.CENTER);
        mainMenuView.getChildren().add(footer);
    }

    private void buildVisualizer() {
        treeSelector.getItems().setAll(TreeType.values());
        traversalSelector.getItems().setAll(TraversalType.values());
        traversalSelector.setValue(TraversalType.DFS);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.6);
        speedSlider.setMinorTickCount(1);

        parentField.setPromptText("Parent value");
        valueField.setPromptText("Value");
        newValueField.setPromptText("New value");
        validationLabel.setWrapText(true);
        validationLabel.setStyle("-fx-text-fill: #47617a; -fx-font-size: 12px;");
        legendLabel.setWrapText(true);
        legendLabel.setStyle("-fx-text-fill: #47617a; -fx-font-size: 12px;");
        traceCounterLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #47617a;");

        createButton.setMaxWidth(Double.MAX_VALUE);
        insertButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        updateButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setMaxWidth(Double.MAX_VALUE);
        traverseButton.setMaxWidth(Double.MAX_VALUE);
        undoButton.setMaxWidth(Double.MAX_VALUE);
        redoButton.setMaxWidth(Double.MAX_VALUE);

        VBox leftPanel = buildControlPanel();
        VBox rightPanel = buildCodePanel();

        BorderPane treePaneHolder = new BorderPane();
        treePaneHolder.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        treePaneHolder.setPadding(new Insets(12));
        treePaneHolder.setCenter(treeCanvas);

        Label title = new Label("Visualizer");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(12, backToMenuButton, title, spacer, new Label("Tree Type:"), treeSelector, helpVisualizerButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 12, 0));

        VBox statusBox = new VBox(6, statusLabel, stepLabel, traceCounterLabel, legendLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        VBox speedBox = new VBox(4, new Label("Animation speed"), speedSlider);
        speedBox.setPrefWidth(180);
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
        progressBar.setPrefWidth(260);
        HBox bottomBar = new HBox(12, statusBox, bottomSpacer, speedBox, stepBackButton, playPauseButton, stepForwardButton, progressBar);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(12, 0, 0, 0));

        visualizerView.setTop(topBar);
        visualizerView.setLeft(leftPanel);
        visualizerView.setCenter(treePaneHolder);
        visualizerView.setRight(rightPanel);
        visualizerView.setBottom(bottomBar);
        visualizerView.setPadding(new Insets(14));
        visualizerView.setStyle("-fx-background-color: #eef5fb;");
    }

    private VBox buildControlPanel() {
        Label heading = new Label("Operations");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        VBox panel = new VBox(10,
                heading,
                createButton,
                new Label("Parent value is used only for Generic Tree."),
                parentField,
                valueField,
                newValueField,
                new Label("Traversal"),
                traversalSelector,
                new Label("Validation / guidance"),
                validationLabel,
                insertButton,
                deleteButton,
                updateButton,
                searchButton,
                traverseButton,
                new Separator(),
                undoButton,
                redoButton
        );
        panel.setPrefWidth(270);
        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        return panel;
    }

    private VBox buildCodePanel() {
        Label title = new Label("Code / Pseudocode");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        codePanel.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(codePanel);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: white;");

        Label note = new Label("The highlighted line shows the current step in the operation trace.");
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #47617a; -fx-font-size: 12px;");

        VBox panel = new VBox(12, title, note, scrollPane);
        panel.setPrefWidth(360);
        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return panel;
    }

    public StackPane getRoot() {
        return root;
    }

    public void showMainMenu() {
        mainMenuView.setVisible(true);
        mainMenuView.setManaged(true);
        visualizerView.setVisible(false);
        visualizerView.setManaged(false);
    }

    public void showVisualizer() {
        mainMenuView.setVisible(false);
        mainMenuView.setManaged(false);
        visualizerView.setVisible(true);
        visualizerView.setManaged(true);
    }

    public void renderStep(OperationStep step, int index, int total) {
        statusLabel.setText(step.title());
        stepLabel.setText(step.message());
        traceCounterLabel.setText("Step " + (index + 1) + " / " + total);
        progressBar.setProgress(total == 1 ? 1 : (double) index / (total - 1));
        renderPseudoCode(step.pseudocode(), step.activeLine());
        treeCanvas.render(step.snapshot());
        stepBackButton.setDisable(index == 0);
        stepForwardButton.setDisable(index >= total - 1);
        playPauseButton.setDisable(total <= 1);
    }

    public void renderSnapshot(TreeSnapshot snapshot) {
        treeCanvas.render(snapshot);
    }

    public void renderPseudoCode(List<String> lines, int activeLine) {
        codePanel.getChildren().clear();
        for (int i = 0; i < lines.size(); i++) {
            Label line = new Label(String.format("%2d. %s", i + 1, lines.get(i)));
            line.setWrapText(true);
            line.setStyle(i == activeLine
                    ? "-fx-background-color: #d7ecff; -fx-padding: 6; -fx-background-radius: 6; -fx-font-weight: bold;"
                    : "-fx-padding: 6;");
            codePanel.getChildren().add(line);
        }
    }

    public void setValidationMessage(String message) {
        validationLabel.setText(message);
    }

    public void setTreeType(TreeType treeType) {
        treeSelector.setValue(treeType);
    }

    public void refreshInputHints(TreeType treeType, boolean emptyTree) {
        boolean generic = treeType == TreeType.GENERIC;
        parentField.setDisable(!generic);
        parentField.setOpacity(generic ? 1 : 0.65);
        if (!generic) {
            parentField.setPromptText("Parent ignored");
            validationLabel.setText("Parent value is ignored for " + treeType.getDisplayName() + ".");
            return;
        }
        parentField.setPromptText(emptyTree ? "Leave blank for root creation" : "Parent node value");
        validationLabel.setText(emptyTree
                ? "Generic Tree is empty: leave Parent blank and insert the first root value."
                : "Generic Tree rule: Parent is required once the tree already has a root.");
    }

    public Map<TreeType, Button> getOpenTreeButtons() {
        return openTreeButtons;
    }

    public Button getHelpMenuButton() {
        return helpMenuButton;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    public Button getBackToMenuButton() {
        return backToMenuButton;
    }

    public Button getHelpVisualizerButton() {
        return helpVisualizerButton;
    }

    public Button getCreateButton() {
        return createButton;
    }

    public Button getInsertButton() {
        return insertButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public Button getTraverseButton() {
        return traverseButton;
    }

    public Button getUndoButton() {
        return undoButton;
    }

    public Button getRedoButton() {
        return redoButton;
    }

    public Button getPlayPauseButton() {
        return playPauseButton;
    }

    public Button getStepBackButton() {
        return stepBackButton;
    }

    public Button getStepForwardButton() {
        return stepForwardButton;
    }

    public ComboBox<TreeType> getTreeSelector() {
        return treeSelector;
    }

    public ComboBox<TraversalType> getTraversalSelector() {
        return traversalSelector;
    }

    public TextField getParentField() {
        return parentField;
    }

    public TextField getValueField() {
        return valueField;
    }

    public TextField getNewValueField() {
        return newValueField;
    }

    public Slider getSpeedSlider() {
        return speedSlider;
    }
}
