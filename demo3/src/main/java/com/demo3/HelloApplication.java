package com.demo3;

import com.demo3.tree.BinarySearchTreeModel;
import com.demo3.tree.GenericTreeModel;
import com.demo3.tree.OperationStep;
import com.demo3.tree.OperationTrace;
import com.demo3.tree.RedBlackTreeModel;
import com.demo3.tree.SnapshotNode;
import com.demo3.tree.TraversalType;
import com.demo3.tree.TreeModel;
import com.demo3.tree.TreeSnapshot;
import com.demo3.tree.TreeType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HelloApplication extends Application {
    private final StackPane root = new StackPane();
    private final BorderPane visualizerView = new BorderPane();
    private final VBox codePanel = new VBox(8);
    private final TreeCanvas treeCanvas = new TreeCanvas();
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
    private final Button playPauseButton = new Button("Play");
    private final Button stepBackButton = new Button("Step Back");
    private final Button stepForwardButton = new Button("Step Forward");
    private final Button undoButton = new Button("Undo");
    private final Button redoButton = new Button("Redo");

    private TreeModel currentModel = new GenericTreeModel();
    private TreeType currentTreeType = TreeType.GENERIC;
    private OperationTrace currentTrace = OperationTrace.single(
            "Welcome",
            "Create or select a tree type to begin visualizing operations.",
            List.of("Choose a tree type", "Run an operation", "Step through the result"),
            0,
            new GenericTreeModel().snapshot()
    );
    private int currentStepIndex;
    private Timeline playback;
    private final Deque<TreeModel> undoStack = new ArrayDeque<>();
    private final Deque<TreeModel> redoStack = new ArrayDeque<>();

    @Override
    public void start(Stage stage) {
        VBox mainMenu = buildMainMenu(stage);
        root.getChildren().addAll(mainMenu, visualizerView);
        showMainMenu(mainMenu);
        configureVisualizer(mainMenu);
        refreshInputHints();
        renderCurrentStep();

        Scene scene = new Scene(root, 1420, 900);
        stage.setTitle("Tree Operation Visualizer");
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildMainMenu(Stage stage) {
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

        VBox menu = new VBox(18);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(40));
        menu.setStyle("-fx-background-color: linear-gradient(to bottom, #f6fbff, #dfefff);");
        menu.getChildren().addAll(title, subtitle, checklist);

        for (TreeType type : TreeType.values()) {
            Button button = new Button("Open " + type.getDisplayName());
            button.setPrefWidth(340);
            button.setPrefHeight(50);
            button.setStyle("-fx-font-size: 15px; -fx-background-color: #0f5ea8; -fx-text-fill: white;");
            button.setOnAction(event -> {
                switchTree(type);
                showVisualizer(menu);
            });
            menu.getChildren().add(button);
        }

        Button helpButton = new Button("Help");
        helpButton.setPrefWidth(160);
        helpButton.setOnAction(event -> showHelp());

        Button quitButton = new Button("Quit");
        quitButton.setPrefWidth(160);
        quitButton.setOnAction(event -> stage.close());

        HBox footer = new HBox(10, helpButton, quitButton);
        footer.setAlignment(Pos.CENTER);
        menu.getChildren().add(footer);
        return menu;
    }

    private void configureVisualizer(VBox mainMenu) {
        treeSelector.getItems().setAll(TreeType.values());
        treeSelector.setValue(currentTreeType);
        treeSelector.setOnAction(event -> switchTree(treeSelector.getValue()));

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

        VBox leftPanel = buildControlPanel();
        VBox rightPanel = buildCodePanel();

        BorderPane treePaneHolder = new BorderPane();
        treePaneHolder.setStyle("-fx-background-color: white; -fx-background-radius: 12;");
        treePaneHolder.setPadding(new Insets(12));
        treePaneHolder.setCenter(treeCanvas);

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(event -> showMainMenu(mainMenu));

        Button helpButton = new Button("Help");
        helpButton.setOnAction(event -> showHelp());

        Label title = new Label("Visualizer");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(12, backButton, title, spacer, new Label("Tree Type:"), treeSelector, helpButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 12, 0));

        playPauseButton.setOnAction(event -> togglePlayback());
        stepBackButton.setOnAction(event -> stepBackward());
        stepForwardButton.setOnAction(event -> stepForward());
        progressBar.setPrefWidth(260);

        VBox statusBox = new VBox(6, statusLabel, stepLabel, traceCounterLabel, legendLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        VBox speedBox = new VBox(4, new Label("Animation speed"), speedSlider);
        speedBox.setPrefWidth(180);
        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);
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

        Button createButton = new Button("Create Empty Tree");
        createButton.setMaxWidth(Double.MAX_VALUE);
        createButton.setOnAction(event -> executeOperation(() -> currentModel.create()));

        Button insertButton = new Button("Insert");
        insertButton.setMaxWidth(Double.MAX_VALUE);
        insertButton.setOnAction(event -> {
            Integer value = parseInt(valueField, "Insert value");
            if (value == null) {
                return;
            }
            Integer parent = parseOptionalInt(parentField);
            if (currentTreeType == TreeType.GENERIC && currentModel.snapshot().root() == null && parent != null) {
                validationLabel.setText("Generic Tree is empty: leave Parent value blank so the new value becomes the root.");
                parentField.requestFocus();
                return;
            }
            if (currentTreeType == TreeType.GENERIC && parent == null && currentModel.snapshot().root() != null) {
                validationLabel.setText("Generic Tree insertion needs a parent value when the tree is not empty.");
                return;
            }
            executeOperation(() -> currentModel.insert(parent, value));
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(event -> {
            Integer value = parseInt(valueField, "Delete value");
            if (value != null) {
                executeOperation(() -> currentModel.delete(value));
            }
        });

        Button updateButton = new Button("Update");
        updateButton.setMaxWidth(Double.MAX_VALUE);
        updateButton.setOnAction(event -> {
            Integer currentValue = parseInt(valueField, "Current value");
            Integer nextValue = parseInt(newValueField, "New value");
            if (currentValue != null && nextValue != null) {
                executeOperation(() -> currentModel.update(currentValue, nextValue));
            }
        });

        Button searchButton = new Button("Search");
        searchButton.setMaxWidth(Double.MAX_VALUE);
        searchButton.setOnAction(event -> {
            Integer value = parseInt(valueField, "Search value");
            if (value != null) {
                executeOperation(() -> currentModel.search(value));
            }
        });

        Button traverseButton = new Button("Traverse");
        traverseButton.setMaxWidth(Double.MAX_VALUE);
        traverseButton.setOnAction(event -> executeOperation(() -> currentModel.traverse(traversalSelector.getValue())));

        undoButton.setMaxWidth(Double.MAX_VALUE);
        redoButton.setMaxWidth(Double.MAX_VALUE);
        undoButton.setOnAction(event -> undo());
        redoButton.setOnAction(event -> redo());

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

    private void executeOperation(Supplier<OperationTrace> action) {
        stopPlayback();
        undoStack.push(currentModel.copy());
        redoStack.clear();
        currentTrace = action.get();
        currentStepIndex = 0;
        renderCurrentStep();
        updateUndoRedoButtons();
    }

    private void switchTree(TreeType treeType) {
        if (treeType == null) {
            return;
        }
        stopPlayback();
        currentTreeType = treeType;
        currentModel = switch (treeType) {
            case GENERIC -> new GenericTreeModel();
            case BINARY_SEARCH -> new BinarySearchTreeModel();
            case RED_BLACK -> new RedBlackTreeModel();
        };
        currentTrace = OperationTrace.single(
                treeType.getDisplayName(),
                "New " + treeType.getDisplayName() + " ready. Use the controls to create and manipulate nodes.",
                List.of("Create empty tree", "Insert, delete, update, search, or traverse", "Use step controls to inspect the result"),
                0,
                currentModel.snapshot()
        );
        currentStepIndex = 0;
        undoStack.clear();
        redoStack.clear();
        treeSelector.setValue(treeType);
        refreshInputHints();
        renderCurrentStep();
        updateUndoRedoButtons();
    }

    private void renderCurrentStep() {
        if (currentTrace.steps().isEmpty()) {
            return;
        }
        OperationStep step = currentTrace.steps().get(currentStepIndex);
        statusLabel.setText(step.title());
        stepLabel.setText(step.message());
        traceCounterLabel.setText("Step " + (currentStepIndex + 1) + " / " + currentTrace.steps().size());
        progressBar.setProgress(currentTrace.steps().size() == 1 ? 1 : (double) currentStepIndex / (currentTrace.steps().size() - 1));
        renderPseudoCode(step);
        treeCanvas.render(step.snapshot());
        stepBackButton.setDisable(currentStepIndex == 0);
        stepForwardButton.setDisable(currentStepIndex >= currentTrace.steps().size() - 1);
        playPauseButton.setDisable(currentTrace.steps().size() <= 1);
    }

    private void renderPseudoCode(OperationStep step) {
        codePanel.getChildren().clear();
        List<String> lines = step.pseudocode();
        for (int i = 0; i < lines.size(); i++) {
            Label line = new Label(String.format("%2d. %s", i + 1, lines.get(i)));
            line.setWrapText(true);
            line.setStyle(i == step.activeLine()
                    ? "-fx-background-color: #d7ecff; -fx-padding: 6; -fx-background-radius: 6; -fx-font-weight: bold;"
                    : "-fx-padding: 6;");
            codePanel.getChildren().add(line);
        }
    }

    private void togglePlayback() {
        if (playback != null && playback.getStatus() == Animation.Status.RUNNING) {
            stopPlayback();
            return;
        }
        playback = new Timeline(new KeyFrame(Duration.seconds(speedSlider.getValue()), event -> {
            if (currentStepIndex < currentTrace.steps().size() - 1) {
                currentStepIndex++;
                renderCurrentStep();
            } else {
                stopPlayback();
            }
        }));
        playback.setCycleCount(Timeline.INDEFINITE);
        playback.play();
        playPauseButton.setText("Pause");
    }

    private void stopPlayback() {
        if (playback != null) {
            playback.stop();
            playback = null;
        }
        playPauseButton.setText("Play");
    }

    private void stepForward() {
        stopPlayback();
        if (currentStepIndex < currentTrace.steps().size() - 1) {
            currentStepIndex++;
            renderCurrentStep();
        }
    }

    private void stepBackward() {
        stopPlayback();
        if (currentStepIndex > 0) {
            currentStepIndex--;
            renderCurrentStep();
        }
    }

    private void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        stopPlayback();
        redoStack.push(currentModel.copy());
        currentModel = undoStack.pop();
        currentTreeType = currentModel.type();
        currentTrace = OperationTrace.single("Undo", "Restored the previous tree state.", List.of("Restore previous snapshot", "Render tree"), 1, currentModel.snapshot());
        currentStepIndex = 0;
        treeSelector.setValue(currentTreeType);
        renderCurrentStep();
        updateUndoRedoButtons();
    }

    private void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        stopPlayback();
        undoStack.push(currentModel.copy());
        currentModel = redoStack.pop();
        currentTreeType = currentModel.type();
        currentTrace = OperationTrace.single("Redo", "Reapplied the next tree state.", List.of("Restore next snapshot", "Render tree"), 1, currentModel.snapshot());
        currentStepIndex = 0;
        treeSelector.setValue(currentTreeType);
        renderCurrentStep();
        updateUndoRedoButtons();
    }

    private void updateUndoRedoButtons() {
        undoButton.setDisable(undoStack.isEmpty());
        redoButton.setDisable(redoStack.isEmpty());
    }

    private Integer parseInt(TextField field, String label) {
        try {
            String text = field.getText().trim();
            if (text.isEmpty()) {
                validationLabel.setText(label + " is required.");
                field.requestFocus();
                return null;
            }
            int value = Integer.parseInt(text);
            validationLabel.setText("Ready: " + label + " = " + value + ".");
            return value;
        } catch (RuntimeException ex) {
            validationLabel.setText(label + " must be an integer.");
            field.requestFocus();
            return null;
        }
    }

    private Integer parseOptionalInt(TextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        return parseInt(field, "Parent value");
    }

    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Project guide");
        alert.setContentText(
                "Assignment mapping:\n" +
                "- Main menu lets you choose Generic Tree, Binary Search Tree, or Red-Black Tree.\n" +
                "- Center panel visualizes nodes and edges.\n" +
                "- Right panel highlights algorithm steps.\n" +
                "- Bottom bar provides progress, playback, and manual stepping.\n" +
                "- Undo/Redo restore previous tree states.\n\n" +
                "Input rules:\n" +
                "- Generic Tree insert needs Parent value and Value.\n" +
                "- Binary Search Tree and Red-Black Tree only need Value.\n" +
                "- Update uses Value as the current value and New value as the replacement."
        );
        alert.showAndWait();
    }

    private void refreshInputHints() {
        boolean generic = currentTreeType == TreeType.GENERIC;
        parentField.setDisable(!generic);
        parentField.setOpacity(generic ? 1 : 0.65);
        if (!generic) {
            parentField.setPromptText("Parent ignored");
            validationLabel.setText("Parent value is ignored for " + currentTreeType.getDisplayName() + ".");
            return;
        }

        boolean emptyTree = currentModel.snapshot().root() == null;
        parentField.setPromptText(emptyTree ? "Leave blank for root creation" : "Parent node value");
        validationLabel.setText(emptyTree
                ? "Generic Tree is empty: leave Parent blank and insert the first root value."
                : "Generic Tree rule: Parent is required once the tree already has a root.");
    }

    private void showMainMenu(VBox mainMenu) {
        mainMenu.setVisible(true);
        mainMenu.setManaged(true);
        visualizerView.setVisible(false);
        visualizerView.setManaged(false);
    }

    private void showVisualizer(VBox mainMenu) {
        mainMenu.setVisible(false);
        mainMenu.setManaged(false);
        visualizerView.setVisible(true);
        visualizerView.setManaged(true);
    }

    private static final class TreeCanvas extends Region {
        private final Canvas canvas = new Canvas(760, 640);

        private TreeCanvas() {
            getChildren().add(canvas);
            setMinSize(420, 340);
        }

        void render(TreeSnapshot snapshot) {
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
}
